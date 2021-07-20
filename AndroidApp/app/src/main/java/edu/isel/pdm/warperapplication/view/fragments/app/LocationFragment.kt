package edu.isel.pdm.warperapplication.view.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.LocationViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
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
        val map: MapView = rootView.findViewById(R.id.map)
        val mapController = map.controller
        initMap(map)
        viewModel.initFirestore()

        viewModel.deliveryLocation.observe(viewLifecycleOwner, {
            updateMap(it, map, mapController)
        })


        map.invalidate()

        // Inflate the layout for this fragment
        return rootView
    }


    //TODO: Fix map not centering correctly
    private fun updateMap(point: GeoPoint?, map: MapView, mapController: IMapController) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker);
        mapController.setCenter(point)
        mapController.setZoom(15)
        map.invalidate()
    }

    fun initMap(map: MapView) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
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