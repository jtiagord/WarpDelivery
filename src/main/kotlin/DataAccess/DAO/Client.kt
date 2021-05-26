package DataAccess.DAO

class Client(   val username : String,
                val firstname : String,
                val lastname : String,
                val phonenumber : String,
                val email : String,
                val password : String?,
                val addresses : List<Address> = emptyList())
{
}