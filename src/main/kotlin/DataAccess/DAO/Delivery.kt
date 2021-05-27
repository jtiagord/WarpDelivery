package DataAccess.DAO

import java.sql.Timestamp

class Delivery (val deliveryId : Long,
                val clientId : Long,
                var warperId: Long?,
                val state : String,
                val purchaseDate : Timestamp,
                val deliveryDate : Timestamp,
                val rating : Int?,
                val price : Float,
                val type : String,
                var transitions : List<StateTransition>) {

}