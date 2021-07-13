package edu.isel.pdm.warperapplication.view.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.activities.MainActivity
import edu.isel.pdm.warperapplication.viewModels.LoginViewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = rootView.findViewById<Button>(R.id.btn_login)
        val usernameInput = rootView.findViewById<EditText>(R.id.et_username)
        val passwordInput = rootView.findViewById<EditText>(R.id.et_password)

        viewModel.loginStatus.observe(viewLifecycleOwner, {
            if (it){
                val intent = Intent(activity, MainActivity::class.java)
                this.startActivity(intent)
            } else {
                Toast.makeText(activity, "Invalid username / password combo", Toast.LENGTH_LONG)
                    .show()
            }
        })

        loginButton.setOnClickListener {
            if(checkInputs(listOf(usernameInput, passwordInput))){

                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                tryLogin(username, password)
            } else {
                Log.v("LOGIN", "NULL INPUTS")
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_LONG).show()
            }
        }
        return rootView
    }

    private fun tryLogin(username: String, password: String){
        viewModel.tryLogin(username, password)
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