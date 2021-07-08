package edu.isel.pdm.warperapplication

import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import edu.isel.pdm.warperapplication.view.fragments.HistoryFragment
import edu.isel.pdm.warperapplication.view.fragments.LocationFragment
import edu.isel.pdm.warperapplication.view.fragments.NotificationsFragment
import edu.isel.pdm.warperapplication.view.fragments.UserFragment
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration.getInstance


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    val TAG = "MAIN ACTIVITY"
    var roadManager: RoadManager = OSRMRoadManager(this, "User-Agent")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        //initMap()
        val locationFragment = LocationFragment()
        val notificationsFragment = NotificationsFragment()
        val historyFragment = HistoryFragment()
        val userFragment = UserFragment()


        makeCurrentFragment(userFragment)
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            R.id.bottom_navigation
        )
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_location -> makeCurrentFragment(locationFragment)
                R.id.ic_notifications -> makeCurrentFragment(notificationsFragment)
                R.id.ic_history -> makeCurrentFragment(historyFragment)
                R.id.ic_user -> makeCurrentFragment(userFragment)
            }
            true
        }


    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle(fragment.id)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }





    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}