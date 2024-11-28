package id.oversteken.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.androidfactory.network.KtorClient
import id.oversteken.R
import id.oversteken.models.ForeGroundLocationState
import id.oversteken.models.Geofencing
import id.oversteken.models.LocationTimer
import id.oversteken.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ForeGroundLocationService : Service(), LocationListener {
    companion object {
        private val NOTIFICATION_CHANNEL_ID = "LOCATION"
        private val NOTIFICATION_ID = 1
        private val GEO_ZONE_RADIUS = 200

        private val _userLocationState = MutableStateFlow(ForeGroundLocationState())
        val userLocationState = _userLocationState.asStateFlow()
    }

    private lateinit var locationManager: LocationManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var defaultNotificationBuilder: NotificationCompat.Builder

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val ktorClient = KtorClient("http://10.0.2.2:8080/request")
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // create default notification
        defaultNotificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.location_notification_default_title))
            .setContentText(getString(R.string.location_notification_default_description))
            .setSmallIcon(R.drawable.ic_stat_name)
            .setOngoing(true)
            .setSilent(true)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setOnlyAlertOnce(true)
        // IF API level is higher than 31 also set foreground service behavior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            defaultNotificationBuilder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
        }

        // start foreground with default Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification())

        }

        requestLocationUpdates()

        _userLocationState.update {
            it.copy(serviceRunning = true)
        }

        return START_STICKY
    }

    private fun requestLocationUpdates() {
        // if user hasn't granted the right permission stop service
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) stopSelf()

        // request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, -1, 0f, this)
    }

    private fun createNotification(notification: NotificationCompat.Builder = defaultNotificationBuilder): Notification {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.location_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        return notification.build()
    }

    override fun onDestroy() {
        super.onDestroy()

        LocationTimer.stopMessageSending()
        locationManager.removeUpdates(this)
        _userLocationState.update {
            it.copy(serviceRunning = false)
        }
    }

    override fun onLocationChanged(location: Location) {
        val isInGeoZone = Geofencing.isUserInGeoZone(
            context = baseContext,
            userLocation = location,
            radius = GEO_ZONE_RADIUS
        )


        if (_userLocationState.value.isInGeoZone != isInGeoZone) {
            notificationManager.notify(
                NOTIFICATION_ID,
                createNotification(defaultNotificationBuilder.run {
                    setContentText(
                        if (isInGeoZone) {
                            getText(R.string.location_notification_description_in_radius)
                        } else {
                            getText(R.string.location_notification_default_description)
                        }
                    )
                })
            )
        }

        _userLocationState.update {
            it.copy(
                isInGeoZone = isInGeoZone,
                location = location,
            )
        }


        if (isInGeoZone) {
            // If already in geozone/sending messages and further away than 10 meters from the last timeout location start timer.
            if (!LocationTimer.isSendingMessages && location.distanceTo(LocationTimer.lastKnownTimeoutLocation) > 10) LocationTimer.startMessageSending()

            // if user is no longer in geozone stop sending messages
        } else if (LocationTimer.isSendingMessages) {
            LocationTimer.stopMessageSending()
        }

    }


    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)
        requestLocationUpdates()
    }

    @Deprecated("Ignore")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderDisabled(provider: String) {}
}
