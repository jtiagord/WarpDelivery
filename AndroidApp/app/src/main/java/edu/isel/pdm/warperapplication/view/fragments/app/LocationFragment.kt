package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import edu.isel.pdm.warperapplication.viewModels.WarperState
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
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

import kotlin.collections.ArrayList


class LocationFragment() : Fragment() {

    private val viewModel: LocationViewModel by viewModels()

    companion object {
        private val userAgent = "OsmNavigator/2.4"
        private lateinit var roadManager: RoadManager
    }

    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private var roadOverlay: Polyline? = null
    private var pickupMarker: Marker? = null
    private var deliveryMarker: Marker? = null
    private var vehicleSelectionDialog : AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_location, container, false)
        val activeBtn = rootView.findViewById<Button>(R.id.btn_active)
        val inactiveBtn = rootView.findViewById<Button>(R.id.btn_inactive)
        val finishBtn = rootView.findViewById<Button>(R.id.btn_finish)

        map = rootView.findViewById(R.id.map)
        map.isVisible = false
        mapController = map.controller

        //Init buttons
        activeBtn.setOnClickListener {
            viewModel.getVehicles()
        }

        inactiveBtn.setOnClickListener {
            viewModel.setInactive()
        }

        finishBtn.setOnClickListener {
            viewModel.confirmDelivery()
        }

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

        //Observe if warper is at delivery point
        viewModel.atDeliveryPoint.observe(viewLifecycleOwner, {
            finishBtn.isVisible = true
        })

        //Observe state changes and draw UI according to warper state
        viewModel.state.observe(viewLifecycleOwner, {

            if (it != WarperState.INACTIVE) {

                //Display UI for active / delivering / looking for delivery state
                if(it == WarperState.RETRIEVING) {
                    finishBtn.isVisible = false
                    pickupMarker?.setVisible(true)
                    deliveryMarker?.setVisible(false)
                }
                else if(it == WarperState.DELIVERING) {
                    pickupMarker?.setVisible(true)
                    deliveryMarker?.setVisible(true)
                }

                map.isVisible = true
                initMap(map)
                activeBtn.isVisible = false
                inactiveBtn.isVisible = true

            } else {

                //Display UI for inactive state
                if(pickupMarker != null){
                    pickupMarker!!.setVisible(false)
                    pickupMarker = null
                }

                if(deliveryMarker != null){
                    deliveryMarker!!.setVisible(false)
                    deliveryMarker = null
                }

                deliveryMarker?.setVisible(false)
                map.isVisible = false
                activeBtn.isVisible = true
                inactiveBtn.isVisible = false
                finishBtn.isVisible = false
            }
        })

        //Displays the vehicle selection dialog after vehicles are obtained from the API
        viewModel.vehicleIds.observe(viewLifecycleOwner, {
            if (it.isEmpty())
                Toast.makeText(context, getString(R.string.no_vehicles_error), Toast.LENGTH_LONG)
                    .show()
            else if (viewModel.state.value == WarperState.INACTIVE)
                showVehicleSelectionDialog()
        })

        // Inflate the layout for this fragment
        return rootView
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
            viewModel.atDeliveryPoint.postValue(true)
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
        map.isVerticalMapRepetitionEnabled = false
        map.setMultiTouchControls(true)

        //Add user location overlay
        val myOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
        map.overlays.add(myOverlay)
        myOverlay.enableMyLocation()
        myOverlay.enableFollowLocation()

        //Add markers if needed
        if(deliveryMarker != null)
            map.overlays.add(deliveryMarker)

        if(pickupMarker != null)
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
                dialog.cancel()
            }
        vehicleSelectionDialog = alertDialog.show()
    }

    private fun getRouteAsync(state: WarperState) {
        val waypoints = ArrayList<GeoPoint>()

        val currLoc = viewModel.currentLocation.value ?: LocationEntity(0.0, 0.0)
        val currGeoPoint = currLoc.toGeoPoint(currLoc.latitude, currLoc.longitude)

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

            lifecycleScope.launch() {
                updateUIWithRoadAndMarker(newRoad, pickupGeoPoint, deliveryGeoPoint)
            }
        }
    }

    private fun updateUIWithRoadAndMarker(
        road: Road,
        pickupGeoPoint: GeoPoint,
        deliveryGeoPoint: GeoPoint
    ) {

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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
    }

    override fun onPause() {
        super.onPause()
        vehicleSelectionDialog?.dismiss()
    }
}

private fun GeoPoint.toLocation(): LocationEntity {
    return LocationEntity(this.latitude, this.longitude)
}
