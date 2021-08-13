package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog
import android.content.Intent
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
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class LocationFragment : Fragment() {

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
        val warperMarker = Marker(map)

        viewModel.vehicleIds.observe(viewLifecycleOwner, {
            if(it.isEmpty())
                Toast.makeText(context, "You have no vehicles, please add one", Toast.LENGTH_LONG).show()
            else
                showVehicleSelectionDialog()
        })

        activeBtn.setOnClickListener {
            viewModel.getVehicles()
        }

        viewModel.initFirestore()

        viewModel.active.observe(viewLifecycleOwner, { it ->

            if(it) {
                viewModel.deliveryLocation.observe(viewLifecycleOwner, {
                    //updateMap(it, map, mapController)
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

        viewModel.currentLocation.observe(viewLifecycleOwner, {
            updateLocationOnMap(it, map, warperMarker)
        })

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode == 0) {

        }
    }

    //TODO: Maybe do this in the viewmodel
    fun onNewLocation(location : Location){
        Log.d("location", "New location : ${location.latitude},${location.longitude}")
        viewModel.updateCurrentLocation(location.latitude, location.longitude)
    }

    private fun updateLocationOnMap(point: GeoPoint, map: MapView, warperMarker: Marker) {
        map.overlays.remove(warperMarker)
        warperMarker.position = point
        map.overlays.add(warperMarker)
        map.invalidate()
    }

    private fun initMap(map: MapView, mapController: IMapController) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
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

    /*
    fun getAndDrawRoute(map: MapView, mapController: MapController){
        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(viewModel.startingLoc.value!!)
        waypoints.add(viewModel.pickupLoc.value!!)
        waypoints.add(viewModel.deliveryLoc.value!!)
        val road = roadManager.getRoad(waypoints)
        val roadOverlay = RoadManager.buildRoadOverlay(road)
        map.overlays.add(roadOverlay);
        mapController.setCenter(viewModel.startingLoc.value!!)
        map.invalidate()

    }
     */
}