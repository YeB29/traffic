package id.oversteken.models

import android.location.Location
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageTimer(private val message: Message) {

    private val handler = Handler(Looper.getMainLooper())
    private val stopRunnable = Runnable {
        isTimerRunning = false
    }
    var isTimerRunning = false
        private set

    fun startTimer(location : Location){
        if(!isTimerRunning) {
            isTimerRunning = true
            startSendingMessages(location)
        }
    }

    fun stopTimer(){
        isTimerRunning =false
    }

    private fun startSendingMessages(location : Location){
        CoroutineScope(Dispatchers.IO).launch {
            while(isTimerRunning) {
                message.sendMessage(location.latitude, location.longitude)
            }
        }
    }
}
