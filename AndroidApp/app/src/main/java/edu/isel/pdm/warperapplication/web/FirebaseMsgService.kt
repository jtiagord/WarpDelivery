package edu.isel.pdm.warperapplication.web

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.isel.pdm.warperapplication.R

class FirebaseMsgService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "CHANNEL"
        private var notificationChannel: NotificationChannel? = null
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("MESSAGE", message.from!!)

        if(message.data.isNotEmpty()){
            Log.d("MESSAGE", "DATA:" + message.data)
            sendNotification(message.data.toString())
        }
    }

    private fun sendNotification(msg: String){

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(notificationChannel == null)
            createNotificationChannel(notificationManager)

        val defaultSoundUri = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WARPDELIVERY")
            .setContentText(msg)
            .setSound(defaultSoundUri)
            .setSmallIcon(R.drawable.ic_vehicle)

        notificationManager.notify(0, notificationBuilder.build())
    }
}