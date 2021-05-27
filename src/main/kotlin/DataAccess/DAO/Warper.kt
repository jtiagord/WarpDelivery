package DataAccess.DAO

class Warper (val username : String,
              val firstname : String,
              val lastname : String,
              val phonenumber : String,
              val email : String,
              val password :String?,
              var vehicles : List<Vehicle>){
}