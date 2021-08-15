package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.adapters.VehiclesAdapter
import edu.isel.pdm.warperapplication.viewModels.VehiclesViewModel
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehiclesFragment : Fragment() {

    companion object {
        val vehicleTypes = arrayOf("small", "medium", "large")
    }

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

        addButton.setOnClickListener {
            //addVehicleDialog.show(parentFragmentManager, "Add Vehicle")
            showVehicleCreationDialog()
        }
        refreshButton.setOnClickListener {
            viewModel.getVehicles()
            refreshButton.isEnabled = false
            refreshButton.postDelayed({ refreshButton.isEnabled = true }, 2000)
        }

        viewModel.vehicles.observe(viewLifecycleOwner, {
            updateVehicles(recyclerView, it)
        })

        return rootView
    }

    private fun updateVehicles(view: RecyclerView, vehicles: List<Vehicle>?) {
        if (vehicles == null) {
            Toast.makeText(activity, "Error updating vehicles", Toast.LENGTH_LONG).show()
            return
        }

        view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = VehiclesAdapter(vehicles)
        }
    }

    private fun showVehicleCreationDialog() {
        val alertDialog = AlertDialog.Builder(context)
        var selectedItem = 0
        val regEditText = EditText(requireActivity())
        regEditText.hint = "Vehicle Registration"

        alertDialog.setTitle("Add vehicle")
            .setView(regEditText)
            .setSingleChoiceItems(vehicleTypes, 0) { _, which ->
                selectedItem = which
            }

            //TODO: Stop dialog from dismissing
            .setPositiveButton("Ok") { _, _ ->
                if (regEditText.text.isNullOrBlank()) {
                    Toast.makeText(
                        context,
                        "Please fill out the registration field",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val vehicle = Vehicle(regEditText.text.toString(), vehicleTypes[selectedItem])
                    viewModel.addVehicle(vehicle)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        alertDialog.show()
    }
}