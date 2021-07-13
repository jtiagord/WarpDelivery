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
import edu.isel.pdm.warperapplication.web.entities.Warper

class UserFragment : Fragment() {

    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        val userTextView = rootView.findViewById<TextView>(R.id.username_value)
        val fNameTextView = rootView.findViewById<TextView>(R.id.first_name_value)
        val lNameTextView = rootView.findViewById<TextView>(R.id.last_name_value)
        val emailTextView = rootView.findViewById<TextView>(R.id.email_value)
        val phoneTextView = rootView.findViewById<TextView>(R.id.phone_value)

        viewModel.userInfo.observe(viewLifecycleOwner, {
            val warper = viewModel.userInfo.value

            if(warper != null) {
                displayUserInfo(
                    userTextView, fNameTextView, lNameTextView, emailTextView, phoneTextView, warper
                )
            }

        })

        //TODO: Place this where it belongs
        viewModel.getUserInfo()
        return rootView
    }


    private fun displayUserInfo(
        user: TextView, fName: TextView, lName: TextView, email: TextView,
        phone: TextView, warper: Warper
    ) {
        user.text = warper.username
        fName.text = warper.firstname
        lName.text = warper.lastname
        email.text = warper.email
        phone.text = warper.phonenumber
    }
}