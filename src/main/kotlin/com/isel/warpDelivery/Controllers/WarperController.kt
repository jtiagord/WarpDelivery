import isel.warpDelivery.inputmodels.RequestActiveWarperInputModel
import isel.warpDelivery.model.Warper
import isel.warpDelivery.model.WarperList
import org.jdbi.v3.core.Jdbi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class WarperController(val activeWarpers : WarperList, val jdbi : Jdbi) {

    @PostMapping("Warper")
    fun addActiveWarper(@RequestBody warper : RequestActiveWarperInputModel) : String{
        activeWarpers.add(Warper(warper.username,warper.currentLocation))
        return "a"
    }
}