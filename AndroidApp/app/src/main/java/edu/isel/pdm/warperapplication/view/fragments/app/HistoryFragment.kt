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
import edu.isel.pdm.warperapplication.view.adapters.DeliveriesAdapter
import edu.isel.pdm.warperapplication.viewModels.HistoryViewModel
import edu.isel.pdm.warperapplication.web.entities.Delivery

class HistoryFragment : Fragment() {

    private val viewModel : HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        val refreshButton = rootView.findViewById<FloatingActionButton>(R.id.refreshButton)

        refreshButton.setOnClickListener {
            viewModel.getDeliveries()
            refreshButton.isEnabled = false
            refreshButton.postDelayed({ refreshButton.isEnabled=true }, 2000)
        }

        viewModel.deliveries.observe(viewLifecycleOwner, {
            updateDeliveries(recyclerView, it)
        })

        return rootView
    }

    private fun updateDeliveries(view: RecyclerView, deliveries: List<Delivery>?){
        if(deliveries == null){
            Toast.makeText(activity, R.string.delivery_update_error, Toast.LENGTH_LONG).show()
            return
        }

        view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = DeliveriesAdapter(deliveries)
        }
    }
}