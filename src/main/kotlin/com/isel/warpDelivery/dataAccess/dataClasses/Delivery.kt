package com.isel.warpDelivery.dataAccess.dataClasses

import com.isel.warpDelivery.inputmodels.Size
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.IllegalStateException
import java.sql.ResultSet
import java.sql.Timestamp

enum class DeliveryState(val text : String) {
    LOOKING_FOR_WARPER("Looking for warper"),
    DELIVERING("Delivering"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromText(text: String): DeliveryState? {
            for (value in values()) {
                if (value.text.equals(text, ignoreCase = true)) {
                    return value
                }
            }
            return null
        }
    }
}

class Delivery (val deliveryId : String?,
                val warperUsername: String?,
                val storeId: String,
                val state : DeliveryState,
                val clientPhone: String,
                val purchaseDate : Timestamp?,
                var deliverDate : Timestamp?,
                var rating : Int?,
                val deliverLatitude: Double,
                val deliverLongitude: Double,
                val deliverAddress: String,
                var reward: Float?,
                val type : Size,
                var transitions : List<StateTransition>?)


data class DeliveryEdit(
    val warperUsername: String?,
    val state : String?,
    val rating : Int ?,
    val reward: Float ?
)

/*class DeliveryRowMapper : RowMapper<Delivery> {
    override fun map(rs: ResultSet, ctx: StatementContext): Delivery {
        return Delivery(
            deliveryId = rs.getString("deliveryid"),
            warperUsername = rs.getString("warperusername"),
            storeId = rs.getString("storeid"),
            state = DeliveryState.fromText(rs.getString("state"))!!,
            clientPhone = rs.getString("clientphone"),
            purchaseDate = rs.getTimestamp("purchasedate") ,
            deliverDate = rs.getTimestamp("deliverdate"),
            deliverLatitude = rs.getDouble("deliverLatitude"),
            deliverLongitude = rs.getDouble("deliverLongitude"),
            deliverAddress = rs.getString("deliveryAddress"),
            reward = ctx.
        )
    }

}*/


class DeliveryStateColumnMapper : ColumnMapper<DeliveryState>{
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext): DeliveryState {
        return DeliveryState.fromText(r.getString(columnNumber)) ?:
            throw IllegalStateException("DeliveryState has an invalid value")
    }
}
