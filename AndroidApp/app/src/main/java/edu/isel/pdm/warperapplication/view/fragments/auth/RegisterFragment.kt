package edu.isel.pdm.warperapplication.view.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.activities.MainActivity
import edu.isel.pdm.warperapplication.viewModels.RegisterViewModel

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_register, container, false)
        val registerButton = rootView.findViewById<Button>(R.id.btn_register)
        val usernameInput = rootView.findViewById<EditText>(R.id.et_username)
        val firstNameInput = rootView.findViewById<EditText>(R.id.et_firstName)
        val lastNameInput = rootView.findViewById<EditText>(R.id.et_lastName)
        val emailInput = rootView.findViewById<EditText>(R.id.et_email)
        val phoneInput = rootView.findViewById<EditText>(R.id.et_phone)
        val passwordInput = rootView.findViewById<EditText>(R.id.et_password)
        val passwordReInput = rootView.findViewById<EditText>(R.id.et_rePassword)

        viewModel.registerStatus.observe(viewLifecycleOwner, {
            if (it) {
                val intent = Intent(activity, MainActivity::class.java)
                this.startActivity(intent)
            } else {
                Toast.makeText(activity, "Failed to register", Toast.LENGTH_LONG)
                    .show()
            }
        })

        registerButton.setOnClickListener {

            val inputList = listOf<EditText>(
                usernameInput, firstNameInput, emailInput, phoneInput,
                passwordInput, passwordReInput
            )


            if (checkInputs(inputList)) {

                val password = passwordInput.text.toString()
                val passwordRe = passwordReInput.text.toString()

                if (password != passwordRe)
                    Toast.makeText(context, R.string.password_mismatch_error, Toast.LENGTH_LONG)
                        .show()
                else {
                    val username = usernameInput.text.toString()
                    val firstName = firstNameInput.text.toString()
                    val lastName = lastNameInput.text.toString()
                    val email = emailInput.text.toString()
                    val phone = phoneInput.text.toString()

                    register(username, password, firstName, lastName, email, phone)
                }
            } else {
                Toast.makeText(context, R.string.empty_fields_error, Toast.LENGTH_LONG).show()
            }
        }

        return rootView
    }

    private fun checkInputs(inputs: List<EditText>): Boolean {
        for (input in inputs) {
            if (input.text.toString() == "")
                return false
        }
        return true
    }

    private fun register(
        username: String,
        password: String,
        fName: String,
        lName: String,
        email: String,
        phone: String
    ) {
        viewModel.tryRegister(username, password, fName, lName, email, phone)
    }
}