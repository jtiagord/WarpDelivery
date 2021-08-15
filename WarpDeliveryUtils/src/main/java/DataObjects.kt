data class Store(
    val name : String,
    val postalcode : String,
    val address : String,
    val location : Location,

)

data class Location (val latitude : Double , val longitude : Double)


data class ApiKey (
    val apiKey : String
)

