package edu.isel.pdm.warperapplication.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.fragments.auth.LoginFragment
import edu.isel.pdm.warperapplication.view.fragments.auth.RegisterFragment


class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val authPageAdapter = AuthenticationPagerAdapter(supportFragmentManager)

        authPageAdapter.addFragment(LoginFragment())
        authPageAdapter.addFragment(RegisterFragment())
        viewPager.adapter = authPageAdapter

    }
}

//TODO: Migrate to ViewPager2
class AuthenticationPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: ArrayList<Fragment> = ArrayList()

    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}
