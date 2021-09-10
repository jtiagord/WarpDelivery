package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.activities.AuthActivity
import edu.isel.pdm.warperapplication.viewModels.UserViewModel
import edu.isel.pdm.warperapplication.web.entities.Warper
import edu.isel.pdm.warperapplication.web.entities.WarperEdit

class UserFragment : Fragment() {

    data class EditUserOptions(
        val inputType: Int,
        val text: String,
    )

    companion object {
        private val inputsMap = initInputsMap()

        private fun initInputsMap(): HashMap<Int, EditUserOptions> {
            val map = HashMap<Int, EditUserOptions>()
            map[R.id.ib_fName] = EditUserOptions(InputType.TYPE_CLASS_TEXT, "First Name")
            map[R.id.ib_lName] = EditUserOptions(InputType.TYPE_CLASS_TEXT, "Last Name")
            map[R.id.ib_email] =
                EditUserOptions(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "Email")
            map[R.id.ib_phone] = EditUserOptions(InputType.TYPE_CLASS_PHONE, "Phone")
            return map
        }
    }

    private val viewModel: UserViewModel by viewModels()
    private var editDialog: AlertDialog? = null

    lateinit var userTextView: TextView
    lateinit var fNameTextView: TextView
    lateinit var lNameTextView: TextView
    lateinit var emailTextView: TextView
    lateinit var phoneTextView: TextView
    lateinit var balanceTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)
        userTextView = rootView.findViewById(R.id.username_value)
        fNameTextView = rootView.findViewById(R.id.first_name_value)
        lNameTextView = rootView.findViewById(R.id.last_name_value)
        emailTextView = rootView.findViewById(R.id.email_value)
        phoneTextView = rootView.findViewById(R.id.phone_value)
        balanceTextView = rootView.findViewById(R.id.balance_value)

        val fNameEditButton = rootView.findViewById<ImageButton>(R.id.ib_fName)
        val lNameEditButton = rootView.findViewById<ImageButton>(R.id.ib_lName)
        val emailEditButton = rootView.findViewById<ImageButton>(R.id.ib_email)
        val phoneEditButton = rootView.findViewById<ImageButton>(R.id.ib_phone)

        val logoutButton = rootView.findViewById<Button>(R.id.btn_logout)

        logoutButton.setOnClickListener {
            viewModel.logout()

            val sharedPref = activity?.getSharedPreferences(
                "LOGIN", Context.MODE_PRIVATE
            )

            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    remove("username")
                    remove("password")
                    commit()
                }
            }
            val intent = Intent(activity, AuthActivity::class.java)
            activity?.finish()
            this.startActivity(intent)
        }

        val imgButtonList =
            listOf<ImageButton>(fNameEditButton, lNameEditButton, emailEditButton, phoneEditButton)
        for (button in imgButtonList) {
            button.setOnClickListener {
                showEditDialog(button.id)
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner, {
            val warper = viewModel.userInfo.value

            if (warper != null) {
                displayUserInfo(warper)
            }
        })

        viewModel.getUserInfo()
        return rootView
    }

    private fun displayUserInfo(warper: Warper) {
        userTextView.text = warper.username
        fNameTextView.text = warper.firstname
        lNameTextView.text = warper.lastname
        emailTextView.text = warper.email
        phoneTextView.text = warper.phonenumber
        balanceTextView.text = warper.balance.toString()
    }

    private fun showEditDialog(buttonId: Int) {

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val input = EditText(context)
        input.setRawInputType(inputsMap[buttonId]!!.inputType)
        input.layoutParams = lp

        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle(inputsMap[buttonId]!!.text)
        alertDialog.setMessage(getString(R.string.edit_field, inputsMap[buttonId]!!.text))
        alertDialog.setView(input)
        alertDialog.setPositiveButton(R.string.confirm)
        { _, _ ->
            if (input.text.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    getString(R.string.empty_field_error),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                when (buttonId) {
                    R.id.ib_fName -> {
                        viewModel.updateUser(WarperEdit(firstname = input.text.toString()))
                    }
                    R.id.ib_lName -> {
                        viewModel.updateUser(WarperEdit(lastname = input.text.toString()))
                    }
                    R.id.ib_phone -> {
                        viewModel.updateUser(WarperEdit(phonenumber = input.text.toString()))
                    }
                    R.id.ib_email -> {
                        viewModel.updateUser(WarperEdit(email = input.text.toString()))
                    }
                }
            }
        }
            .setNegativeButton(R.string.cancel)
            { dialog, _ ->
                dialog.cancel()
            }
        editDialog = alertDialog.show()
    }

    override fun onPause() {
        super.onPause()
        editDialog?.dismiss()
    }
}