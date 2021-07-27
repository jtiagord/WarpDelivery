package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.authentication.USER_ATTRIBUTE_KEY
import com.isel.warpDelivery.authentication.UserInfo
import com.isel.warpDelivery.authentication.WarperResource
import com.isel.warpDelivery.common.STORE_PATH
import com.isel.warpDelivery.common.WARPERS_PATH
import com.isel.warpDelivery.dataAccess.mappers.DeliveryState
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.inputmodels.StoreInputModel
import com.isel.warpDelivery.inputmodels.toDao

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(STORE_PATH)
class StoreController(val storeMapper : StoreMapper){

    @PostMapping
    fun addStore(@RequestBody store: StoreInputModel): ResponseEntity<Any> {
        val uuid = UUID.randomUUID().toString()
        val apiKey = uuid.replace("-","")
        val storeDao = store.toDao(apiKey)
        val storeCreated = storeMapper.create(storeDao)

        return ResponseEntity.created(URI("${STORE_PATH}/$storeCreated")).body(mapOf("apiKey" to apiKey))
    }

}





