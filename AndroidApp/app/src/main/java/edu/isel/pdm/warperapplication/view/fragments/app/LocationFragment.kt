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
import org.osmdroid.views.overlay.Polyline

import kotlin.collections.ArrayList


class LocationFragment() : Fragment() {

    private val viewModel: LocationViewModel by viewModels()


    companion object {
        private val userAgent = "OsmNavigator/2.4"
        lateinit var roadManager: RoadManager
        var roadObtained = false

    }

    private lateinit var map : MapView
    private lateinit var mapController: IMapController
    private var roadOverlay: Polyline? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        roadManager = OSRMRoadManager(context, userAgent)
        val manager = roadManager as OSRMRoadManager
        manager.setMean(OSRMRoadManager.MEAN_BY_CAR)

        val rootView = inflater.inflate(R.layout.fragment_location, container, false)
        val activeBtn = rootView.findViewById<Button>(R.id.btn_active)
        val inactiveBtn = rootView.findViewById<Button>(R.id.btn_inactive)
        val finishBtn = rootView.findViewById<Button>(R.id.btn_finish)

        map = rootView.findViewById(R.id.map)
        map.isVisible = false
        mapController = map.controller

        activeBtn.setOnClickListener {
            viewModel.getVehicles()
        }

        inactiveBtn.setOnClickListener{
            viewModel.setInactive()
        }

        viewModel.initFirestore()

        viewModel.active.observe(viewLifecycleOwner, {
            if(it) {
                viewModel.deliveryLocation.observe(viewLifecycleOwner, { point ->
                    if(point != null && !roadObtained) {
                        Log.d("ACTIVE", "delivering")
                        getRouteAsync(map)
                        inactiveBtn.isVisible = false
                    }
                })

                //Display UI for active / delivering state
                map.isVisible = true
                initMap(map)
                activeBtn.isVisible = false
                inactiveBtn.isVisible = true
                Log.d("ACTIVE", "active")

                viewModel.atDeliveryPoint.observe(viewLifecycleOwner, {
                    finishBtn.isVisible = true
                })

            } else {
                //Display UI for inactive state
                Log.d("ACTIVE", "inactive")
                map.isVisible = false
                activeBtn.isVisible = true
                inactiveBtn.isVisible = false
                finishBtn.isVisible = false
            }
        })

        viewModel.vehicleIds.observe(viewLifecycleOwner, {
            if(it.isEmpty())
                Toast.makeText(context, "You have no vehicles, please add one", Toast.LENGTH_LONG).show()
            else if (!viewModel.active.value!!)
                showVehicleSelectionDialog()
        })


        // Inflate the layout for this fragment
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {

        }
    }

    fun onNewLocation(location : Location){
        if(!viewModel.active.value!!)
            return

        val oldLocation = viewModel.currentLocation.value
        val newLocation = LocationEntity(location.latitude, location.longitude)
        val deliveryLocation = viewModel.deliveryLocation.value

        if(deliveryLocation != null && newLocation.getDistance(deliveryLocation.toLocation()) < 10) {
            viewModel.atDeliveryPoint.postValue(true)
            Log.v("DELIVERY", "AT POINT")
        }

        if(oldLocation != null &&
            newLocation.getDistance(oldLocation) > 2) {
            getRouteAsync(map)
        }

        viewModel.updateCurrentLocation(newLocation)
    }


    private fun initMap(map: MapView) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.isTilesScaledToDpi = true
        map.minZoomLevel = 1.0
        map.maxZoomLevel = 21.0
        map.isVerticalMapRepetitionEnabled = false
        map.setMultiTouchControls(true)
        val myOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context) ,map)
        map.overlays.add(myOverlay)
        myOverlay.enableMyLocation()
        myOverlay.enableFollowLocation()
        map.invalidate()
    }

    private fun showVehicleSelectionDialog() {
        val alertDialog = AlertDialog.Builder(context)
        val vehiclesList = viewModel.vehicleIds.value!!

        var selectedItem = 0
        alertDialog.setTitle("Select vehicle")
            .setSingleChoiceItems(vehiclesList, 0) { _, which ->
                selectedItem = which
            }
            .setPositiveButton("Ok") { _, _ ->
                viewModel.setActive(vehiclesList[selectedItem])
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        alertDialog.show()
    }


    private fun getRouteAsync(map: MapView){
        val waypoints = ArrayList<GeoPoint>()
        val currLoc = viewModel.currentLocation.value ?: LocationEntity(0.0,0.0)
        val currGeoPoint = GeoPoint(currLoc.latitude, currLoc.longitude)
        Log.d("ROUTE", viewModel.pickupLocation.value.toString())
        Log.d("ROUTE", viewModel.deliveryLocation.value.toString())
        waypoints.add(currGeoPoint)
        waypoints.add(viewModel.pickupLocation.value!!)
        waypoints.add(viewModel.deliveryLocation.value!!)

        lifecycleScope.launch(Dispatchers.IO) {
            val newRoad = roadManager.getRoad(waypoints)

            updateUIWithRoad(newRoad)
        }

    }

    private fun updateUIWithRoad(road: Road) {
        val newRoadOverlay = RoadManager.buildRoadOverlay(road)
        if(roadOverlay != null)  map.overlays.remove(roadOverlay)
        roadOverlay = newRoadOverlay
        map.overlays.add(roadOverlay)
        map.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
    }
}

private fun GeoPoint.toLocation(): LocationEntity {
    return LocationEntity(this.latitude, this.longitude)
}
