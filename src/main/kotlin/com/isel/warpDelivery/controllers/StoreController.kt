package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.STORE_PATH
import com.isel.warpDelivery.common.WARPERS_PATH
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.inputmodels.StoreInputModel
import com.isel.warpDelivery.inputmodels.toDao

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping(STORE_PATH)
class StoreController(val storeMapper : StoreMapper){


    @PostMapping
    fun addWarper(@RequestBody store: StoreInputModel): ResponseEntity<Any> {
        val storeCreated = storeMapper.create(store.toDao())
        return ResponseEntity.created(URI("${STORE_PATH}/$storeCreated")).build()
    }
}


