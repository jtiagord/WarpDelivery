package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.tasks.Task
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class LocationFragment() : Fragment() {

    private val viewModel: LocationViewModel by viewModels()
    var roadManager: RoadManager = OSRMRoadManager(activity, "User-Agent")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_location, container, false)
        val activeBtn = rootView.findViewById<Button>(R.id.btn_active)

        val map: MapView = rootView.findViewById(R.id.map)
        map.isVisible = false
        val mapController = map.controller

        activeBtn.setOnClickListener {
            viewModel.getVehicles()
        }

        viewModel.initFirestore()

        viewModel.active.observe(viewLifecycleOwner, {
            if(it) {
                viewModel.deliveryLocation.observe(viewLifecycleOwner, {
                    getAndDrawRoute(map,mapController)
                })

                map.isVisible = true
                initMap(map, mapController)
                activeBtn.isVisible = false
                map.invalidate()

            } else {
                map.isVisible = false
                viewModel.detachListener()
                activeBtn.isVisible = true
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

    fun onNewLocation(location : Location){
        val locationEntity = LocationEntity(location.latitude, location.longitude)
        viewModel.updateCurrentLocation(locationEntity)
    }


    private fun initMap(map: MapView, mapController: IMapController) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        //map.minZoomLevel = 25.0
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


    private fun getAndDrawRoute(map: MapView, mapController: IMapController){
        val waypoints = ArrayList<GeoPoint>()
        val currLoc = viewModel.currentLocation.value!!
        val currGeoPoint = GeoPoint(currLoc.latitude, currLoc.longitude)
        waypoints.add(currGeoPoint)
        waypoints.add(viewModel.pickupLocation.value!!)
        waypoints.add(viewModel.deliveryLocation.value!!)
        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map.overlays.add(roadOverlay);
        map.invalidate()
    }

}