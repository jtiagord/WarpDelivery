package edu.isel.pdm.warperapplication.view.fragments.app

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.LocationServices
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import edu.isel.pdm.warperapplication.web.entities.WarperEdit
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

        viewModel.vehicleIds.observe(viewLifecycleOwner, {
            showVehicleSelectionDialog()
        })

        activeBtn.setOnClickListener {
            viewModel.getVehicles()
        }

        viewModel.active.observe(viewLifecycleOwner, { it ->


            if(it) {
                viewModel.initFirestore()

                viewModel.deliveryLocation.observe(viewLifecycleOwner, {
                    //updateMap(it, map, mapController)
                })

                map.isVisible = true
                initMap(map)
                activeBtn.isVisible = false
                map.invalidate()

            } else {
                map.isVisible = false
                viewModel.detachListener()
                activeBtn.isVisible = true
            }
        })

        // Inflate the layout for this fragment
        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode == 0) {

        }
    }

    //TODO: Fix map not centering correctly
    private fun updateMap(point: GeoPoint?, map: MapView, mapController: IMapController) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
        mapController.setCenter(point)
        mapController.setZoom(15)
        map.invalidate()
    }

    //TODO: Check if this is the right permission

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLocationPermissions() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initMap(map: MapView) {
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