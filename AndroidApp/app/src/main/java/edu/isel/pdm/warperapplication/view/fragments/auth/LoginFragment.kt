package edu.isel.pdm.warperapplication.view.fragments.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

        val sharedPref =  activity?.getSharedPreferences(
            "LOGIN", Context.MODE_PRIVATE)
        if(sharedPref!= null) {
            if (sharedPref.contains("username") && sharedPref.contains("password")) {

                val username = sharedPref.getString("username", "") ?: ""
                val password = sharedPref.getString("password", "") ?: ""

                viewModel.tryLogin(username, password)
            }
        }


        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = rootView.findViewById<Button>(R.id.btn_login)
        val usernameInput = rootView.findViewById<EditText>(R.id.et_username)
        val passwordInput = rootView.findViewById<EditText>(R.id.et_password)

        viewModel.loginStatus.observe(viewLifecycleOwner, {
            if (it) {

                if(sharedPref!= null) {

                    with(sharedPref.edit()) {
                        val (username, password) = viewModel.lastLogin?: return@with
                        Log.v("USER", "STORING USER $username:$password")
                        putString("username",username)
                        putString("password",password)
                        apply()
                    }
                }

                val intent = Intent(activity, MainActivity::class.java)

                this.startActivity(intent)
            } else {
                Toast.makeText(activity, R.string.invalid_details_error, Toast.LENGTH_LONG).show()
            }
        })

        loginButton.setOnClickListener {
            if (checkInputs(listOf(usernameInput, passwordInput))) {

                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                tryLogin(username, password)
            } else {
                Toast.makeText(context, R.string.empty_fields_error, Toast.LENGTH_LONG).show()
            }
        }
        return rootView
    }

    private fun tryLogin(username: String, password: String) {
        viewModel.tryLogin(username, password)
    }

    private fun checkInputs(inputs: List<EditText>): Boolean {
        for (input in inputs) {
            if (input.text.toString() == "")
                return false
        }
        return true
    }
}