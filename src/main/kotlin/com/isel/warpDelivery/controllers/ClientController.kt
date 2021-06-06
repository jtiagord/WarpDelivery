package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.DAO.Address
import com.isel.warpDelivery.dataAccess.DAO.Client
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.StateTransition
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.inputmodels.AddressInputModel
import com.isel.warpDelivery.inputmodels.ClientInputModel
import com.isel.warpDelivery.inputmodels.RatingAndRewardInputModel
import isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(CLIENTS_PATH)
class ClientController(val clientMapper: ClientMapper, val deliveryMapper: DeliveryMapper) {

    @PostMapping
    fun addClient(
        req: HttpServletRequest,
        @RequestBody client: ClientInputModel
    ) {
        var clientDao = Client(
            client.username, client.firstName, client.lastName, client.phoneNumber,
            client.email, client.password
        )
        clientMapper.create(clientDao) //TODO: Handle SQL exceptions
    }

    @GetMapping
    fun getClients(req: HttpServletRequest): List<Client> {
        return clientMapper.readAll()
    }

    @GetMapping("/{username}")
    fun getClient(req: HttpServletRequest, @PathVariable username: String): Client {
        return clientMapper.read(username) //TODO: Fix error when client doesn't exist
    }

    @GetMapping("/{username}/addresses")
    fun getClientAddresses(req: HttpServletRequest, @PathVariable username: String): List<Address> {
        return clientMapper.getAddresses(username)
    }

    @PostMapping("/{username}/addresses")
    fun addClientAddress(
        req: HttpServletRequest,
        @PathVariable Username: String,
        @RequestBody addressInfo: AddressInputModel
    ) {
        var addressDao = Address(Username, addressInfo.postalCode, addressInfo.address)
        return clientMapper.addAddress(addressDao) //TODO: HANDLE CLIENT DOESNT EXIST
    }

    @GetMapping("/{username}/addresses/{addressId}")
    fun getClientAddress(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable addressId: Int
    ): Address {
        return clientMapper.getAddress(username, addressId)
    }

    @DeleteMapping("/{username}/addresses/{addressId}")
    fun removeClientAddress(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable addressId: Int
    ) {
        return clientMapper.removeAddress(username, addressId)
    }

    @GetMapping("/{username}/deliveries")
    fun getClientDeliveries(req: HttpServletRequest, @PathVariable username: String): List<Delivery> {
        return deliveryMapper.getClientDeliveries(username)
    }

    @PostMapping("/{username}/deliveries")
    fun addClientDelivery(req: HttpServletRequest, @PathVariable username: String) {
        //TODO: IMPLEMENT
    }

    @GetMapping("/{username}/deliveries/{deliveryId}")
    fun getClientDelivery(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: String
    ): Delivery {
        return deliveryMapper.read(deliveryId)
    }

    @PostMapping("/{username}/deliveries/{deliveryId}")
    fun giveRatingAndReward(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: Int,
        @RequestBody ratingAndReward: RatingAndRewardInputModel
    ) {
        clientMapper.giveRatingAndReward(username, deliveryId, ratingAndReward.rating, ratingAndReward.reward)
        //TODO: Handle wrong inputs
    }

    @GetMapping("/{username}/deliveries/{deliveryId}/transitions")
    fun getStateTransitions(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: Int
    ): List<StateTransition> {
        return clientMapper.getTransitions(deliveryId)
        //TODO: Handle case when there are no transitions
    }
}