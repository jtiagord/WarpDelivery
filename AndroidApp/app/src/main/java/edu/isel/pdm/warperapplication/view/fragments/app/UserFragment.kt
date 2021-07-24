package edu.isel.pdm.warperapplication.view.fragments.app

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.viewModels.UserViewModel
import edu.isel.pdm.warperapplication.web.entities.Warper

class UserFragment : Fragment() {

    companion object {
        val inputsMap = initInputsMap()

        private fun initInputsMap(): HashMap<Int, Pair<Int, String>> {
            val map = HashMap<Int, Pair<Int, String>>()
            map[R.id.ib_fName] = Pair(InputType.TYPE_CLASS_TEXT, "First Name")
            map[R.id.ib_lName] = Pair(InputType.TYPE_CLASS_TEXT, "Last Name")
            map[R.id.ib_email] = Pair(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "Email")
            map[R.id.ib_phone] = Pair(InputType.TYPE_CLASS_PHONE, "Phone")
            return map
        }
    }

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

        val fNameEditButton = rootView.findViewById<ImageButton>(R.id.ib_fName)
        val lNameEditButton = rootView.findViewById<ImageButton>(R.id.ib_lName)
        val emailEditButton = rootView.findViewById<ImageButton>(R.id.ib_email)
        val phoneEditButton = rootView.findViewById<ImageButton>(R.id.ib_phone)

        val buttonList =
            listOf<ImageButton>(fNameEditButton, lNameEditButton, emailEditButton, phoneEditButton)
        for (button in buttonList) {
            button.setOnClickListener {
                showEditDialog(button.id)
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner, {
            val warper = viewModel.userInfo.value

            if (warper != null) {
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

    private fun showEditDialog(buttonId: Int) {
        val alertDialog = AlertDialog.Builder(context)

        //TODO: Use placeholder strings, validate inputs
        alertDialog.setTitle(inputsMap[buttonId]!!.second)
        alertDialog.setMessage("Insert new ${inputsMap[buttonId]!!.second}")

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val input = EditText(context)
        input.layoutParams = lp

        alertDialog.setView(input)


        input.setRawInputType(inputsMap[buttonId]!!.first)

        alertDialog.setPositiveButton(
            "Confirm"
        ) { _, _ ->
            //TODO: UPDATE USER HERE
        }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }
        alertDialog.show()
    }
}