import com.isel.warpDelivery.Common.*
import com.isel.warpDelivery.Model.Warpers
import dataAccess.DAO.Delivery
import dataAccess.DAO.StateTransition
import dataAccess.DAO.Vehicle
import dataAccess.DAO.Warper
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
class WarperController(val warpers : Warpers) {

    //-------------------------------Warper related endpoints-------------------------
    @GetMapping(WARPERS)
    fun getAllWarpers() =
        ResponseEntity
            .ok()
            .body(warpers.getAllWarpers())

    @GetMapping(WARPER)
    fun getWarper(@PathVariable username:String) =
        ResponseEntity
            .ok()
            .body(warpers.getWarper(username))

    @PostMapping(WARPERS)
    fun addWarper(@RequestBody warper: Warper) =
        ResponseEntity
            .status(201)
            .body(warpers.addWarper(warper))

    @DeleteMapping(WARPER)
    fun deleteWarper(@PathVariable username:String) =
        ResponseEntity
            .status(204)
            .body(warpers.deleteWarper(username))

    @PutMapping(WARPER)
    fun updateWarper(@RequestBody warper:Warper)=
        ResponseEntity
            .status(204)
            .body(warpers.updateWarper(warper))

    //-------------------------------------------------------------------------------

    @GetMapping(WARPER_VEHICLE)
    fun getVehicle(@PathVariable username:String,vehicleRegistration:String)=
        ResponseEntity
            .ok()
            .body(warpers.getVehicle(username,vehicleRegistration))

    @GetMapping(WARPER_VEHICLES)
    fun getVehicles(@PathVariable username:String)=
        ResponseEntity
            .ok()
            .body(warpers.getVehicles(username))

    @PostMapping(WARPER_VEHICLES)
    fun addVehicle(@RequestBody vehicle: Vehicle, @PathVariable username: String)=
        ResponseEntity
            .status(201)
            .body(warpers.addVehicle(vehicle))

    @DeleteMapping(WARPER_VEHICLE)
    fun deleteVehicle(@PathVariable username: String,vehicleRegistration:String)=
        ResponseEntity
            .status(204)
            .body(warpers.deleteVehicle(username,vehicleRegistration))

    //-------------------------------------------------------------------------------

    @GetMapping(WARPER_STATE)
    fun getState(@PathVariable username:String)=
        ResponseEntity
            .ok()
            .body(warpers.getState(username))

    @PutMapping(WARPER_STATE)
    fun updateState(@PathVariable state: Warper, @PathVariable username: String)=
        ResponseEntity
            .status(204)
            .body(warpers.updateState(state))
    //-------------------------------------------------------------------------------

    @GetMapping(WARPER_DELIVERIES)
    fun getDeliveries(@PathVariable username:String)=
        ResponseEntity
            .ok()
            .body(warpers.getDeliveries(username))

    @PostMapping(WARPER_DELIVERIES)
    fun addDelivery(@PathVariable delivery: Delivery,@PathVariable username: String)=
        ResponseEntity
            .status(201)
            .body(warpers.addDelivery(delivery))

    @GetMapping(WARPER_DELIVERY)
    fun getDelivery(@PathVariable username:String,@PathVariable delivery_id:Int)=
        ResponseEntity
            .ok()
            .body(warpers.getDelivery(username,delivery_id.toString()))

    @PutMapping(WARPER_DELIVERY)
    fun updateDelivery(@PathVariable username:String,@RequestBody delivery:Delivery)=
        ResponseEntity
            .status(204)
            .body(warpers.updateDelivery(username,delivery))

    //-------------------------------------------------------------------------------

    @GetMapping(WARPER_DELIVERY_TRANSITIONS)
    fun getDeliveryTransitions(@PathVariable username:String)=
        ResponseEntity
            .ok()
            .body(warpers.getDeliveryTransitions(username))

    /*@PostMapping("Warper")
    fun addActiveWarper(@RequestBody warper : RequestActiveWarperInputModel) : String{
        activeWarpers.add(Warper(warper.username,warper.currentLocation))
        return "a"
    }*/
}