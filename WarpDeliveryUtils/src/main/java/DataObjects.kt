data class Store(
    val name : String,
    val postalcode : String,
    val address : String,
    val location : Location,

)

enum class Size(val text: String) {
    SMALL("small"), MEDIUM("medium"), LARGE("large");

    companion object {
        fun fromText(text: String): Size?{
            for (value in values()) {
                if (value.text.equals(text,ignoreCase = true)) {
                    return value
                }
            }
            return null
        }
    }
}


data class DeliveryInfo(
    val userPhone : String,
    val deliverySize : Size,
    val address : String,
    val deliveryLocation : Location
)

data class Location (val latitude : Double , val longitude : Double)


data class ApiKey (
    val apiKey : String
)

data class ErrorMessage(
    val errorMessage : String
)



