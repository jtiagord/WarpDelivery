package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import edu.isel.pdm.warperapplication.viewModels.WarperState
import edu.isel.pdm.warperapplication.web.entities.DeliveryFullInfo
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.util.TileSystem
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

import kotlin.collections.ArrayList

class LocationFragment : Fragment() {

    private val viewModel: LocationViewModel by viewModels()

    companion object {
        private const val userAgent = "OsmNavigator/2.4"
        private lateinit var roadManager: RoadManager
    }

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private var roadOverlay: Polyline? = null
    private var pickupMarker: Marker? = null
    private var deliveryMarker: Marker? = null
    private var vehicleSelectionDialog: AlertDialog? = null
    private var deliveryInfoDialog: AlertDialog? = null
    private var locationOverlay: MyLocationNewOverlay? = null

    private lateinit var activeBtn: Button
    private lateinit var inactiveBtn: ExtendedFloatingActionButton
    private lateinit var finishBtn: ExtendedFloatingActionButton
    private lateinit var revokeBtn: ExtendedFloatingActionButton
    private lateinit var infoBtn: ExtendedFloatingActionButton
    private lateinit var centerBtn: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_location, container, false)

        activeBtn = rootView.findViewById(R.id.btn_active)
        inactiveBtn = rootView.findViewById(R.id.btn_inactive)
        finishBtn = rootView.findViewById(R.id.btn_finish)
        revokeBtn = rootView.findViewById(R.id.btn_revoke)
        infoBtn = rootView.findViewById(R.id.btn_deliveryInfo)
        centerBtn = rootView.findViewById(R.id.btn_center)
        initButtons()

        map = rootView.findViewById(R.id.map)
        map.isVisible = false
        mapController = map.controller


        //Init road manager
        roadManager = OSRMRoadManager(context, userAgent)
        val manager = roadManager as OSRMRoadManager
        manager.setMean(OSRMRoadManager.MEAN_BY_CAR)

        viewModel.initFirestore()

        viewModel.deliveryLocation.observe(viewLifecycleOwner, { point ->
            val state = viewModel.state.value
            if (state == WarperState.RETRIEVING || state == WarperState.DELIVERING) {
                getRouteAsync(state)
                inactiveBtn.isVisible = false
            }
        })

        //Observe state changes and draw UI according to warper state
        viewModel.state.observe(viewLifecycleOwner, {
            when (it) {
                WarperState.INACTIVE -> setInactiveUI()
                WarperState.LOOKING_FOR_DELIVERY -> setSearchingUI()
                WarperState.RETRIEVING -> setRetrievingUI()
                WarperState.DELIVERING -> setDeliveringUI()
                else -> setInactiveUI()
            }
        })

        //Displays the vehicle selection dialog after vehicles are obtained from the API
        viewModel.vehicleIds.observe(viewLifecycleOwner, {
            if (it == null)
                return@observe
            if (it.isEmpty())
                Toast.makeText(context, getString(R.string.no_vehicles_error), Toast.LENGTH_LONG)
                    .show()
            else if (viewModel.state.value == WarperState.INACTIVE)
                showVehicleSelectionDialog()
        })

        // Inflate the layout for this fragment
        return rootView
    }

    private fun setInactiveUI() {
        //Display UI for inactive state
        if (pickupMarker != null) {
            pickupMarker!!.setVisible(false)
            pickupMarker = null
        }

        if (deliveryMarker != null) {
            deliveryMarker!!.setVisible(false)
            deliveryMarker = null
        }

        activeBtn.isVisible = true
        inactiveBtn.isVisible = false
        finishBtn.isVisible = false
        revokeBtn.isVisible = false
        infoBtn.isVisible = false
        centerBtn.isVisible = false

        map.isVisible = false

    }

    private fun setSearchingUI() {
        initMap(map)

        activeBtn.isVisible = false
        inactiveBtn.isVisible = true
        finishBtn.isVisible = false
        revokeBtn.isVisible = false
        infoBtn.isVisible = false
        centerBtn.isVisible = true

        map.isVisible = true
    }

    private fun setRetrievingUI() {
        initMap(map)

        activeBtn.isVisible = false
        inactiveBtn.isVisible = false
        finishBtn.isVisible = false
        revokeBtn.isVisible = true
        infoBtn.isVisible = true
        centerBtn.isVisible = true

        map.isVisible = true
    }

    private fun setDeliveringUI() {
        initMap(map)

        activeBtn.isVisible = false
        inactiveBtn.isVisible = false
        revokeBtn.isVisible = false
        infoBtn.isVisible = true
        centerBtn.isVisible = true

        map.isVisible = true
    }

    private fun disableTemporarily(button: Button) {
        button.isEnabled = false
        button.postDelayed({ button.isEnabled = true }, 1000)
    }

    private fun initButtons() {
        activeBtn.setOnClickListener {
            disableTemporarily(activeBtn)
            viewModel.getVehicles()
        }

        inactiveBtn.setOnClickListener {
            disableTemporarily(inactiveBtn)
            viewModel.setInactive()
        }

        finishBtn.setOnClickListener {
            disableTemporarily(finishBtn)
            viewModel.confirmDelivery()
        }

        revokeBtn.setOnClickListener {
            disableTemporarily(revokeBtn)
            viewModel.revokeDelivery()
        }

        infoBtn.setOnClickListener {
            disableTemporarily(infoBtn)
            viewModel.getDeliveryInfo { delivery ->
                showDeliveryInfoDialog(delivery)
            }
        }

        centerBtn.setOnClickListener {

            val location = viewModel.currentLocation.value
            if (location != null)
                mapController.animateTo(location.toGeoPoint())
        }
    }

    fun onNewLocation(location: Location) {

        val oldLocation = viewModel.currentLocation.value
        val newLocation = LocationEntity(location.latitude, location.longitude)
        val deliveryLocation = viewModel.deliveryLocation.value

        //Enable finish button if distance to delivery point is less than 17m
        if (viewModel.state.value == WarperState.DELIVERING &&
            deliveryLocation != null &&
            newLocation.getDistance(deliveryLocation.toLocation()) < 17
        ) {
            finishBtn.isVisible = true
            Log.v("DELIVERY", "AT POINT")
        }

        //Get and updated path if the warper has moved more than 2m

        if (oldLocation != null &&
            newLocation.getDistance(oldLocation) > 2 && deliveryLocation != null
        ) {
            getRouteAsync(viewModel.state.value!!)
        }


        //Send a location update to the API
        viewModel.updateCurrentLocation(newLocation)
    }

    private fun initMap(map: MapView) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isTilesScaledToDpi = true
        map.minZoomLevel = 1.0
        map.maxZoomLevel = 21.0
        map.setScrollableAreaLimitLatitude(TileSystem.MaxLatitude, -TileSystem.MaxLatitude, 0)
        map.isVerticalMapRepetitionEnabled = false
        map.setMultiTouchControls(true)

        //Add user location overlay
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        map.overlays.add(locationOverlay)
        locationOverlay!!.enableMyLocation()
        locationOverlay!!.enableFollowLocation()

        //Add markers if needed
        if (deliveryMarker != null)
            map.overlays.add(deliveryMarker)

        if (pickupMarker != null)
            map.overlays.add(pickupMarker)

        map.invalidate()
    }


    private fun showVehicleSelectionDialog() {
        val alertDialog = AlertDialog.Builder(context)
        val vehiclesList = viewModel.vehicleIds.value!!

        var selectedItem = 0
        alertDialog.setTitle(R.string.select_vehicle)
            .setSingleChoiceItems(vehiclesList, 0) { _, which ->
                selectedItem = which
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.setActive(vehiclesList[selectedItem])
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                viewModel.vehicleIds.postValue(null)
                dialog.cancel()
            }
        vehicleSelectionDialog = alertDialog.show()
    }

    private fun getRouteAsync(state: WarperState) {
        val waypoints = ArrayList<GeoPoint>()

        val currLoc = viewModel.currentLocation.value ?: LocationEntity(0.0, 0.0)
        val currGeoPoint = currLoc.toGeoPoint()

        val pickupGeoPoint = viewModel.pickupLocation.value!!
        val deliveryGeoPoint = viewModel.deliveryLocation.value!!

        if (state == WarperState.RETRIEVING)
            waypoints.add(pickupGeoPoint)

        if (state == WarperState.DELIVERING)
            waypoints.add(deliveryGeoPoint)

        waypoints.add(currGeoPoint)

        //Obtain the road in a new thread
        lifecycleScope.launch(Dispatchers.IO) {
            val newRoad = roadManager.getRoad(waypoints)


            lifecycleScope.launch(Dispatchers.Main) {
                updateUIWithRoadAndMarker(newRoad, pickupGeoPoint, deliveryGeoPoint)
            }
        }
    }

    private fun updateUIWithRoadAndMarker(
        road: Road,
        pickupGeoPoint: GeoPoint,
        deliveryGeoPoint: GeoPoint
    ) {

        //Don't proceed with UI update if the fragment has been destroyed
        if (this.isRemoving || this.isDetached || !this.isAdded)
            return

        //Remove old road overlay if exists
        if (roadOverlay != null) map.overlays.remove(roadOverlay)

        //Add new road overlay
        val newRoadOverlay = RoadManager.buildRoadOverlay(road)
        roadOverlay = newRoadOverlay
        map.overlays.add(roadOverlay)

        //Add pickup marker overlay if needed
        if (viewModel.state.value == WarperState.RETRIEVING) {
            pickupMarker = Marker(map)
            pickupMarker!!.title = getString(R.string.pickup_location_title)
            pickupMarker!!.position = pickupGeoPoint
            map.overlays.add(pickupMarker)
        }

        //Add delivery marker overlay if needed
        if (viewModel.state.value == WarperState.DELIVERING) {
            deliveryMarker = Marker(map)
            deliveryMarker!!.title = getString(R.string.delivery_location_title)
            deliveryMarker!!.position = deliveryGeoPoint
            map.overlays.add(deliveryMarker)
        }

        map.invalidate()
    }

    private fun showDeliveryInfoDialog(delivery: DeliveryFullInfo) {
        val alertDialog = AlertDialog.Builder(context)

        val inflater = this.layoutInflater

        val view = inflater.inflate(R.layout.dialog_delivery_info, null)

        val phone = view.findViewById<TextView>(R.id.client_phone)
        val address = view.findViewById<TextView>(R.id.deliver_address)
        val store = view.findViewById<TextView>(R.id.store_name)
        val storeAddress = view.findViewById<TextView>(R.id.store_address)

        phone.text = delivery.clientPhone
        address.text = delivery.deliverAddress
        store.text = delivery.store.name
        storeAddress.text = delivery.store.address

        alertDialog.setView(view)

        alertDialog.setTitle(R.string.delivery_info)
            .setPositiveButton(getString(R.string.ok)) { _, _ -> }

        deliveryInfoDialog = alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
    }

    override fun onPause() {
        super.onPause()
        viewModel.vehicleIds.postValue(null)
        vehicleSelectionDialog?.dismiss()
        deliveryInfoDialog?.dismiss()
    }
}

private fun GeoPoint.toLocation(): LocationEntity {
    return LocationEntity(this.latitude, this.longitude)
}
