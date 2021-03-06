package edu.isel.pdm.warperapplication.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.adapters.AuthPageAdapter
import edu.isel.pdm.warperapplication.view.fragments.auth.LoginFragment
import edu.isel.pdm.warperapplication.view.fragments.auth.RegisterFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_auth)


        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val authPageAdapter = AuthPageAdapter(supportFragmentManager)

        authPageAdapter.addFragment(LoginFragment())
        authPageAdapter.addFragment(RegisterFragment())
        viewPager.adapter = authPageAdapter
    }
}

