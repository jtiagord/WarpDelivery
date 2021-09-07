package com.isel.warpDelivery.model

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component

@Component
class NotificationSystem {

    fun sendNotification(warper : ActiveWarper){
        if(warper.token == null) return
        val message: Message = Message.builder()
            .putData("latitude", warper.location.latitude.toString())
            .putData("longitude", warper.location.latitude.toString())
            .setNotification(
                Notification.builder()
                    .setTitle("New Delivery")
                    .setBody("You have a new delivery")
                    .build())
            .setToken(warper.token)
            .build()


        val response = FirebaseMessaging.getInstance().send(message)
        println("Successfully sent message: $response")
    }
}