package edu.isel.pdm.warperapplication.view.fragments

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

    val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        val userTextView = rootView.findViewById<TextView>(R.id.UserInfoText)
        viewModel.user.observe(viewLifecycleOwner, {
            displayUserInfo(userTextView)
        })
        viewModel.getUserInfo()

        return rootView
    }


    private fun displayUserInfo(textView: TextView){
        val userInfo: String = "" +
                "First Name: ${viewModel.user.value!!.firstname}\n" +
                "Last Name: ${viewModel.user.value!!.lastname}\n" +
                "Email: ${viewModel.user.value!!.email}\n" +
                "Phone Number: ${viewModel.user.value!!.phonenumber}\n"

        textView.text = userInfo
        textView.invalidate()
    }
}