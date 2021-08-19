package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog

import android.location.Location
import android.os.AsyncTask
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
import edu.isel.pdm.warperapplication.web.entities.LocationEntity
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

import java.util.*
import kotlin.collections.ArrayList


class LocationFragment() : Fragment() {

    private val viewModel: LocationViewModel by viewModels()


    companion object {
        private val userAgent = "OsmNavigator/2.4"
        lateinit var roadManager: RoadManager
        var roads: Array<Road> = emptyArray()
        var roadOverlays : Array<Polyline> = emptyArray()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Adapt to vehicle
        roadManager = OSRMRoadManager(context, userAgent)
        val manager = roadManager as OSRMRoadManager
        manager.setMean(OSRMRoadManager.MEAN_BY_CAR)

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
                viewModel.deliveryLocation.observe(viewLifecycleOwner, { point ->
                    if(point != null)
                        getRouteAsync(map,mapController)
                })

                map.isVisible = true
                initMap(map, mapController)
                activeBtn.isVisible = false
                map.invalidate()

            } else {
                map.isVisible = false
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


    private fun getRouteAsync(map: MapView, mapController: IMapController){
        val waypoints = ArrayList<GeoPoint>()
        val currLoc = viewModel.currentLocation.value ?: LocationEntity(0.0,0.0)
        val currGeoPoint = GeoPoint(currLoc.latitude, currLoc.longitude)
        Log.d("ROUTE", viewModel.pickupLocation.value.toString())
        Log.d("ROUTE", viewModel.deliveryLocation.value.toString())
        waypoints.add(currGeoPoint)
        waypoints.add(viewModel.pickupLocation.value!!)
        waypoints.add(viewModel.deliveryLocation.value!!)
        UpdateRoadTask(roadManager, map).execute(waypoints)

    }



    class UpdateRoadTask(val roadManager: RoadManager, val map: MapView) :
        AsyncTask<ArrayList<GeoPoint>, Void?, Road>() {

        override fun doInBackground(vararg params: ArrayList<GeoPoint>): Road? {
            val waypoints = params[0]
            return roadManager.getRoad(waypoints)
        }

        override fun onPostExecute(result: Road) {
            updateUIWithRoad(result)

        }

        private fun updateUIWithRoad(road: Road) {
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            map.overlays.add(roadOverlay)
            map.invalidate()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachListener()
    }

}