package id.oversteken.models

import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.androidfactory.network.KtorClient
import id.oversteken.service.ForeGroundLocationService

object LocationTimer {

    private val handler = Handler(Looper.getMainLooper())
    private val GEO_ZONE_TIMEOUT = 10 * 60 * 1000L // 10 minutes in milliseconds
    var isSendingMessages: Boolean = false  // Flag to track if messages are being sent
    var lastKnownTimeoutLocation: Location = Location(null) // To track the last known location of the user
    val ktorClient = KtorClient("http://10.0.2.2:8080/start")


    private var geoZoneTimeoutRunnable: Runnable = Runnable {
        Log.d(
            this@LocationTimer::class.simpleName,
            "User has been in radius for a long time, disabling message sending."
        )

        lastKnownTimeoutLocation = ForeGroundLocationService.userLocationState.value.location

        stopMessageSending() // Stop sending messages when geo zone timeout is triggered
    }


    private val sendMessageRunnable: Runnable = object : Runnable {
        override fun run() {
            Log.d(this@LocationTimer::class.simpleName, "Sending Message")

            val longitude = ForeGroundLocationService.userLocationState.value.location.longitude
            val latitude = ForeGroundLocationService.userLocationState.value.location.latitude

            // Create new message and send
            Message(ktorClient).sendMessage(latitude, longitude)

            // Schedule the next message
            handler.postDelayed(this, 5000)
        }
    }


    fun startMessageSending() {
        // Realistically this shouldn't be called when isMessageSending is true
        if (isSendingMessages) return

        Log.d(this::class.simpleName, "Start Message Sending")
        isSendingMessages = true

        // start recursive message sending function
        handler.post(sendMessageRunnable)

        // Set timeout to check if user hasn't moved for a while
        handler.postDelayed(geoZoneTimeoutRunnable, GEO_ZONE_TIMEOUT)

    }

    fun stopMessageSending() {
        // Realistically this shouldn't be called when isMessageSending is false
        if (!isSendingMessages) return

        Log.d(this@LocationTimer::class.simpleName, "Stop Sending Message")

        isSendingMessages = false
        Message(ktorClient).stopMessage()

        // stop scheduled messages
        handler.removeCallbacks(sendMessageRunnable)
        handler.removeCallbacks(geoZoneTimeoutRunnable)
    }
}