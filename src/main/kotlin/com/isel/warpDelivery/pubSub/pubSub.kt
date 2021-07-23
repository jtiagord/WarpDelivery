package com.isel.warpDelivery.pubSub

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.core.ApiFuture
import com.google.cloud.pubsub.v1.Publisher
import com.google.protobuf.ByteString
import com.google.pubsub.v1.PubsubMessage
import com.google.pubsub.v1.TopicName
import com.isel.warpDelivery.model.Location
import java.util.concurrent.TimeUnit


class DeliveryMessage (
    val deliveryId: String,
    val storeLocation : Location,
    val storeAddress : String,
    val storeId : String,
    val deliveryLocation: Location,
    val deliveryAddress : String,
    val deliverySize: String
)

object WarperPublisher {

    const val PROJECT_ID =  "warpdelivery-f2221"
    const val TOPIC_ID = "WarperFinder"

    fun publishDelivery(delivery : DeliveryMessage) {
        val mapper = ObjectMapper()
        val deliveryAsJsonString : String = mapper.writeValueAsString(delivery)
        publishMessage(deliveryAsJsonString)
    }

    private fun publishMessage(message : String) {
        val topicName: TopicName = TopicName.of(PROJECT_ID, TOPIC_ID)
        var publisher: Publisher? = null
        try {
            // Create a publisher instance with default settings bound to the topic
            publisher = Publisher.newBuilder(topicName).build()
            val data = ByteString.copyFromUtf8(message)
            val pubsubMessage: PubsubMessage = PubsubMessage.newBuilder().setData(data).build()

            // Once published, returns a server-assigned message id (unique within the topic)
            val messageIdFuture: ApiFuture<String> = publisher.publish(pubsubMessage)
            val messageId = messageIdFuture.get()
            println("Published message ID: $messageId")
        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown()
                publisher.awaitTermination(1, TimeUnit.MINUTES)
            }
        }
    }
}


