import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Subscriber
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.gson.Gson
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.PubsubMessage
import utils.ActiveWarperRepository
import utils.Size
import utils.sendNotification

lateinit var warperRepository : ActiveWarperRepository

fun main() {

    val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()
    FirebaseApp.initializeApp(options)
    warperRepository = ActiveWarperRepository()
    val projectId = "warpdelivery-f2221"
    val subscriptionId = "WarperFinder-sub"
    subscribeAsyncExample(projectId, subscriptionId)
}

fun subscribeAsyncExample(projectId: String?, subscriptionId: String?) {
    val subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId)

    // Instantiate an asynchronous message receiver.
    val receiver = MessageReceiver { message: PubsubMessage, consumer: AckReplyConsumer ->
        val data =  message.data.toStringUtf8()
        val deliveryMessage = parseData(data)
        val warper = warperRepository
                                .getClosest(deliveryMessage.storeLocation,
                                Size.fromText(deliveryMessage.deliverySize)!!)

        if(warper != null){
            warperRepository.setWarperForDelivery(warper,deliveryMessage.getDelivery())
            sendNotification(warper)
        }else{
            warperRepository.setPendingDelivery(deliveryMessage.getDelivery())
        }

        println("WARPER FOUND : ${warper?.username?:"NOT FOUND"}")

        consumer.ack()
    }
    val subscriber: Subscriber =
        Subscriber.newBuilder(subscriptionName, receiver).build()

    // Start the subscriber.
    subscriber.startAsync().awaitRunning()
    System.out.printf("Listening for messages on %s:\n", subscriptionName.toString())
    // Allow the subscriber to run for 30s unless an unrecoverable error occurs.
    subscriber.awaitTerminated()
}

fun parseData(data: String): DeliveryMessage {
    val mapper = Gson()
    return mapper.fromJson(data, DeliveryMessage::class.java);
}
