package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.DAO.Address
import com.isel.warpDelivery.dataAccess.DAO.Client
import com.isel.warpDelivery.inputmodels.ClientInputModel
import isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
class ClientController(val clientMapper: ClientMapper) {

    @PostMapping(CLIENTS)
    fun addClient(
        req: HttpServletRequest,
        @RequestBody client : ClientInputModel
    ) {
       clientMapper.addClient(client) //TODO: Handle SQL exceptions
    }

   @GetMapping(CLIENTS)
    fun getClients(req: HttpServletRequest): List<Client> {
       return clientMapper.readAll()
   }

    @GetMapping(CLIENT)
    fun getClient(req: HttpServletRequest, @PathVariable Username: String): Client {
        return clientMapper.read(Username) //TODO: Fix error when client doesn't exist
    }

    @GetMapping(CLIENT_ADDRESSES)
    fun getClientAddresses(req: HttpServletRequest, @PathVariable Username: String): List<Address> {
        return clientMapper.getAddresses(Username)
    }

    @PostMapping(CLIENT_ADDRESSES)
    fun addClientAddress(req: HttpServletRequest, @PathVariable Username: String) {
        //TODO: IMPLEMENT
    }

    @GetMapping(CLIENT_ADDRESS)
    fun getClientAddress(req: HttpServletRequest,
                         @PathVariable Username: String,
                         @PathVariable AddressId: Int): Address {
        return clientMapper.getAddress(Username, AddressId)
    }

    @DeleteMapping(CLIENT_ADDRESS)
    fun removeClientAddress(req: HttpServletRequest, @PathVariable Username: String, @PathVariable AddressId: Int) {
        return clientMapper.removeAddress(Username, AddressId)
    }

    @GetMapping(CLIENT_DELIVERIES)
    fun getClientDeliveries(req: HttpServletRequest, @PathVariable Username: String) {
        //TODO: IMPLEMENT
    }

    @PostMapping(CLIENT_DELIVERIES)
    fun addClientDelivery(req: HttpServletRequest, @PathVariable Username: String) {
        //TODO: IMPLEMENT
    }

    @GetMapping(CLIENT_DELIVERY)
    fun getClientDelivery(req: HttpServletRequest, @PathVariable Username: String, @PathVariable DeliveryId: String) {
        //TODO: IMPLEMENT
    }

    @PostMapping(CLIENT_DELIVERY)
    fun giveRatingAndReward(req: HttpServletRequest,
                            @PathVariable Username: String,
                            @PathVariable DeliveryId: String,
                            @RequestParam Rating: Int,
                            @RequestParam Reward: Float,
    ) {
        //TODO: IMPLEMENT
    }

    @GetMapping(CLIENT_DELIVERY_TRANSITIONS)
    fun getStateTransitions(req: HttpServletRequest, @PathVariable Username: String, @PathVariable DeliveryId: String) {
        //TODO: IMPLEMENT
    }


}