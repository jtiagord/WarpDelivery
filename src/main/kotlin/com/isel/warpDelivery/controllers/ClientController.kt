package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.DAO.Address
import com.isel.warpDelivery.dataAccess.DAO.Client
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.StateTransition
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.exceptionHandler.ProblemJsonModel
import com.isel.warpDelivery.inputmodels.AddressInputModel
import com.isel.warpDelivery.inputmodels.ClientInputModel
import com.isel.warpDelivery.inputmodels.RatingAndRewardInputModel
import com.isel.warpDelivery.inputmodels.toAddress
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(CLIENTS_PATH)
class ClientController(val clientMapper: ClientMapper, val deliveryMapper: DeliveryMapper) {

    @PostMapping
    fun addClient(
        req: HttpServletRequest,
        @RequestBody client: ClientInputModel
    ) {
        val clientDao = Client(
            client.username, client.firstName, client.lastName, client.phoneNumber,
            client.email, client.password
        )
        clientMapper.create(clientDao)
    }

    @GetMapping
    fun getClients(req: HttpServletRequest): List<Client> {
        return clientMapper.readAll()
    }

    @GetMapping("/{username}")
    fun getClient(req: HttpServletRequest, @PathVariable username: String): Client {
        return clientMapper.read(username) ?: throw ApiException("User ${username} not found", HttpStatus.NOT_FOUND)
    }

    @GetMapping("/{username}/addresses")
    fun getClientAddresses(req: HttpServletRequest, @PathVariable username: String): List<Address> {
        return clientMapper.getAddresses(username)
    }

    @PostMapping("/{username}/addresses")
    fun addClientAddress(
        req: HttpServletRequest,
        @PathVariable username: String,
        @RequestBody addressInfo: AddressInputModel
    ) {
        val addressDao = addressInfo.toAddress(username)
        return clientMapper.addAddress(addressDao)
    }

    @DeleteMapping("/{username}/addresses/{addressId}")
    fun removeClientAddress(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable addressId: Long
    ){
        return clientMapper.removeAddress(username, addressId)
    }

    @GetMapping("/{username}/deliveries")
    fun getClientDeliveries(req: HttpServletRequest, @PathVariable username: String): List<Delivery> {
        return deliveryMapper.getDeliveriesByUsername(username)
    }

    @PostMapping("/{username}/deliveries")
    fun addClientDelivery(req: HttpServletRequest, @PathVariable username: String) {
        //TODO: IMPLEMENT
    }

    @GetMapping("/{username}/deliveries/{deliveryId}")
    fun getClientDelivery(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: Long
    ): Delivery {
        return deliveryMapper.read(deliveryId) ?: throw ApiException("The delivery: $deliveryId doesn't exist", HttpStatus.NOT_FOUND)
    }

    @PostMapping("/{username}/deliveries/{deliveryId}")
    fun giveRatingAndReward(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: Long,
        @RequestBody ratingAndReward: RatingAndRewardInputModel
    ) {
        clientMapper.giveRatingAndReward(username, deliveryId, ratingAndReward.rating, ratingAndReward.reward)
    }

    @GetMapping("/{username}/deliveries/{deliveryId}/transitions")
    fun getStateTransitions(
        req: HttpServletRequest,
        @PathVariable username: String,
        @PathVariable deliveryId: Long
    ): List<StateTransition> {
        return deliveryMapper.getTransitions(deliveryId)
    }

    //Exception Handlers
    @ExceptionHandler(ClientMapper.UserAlreadyExistsException::class,
        ClientMapper.PhoneAlreadyExistsException::class,
        ClientMapper.EmailAlreadyExistsException::class
    )
    fun handleAlreadyExistsException(ex : Exception) = ResponseEntity
        .status(400)
        .contentType(ProblemJsonModel.MEDIA_TYPE)
        .body(
            ProblemJsonModel(
            detail = ex.message,
            type = URI("/probs/resource-already-exists")
            )
        )


    @ExceptionHandler(ClientMapper.UserNotFoundException::class, ClientMapper.AddressNotFoundException::class)
    fun handleNotFoundException(ex : Exception) = ResponseEntity
        .status(404)
        .contentType(ProblemJsonModel.MEDIA_TYPE)
        .body(
            ProblemJsonModel(
                detail = ex.message,
                type = URI("/probs/resource-doesnt-exists")
            )
        )

    @ExceptionHandler(ClientMapper.UserNotAllowedException::class)
    fun handleNotAllowedException(ex : Exception) = ResponseEntity
        .status(401)
        .contentType(ProblemJsonModel.MEDIA_TYPE)
        .body(
            ProblemJsonModel(
                detail = ex.message,
                type = URI("/probs/user-has-no-permissions")
            )
        )
}