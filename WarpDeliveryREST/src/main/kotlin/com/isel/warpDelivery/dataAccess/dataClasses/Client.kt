package com.isel.warpDelivery.dataAccess.dataClasses

class Client(   val username : String,
                val firstname : String,
                val lastname : String,
                val phonenumber : String,
                val email : String,
                val password : String?,
                var addresses : List<Address> = emptyList())
