package edu.isel.pdm.warperapplication.view.fragments.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.UserViewModel

class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        val userTextView = rootView.findViewById<TextView>(R.id.username_value)
        val fNameTextView = rootView.findViewById<TextView>(R.id.first_name_value)
        val lNameTextView = rootView.findViewById<TextView>(R.id.last_name_value)
        val emailTextView = rootView.findViewById<TextView>(R.id.email_value)
        val phoneTextView = rootView.findViewById<TextView>(R.id.phone_value)

        viewModel.userInfo.observe(viewLifecycleOwner, {
            displayUserInfo(
                userTextView, fNameTextView, lNameTextView, emailTextView, phoneTextView
            )
        })

        //TODO: Place this where it belongs
        viewModel.getUserInfo()
        return rootView
    }


    private fun displayUserInfo(
        user: TextView, fName: TextView, lName: TextView, email: TextView,
        phone: TextView
    ) {
        user.text = viewModel.userInfo.value!!.username
        fName.text = viewModel.userInfo.value!!.firstname
        lName.text = viewModel.userInfo.value!!.lastname
        email.text = viewModel.userInfo.value!!.email
        phone.text = viewModel.userInfo.value!!.phonenumber
    }
}