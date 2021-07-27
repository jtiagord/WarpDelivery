package edu.isel.pdm.warperapplication.view.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.adapters.VehiclesAdapter
import edu.isel.pdm.warperapplication.view.fragments.app.dialog.VehicleDialogFragment
import edu.isel.pdm.warperapplication.viewModels.VehiclesViewModel
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehiclesFragment : Fragment() {

    private val viewModel: VehiclesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_vehicles, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        val refreshButton = rootView.findViewById<FloatingActionButton>(R.id.refreshButton)
        val addButton = rootView.findViewById<FloatingActionButton>(R.id.addButton)
        val addVehicleDialog = VehicleDialogFragment(viewModel)

        addButton.setOnClickListener{
            addVehicleDialog.show(parentFragmentManager, "Add Vehicle")
        }
        refreshButton.setOnClickListener {
            viewModel.getVehicles()
            refreshButton.isEnabled = false
            refreshButton.postDelayed({ refreshButton.isEnabled=true }, 2000)
        }

        viewModel.vehicles.observe(viewLifecycleOwner, {
            updateVehicles(recyclerView, it)
        })

        return rootView
    }

    private fun updateVehicles(view: RecyclerView, vehicles: List<Vehicle>?){
        if(vehicles == null){
            Toast.makeText(activity, "Error updating vehicles", Toast.LENGTH_LONG).show()
            return
        }

        view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = VehiclesAdapter(vehicles)
        }
    }
}