import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.Gson
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

const val ISSUER = "WARPDELIVERY"
const val API_URL = "http://localhost:8080"
const val CREATE_STORE_ENDPOINT = "${API_URL}/WarpDelivery/stores"
const val GENERATE_DELIVERY_ENDPOINT = "${API_URL}/WarpDelivery/stores/requestDelivery"
const val HANDLE_DELIVERY_ENDPOINT_TEMPLATE = "${API_URL}/WarpDelivery/stores/deliveries/{deliveryId}/handleDelivery"
const val CANCEL_DELIVERY_ENDPOINT_TEMPLATE = "${API_URL}/WarpDelivery/stores/deliveries/{deliveryId}/cancelDelivery"

data class KeyPair(val publicKey: RSAPublicKey, val privateKey: RSAPrivateKey)

lateinit var publicKey: RSAPublicKey
lateinit var privateKey: RSAPrivateKey

fun main(args : Array<String>){

    val keysFolder = if(args.isEmpty()) System.getenv("WARPDELIVERY_KEYS") else args[0]

    if(keysFolder==null) println("You need to provide a keys folder on the arguments or environment variables")

    publicKey = getPublicKeyFromFile("${keysFolder}/public.pem")
    privateKey = getPrivateKeyFromFile("${keysFolder}/private_key.pem")


    var answer : Int
    do {
        answer = showMenu()
        when(answer){
            1 -> generateAdminToken()
            2 -> createStore()
            3 -> generateDelivery()
            4 -> handleDeliveryToWarper()
            5 -> cancelDelivery()
        }
    }while(answer != 0)
}

fun cancelDelivery() {
    val gson = Gson()


    val apiKey = askFor("ApiKey")

    val deliveryId = askFor("Delivery Id")

    val url = URL(CANCEL_DELIVERY_ENDPOINT_TEMPLATE.replace("{deliveryId}", deliveryId))

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Accept", "application/json")
        setRequestProperty("Authorization", "key=$apiKey")
        doOutput = true
        doInput = true

        println("Response Code : $responseCode")
        when(responseCode) {
            in 200..299-> {
                println("Delivery Handled")
            }
            401->{
                println("Invalid ApiKey")
            }
            in listOf(400,404) -> {
                val response = errorStream.bufferedReader()
                    .use { it.readText() }

                val errorMessage =  gson.fromJson(response, ErrorMessage::class.java)
                println("Error Message : ${errorMessage.errorMessage}")
            }

        }

    }
    println("Press Enter to Proceed")
    readLine()
}

fun handleDeliveryToWarper() {
    val gson = Gson()


    val apiKey = askFor("ApiKey")

    val deliveryId = askFor("Delivery Id")

    val url = URL(HANDLE_DELIVERY_ENDPOINT_TEMPLATE.replace("{deliveryId}", deliveryId))

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Accept", "application/json")
        setRequestProperty("Authorization", "key=$apiKey")
        doOutput = true
        doInput = true

        println("Response Code : $responseCode")
        when(responseCode) {
            in 200..299-> {
                println("Delivery Handled")
            }
            401->{
                println("Invalid ApiKey")
            }
            in listOf(400,404) -> {
                val response = errorStream.bufferedReader()
                    .use { it.readText() }

                val errorMessage =  gson.fromJson(response, ErrorMessage::class.java)
                println("Error Message : ${errorMessage.errorMessage}")
            }

        }

    }
    println("Press Enter to Proceed")
    readLine()
}

fun generateDelivery() {
    val gson = Gson()
    val url = URL(GENERATE_DELIVERY_ENDPOINT)

    val apiKey = askFor("ApiKey")

    val deliveryInfo = askForDeliveryInformation()

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Accept", "application/json")
        setRequestProperty("Authorization", "key=$apiKey")
        doOutput = true
        doInput = true


        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(gson.toJson(deliveryInfo))
        outputStreamWriter.flush()

        println("Response Code : $responseCode")
        when(responseCode) {
            in 200..299-> {

                //The id is in the last part of the location header
                val id = getHeaderField("Location").split("/").last()
                println("Your delivery id : $id")
            }
            401->{
                println("Invalid ApiKey")
            }
            400 -> {
                val response = errorStream.bufferedReader()
                    .use { it.readText() }

                val errorMessage =  gson.fromJson(response, ErrorMessage::class.java)
                println("Error Message : ${errorMessage.errorMessage}")
            }

        }

    }
    println("Press Enter to Proceed")
    readLine()
}

fun askForDeliveryInformation() : DeliveryInfo {
    var userPhone = ""
    while(userPhone.isEmpty()) {
        println("Insert the user phone")
        userPhone = readLine() ?: ""
        if (userPhone.isEmpty()) println("user phone can't be empty")
    }

    var deliverySize : Size? = null
    while(deliverySize == null) {
        println("Insert the Size (small, medium, large)")
        deliverySize = Size.fromText(readLine()?:"")
        if (deliverySize==null) println("Invalid Size")
    }

    var address = ""
    while(address.isEmpty()) {
        println("Insert the address")
        address = readLine() ?: ""
        if (address.isEmpty()) println("Address can't be empty")
    }

    var latitude : Double? = null
    while(latitude == null) {
        println("Insert the latitude")
        latitude = readLine()?.toDoubleOrNull()
        if (latitude == null) println("Invalid latitude")
    }

    var longitude : Double? = null
    while(longitude == null) {
        println("Insert the longitude")
        longitude = readLine()?.toDoubleOrNull()
        if (longitude==null) println("Invalid longitude")
    }

    return DeliveryInfo(userPhone,deliverySize,address,Location(latitude,longitude))
}

fun createStore() {
    val gson = Gson()
    val url = URL(CREATE_STORE_ENDPOINT)

    val store = askForStoreInput()

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "POST"
        setRequestProperty("Content-Type", "application/json")
        setRequestProperty("Accept", "application/json")
        val token = generateAdminToken(false)
        setRequestProperty("Authorization", "Bearer $token")
        doOutput = true
        doInput = true


        val outputStreamWriter = OutputStreamWriter(outputStream)
        outputStreamWriter.write(gson.toJson(store))
        outputStreamWriter.flush()



        println("Response Code : $responseCode")
        if (responseCode in 200..299) {

            //The id is in the last part of the location header
            val id = getHeaderField("Location").split("/").last()

            val response = inputStream.bufferedReader()
                .use { it.readText() }

            val apiKey =  gson.fromJson(response, ApiKey::class.java)

            println("Your store id : $id")
            println("Your store apiKey : ${apiKey.apiKey}")
        }else{
            println("Error creating store")
        }
        println("Press Enter to Proceed")
        readLine()
    }
}


fun askForStoreInput(): Store {
    var storeName = ""
    while(storeName.isEmpty()) {
        println("Insert the store name")
        storeName = readLine() ?: ""
        if (storeName.isEmpty()) println("Store name can't be empty")
    }

    var postalCode = ""
    while(postalCode.isEmpty()) {
        println("Insert the Postal Code")
        postalCode = readLine() ?: ""
        if (storeName.isEmpty()) println("Postal Code can't be empty")
    }

    var address = ""
    while(address.isEmpty()) {
        println("Insert the address")
        address = readLine() ?: ""
        if (address.isEmpty()) println("Address can't be empty")
    }

    var latitude : Double? = null
    while(latitude == null) {
        println("Insert the latitude")
        latitude = readLine()?.toDoubleOrNull()
        if (latitude == null) println("Invalid latitude")
    }

    var longitude : Double? = null
    while(longitude == null) {
        println("Insert the longitude")
        longitude = readLine()?.toDoubleOrNull()
        if (longitude==null) println("Invalid longitude")
    }

    return Store(name = storeName,  postalcode = postalCode, address = address,
        Location(latitude = latitude, longitude = longitude))
}

fun askFor(askFor: String) : String{
    while(true) {
        println("Insert the ${askFor}")
        val apikey = readLine()
        if (apikey?.isNotEmpty() == true) return apikey.trim()
    }
}

fun generateAdminToken(printToken : Boolean = true) : String {
    println("Generating admin token")

    val token = JWT.create().withIssuer(ISSUER)
        .withClaim("id", "ADMIN")
        .withClaim("usertype", "ADMIN")
        .withIssuedAt(Date(System.currentTimeMillis()))
        .sign(Algorithm.RSA256(publicKey,privateKey))
    if(printToken) {
        println("Your admin token : ")
        println(token)
        println("Press Enter to Proceed")
        readLine()
    }

    return token

}

fun getPublicKeyFromFile(filename : String): RSAPublicKey {
    val file = File(filename)
    val key = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())

    val publicKeyPEM = key
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PUBLIC KEY-----", "")


    val decoder = Base64.getDecoder()
    val encoded = decoder.decode(publicKeyPEM)


    val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
    val keySpec = X509EncodedKeySpec(encoded)
    return keyFactory.generatePublic(keySpec) as RSAPublicKey
}

fun getPrivateKeyFromFile(filename : String): RSAPrivateKey {
    val file = File(filename)
    val key = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())



    val privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PRIVATE KEY-----", "")


    val decoder = Base64.getDecoder()
    val encoded = decoder.decode(privateKeyPEM)

    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = PKCS8EncodedKeySpec(encoded)
    return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
}

fun showMenu() : Int {
    println("1 - Generate Admin Token")
    println("2 - Create a store")
    println("3 - Generate a delivery for a store")
    println("4 - Handle Delivery to Warper")
    println("5 - Cancel Delivery")
    println("0 - Exit")
    print("Choose an option : ")

    return readLine()?.toIntOrNull()?: -1
}