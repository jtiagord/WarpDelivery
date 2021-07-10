package edu.isel.pdm.warperapplication.view.fragments.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import edu.isel.pdm.warperapplication.R


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = rootView.findViewById<Button>(R.id.btn_login)
        val usernameInput = rootView.findViewById<EditText>(R.id.et_username)
        val passwordInput = rootView.findViewById<EditText>(R.id.et_password)

        loginButton.setOnClickListener {
            if(checkInputs(listOf(usernameInput, passwordInput))){

                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                login(username, password)
            } else {
                Log.v("LOGIN", "NULL INPUTS")
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
        return rootView
    }

    private fun login(username: String, password: String){
        Log.v("LOGIN", "LOGGING IN")
    }

    private fun checkInputs(inputs: List<EditText>): Boolean{
        for(input in inputs){
            if(input.text.toString() == "")
                return false
        }
        return true
    }

}