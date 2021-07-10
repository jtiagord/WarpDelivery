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

    private val viewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        val userTextView = rootView.findViewById<TextView>(R.id.UserInfoText)

        viewModel.userInfo.observe(viewLifecycleOwner, {
            displayUserInfo(userTextView)
        })

        //TODO: Place this where it belongs
        viewModel.getUserInfo()
        return rootView
    }


    private fun displayUserInfo(textView: TextView){
        val userInfo: String = "" +
                "First Name: ${viewModel.userInfo.value!!.firstname}\n" +
                "Last Name: ${viewModel.userInfo.value!!.lastname}\n" +
                "Email: ${viewModel.userInfo.value!!.email}\n" +
                "Phone Number: ${viewModel.userInfo.value!!.phonenumber}\n"

        textView.text = userInfo
        textView.invalidate()
    }
}