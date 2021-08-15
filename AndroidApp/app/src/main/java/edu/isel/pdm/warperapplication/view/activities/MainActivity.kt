package edu.isel.pdm.warperapplication.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import edu.isel.pdm.warperapplication.R
import edu.isel.pdm.warperapplication.view.fragments.app.*
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration.getInstance


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    val TAG = "MAIN ACTIVITY"
    var roadManager: RoadManager = OSRMRoadManager(this, "User-Agent")

    /**
     * Provides access to the Fused Location Provider API.
     */
    lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Callback for changes in location.
     */
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest : LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createLocationRequest()

        //TODO: Change after map testing
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)

        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val locationFragment = LocationFragment()
        val notificationsFragment = NotificationsFragment()
        val historyFragment = HistoryFragment()
        val userFragment = UserFragment()
        val vehiclesFragment = VehiclesFragment()

        makeCurrentFragment(locationFragment)
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            R.id.bottom_navigation
        )

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                Log.d("NEW LOCATION", "NEW LOCATION")
                locationFragment.onNewLocation(locationResult.lastLocation)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            Log.d("LOCATION PERMISSION", "YOU HAVE PERMISSION")

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper())
        }else{
            requestPermissions()
            Log.d("LOCATION PERMISSION", "YOU DONT HAVE PERMISSION")
        }


        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_location -> makeCurrentFragment(locationFragment)
                R.id.ic_notifications -> makeCurrentFragment(notificationsFragment)
                R.id.ic_history -> makeCurrentFragment(historyFragment)
                R.id.ic_user -> makeCurrentFragment(userFragment)
                R.id.ic_vehicles -> makeCurrentFragment(vehiclesFragment)
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

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //TODO HANDLE BETTER LOCATION
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback, Looper.myLooper());
                // Permission was granted.
            } else {
                // Permission denied.
            }
        }
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}