package dataAccess.DAO

class Warper (val username : String,
              val firstname : String,
              val lastname : String,
              val phonenumber : String,
              val email : String,
              val password :String?,
              val state:String,
              var vehicles : List<Vehicle>){
}