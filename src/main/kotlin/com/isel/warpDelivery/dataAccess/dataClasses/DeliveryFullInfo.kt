package com.isel.warpDelivery.dataAccess.dataClasses

import com.isel.warpDelivery.inputmodels.Size
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.lang.IllegalStateException
import java.sql.ResultSet
import java.sql.Timestamp

class DeliveryFullInfo (
    val deliveryId : String,
    val warper : WarperInfo?,
    val store: StoreInfo,
    val state : DeliveryState,
    val purchaseDate : Timestamp?,
    var deliverDate : Timestamp?,
    val deliverAddress: String,
    val clientPhone : String,
    val type : Size
)

data class WarperInfo (
    val firstname: String,
    val lastname: String,
    val phonenumber: String,
    val email: String,
)

data class StoreInfo(
    val name : String,
    val postalcode : String,
    val address : String
)

class DeliveryFullInfoMapper : RowMapper<DeliveryFullInfo> {
    override fun map(rs: ResultSet, ctx: StatementContext): DeliveryFullInfo {
        val warper = rs.getString("warperUsername")
        var warperInfo : WarperInfo? = null
        if(warper!= null){
            warperInfo = WarperInfo(rs.getString("firstname"),rs.getString("lastname"),
                rs.getString("phonenumber"),
                rs.getString("email"))
        }

        val storeInfo = StoreInfo(rs.getString("name"), rs.getString("address"),
                                    rs.getString("address"))

        return DeliveryFullInfo(rs.getString("deliveryId"),
                        warper= warperInfo,
                        store= storeInfo,
                        state = DeliveryState.fromText(rs.getString("state"))?:DeliveryState.DELIVERED,
                        purchaseDate = rs.getTimestamp("purchaseDate"),
                        deliverDate = rs.getTimestamp("deliverDate"),
                        deliverAddress = rs.getString("deliverAddress"),
                        clientPhone = rs.getString("clientPhone"),
                        type = Size.fromText(rs.getString("type"))?: Size.SMALL
        )

    }

}