package edu.isel.pdm.warperapplication.view.fragments.app.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.VehiclesViewModel
import edu.isel.pdm.warperapplication.web.entities.Vehicle

class VehicleDialogFragment(private val viewModel: VehiclesViewModel) : DialogFragment() {



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_vehicle, null)

            val registrationEditText = view.findViewById<EditText>(R.id.registration)
            val typeEditText = view.findViewById<EditText>(R.id.type)

            //TODO: Verify null inputs, add better dialogue hints
            builder.setView(view)
                // Add action buttons
                .setPositiveButton("Add"
                ) { _, _ ->
                    val vehicle = Vehicle(
                        registrationEditText.text.toString(), typeEditText.text.toString()
                    )
                    viewModel.addVehicle(vehicle)
                }
                .setNegativeButton("Cancel"
                ) { _, _ ->
                    dialog!!.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}