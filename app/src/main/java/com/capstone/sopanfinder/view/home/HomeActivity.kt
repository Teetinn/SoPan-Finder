package com.capstone.sopanfinder.view.home

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capstone.sopanfinder.R
import com.capstone.sopanfinder.databinding.ActivityHomeBinding
import com.capstone.sopanfinder.preference.UserPreference
import com.capstone.sopanfinder.view.favorite.FavoriteActivity
import com.capstone.sopanfinder.view.login.LoginActivity
import com.capstone.sopanfinder.view.maps.MapsActivity
import com.capstone.sopanfinder.view.profile.ProfileActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.concurrent.schedule

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient

    private var lat : Double = 0.0
    private var lon : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        loginCheck()

        binding.searchBtn.setOnClickListener{
            getLocation()

            binding.searchBtn.playAnimation()
            binding.circle.playAnimation()
            binding.homeStatus.setText(R.string.findsopan2)
            binding.homeDesc.setText(R.string.wait)

            Timer().schedule(2000) {
                val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                intent.putExtra("Latitude", lat)
                intent.putExtra("Longitude", lon)
                startActivity(intent)
            }
        }
    }

    private fun getLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it!= null){
//                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "${it.latitude} ${it.longitude}")

                lat = it.latitude
                lon = it.longitude

            }else{
                Log.d("TAG", "Unable to fetch location")

            }
        }
    }

    private fun loginCheck() {
        if (UserPreference.getInstance(this).isLoggedIn) {
            supportActionBar?.title = StringBuilder("Hello, ").append(UserPreference.getInstance(this).user.name)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume(){
        super.onResume()
        binding.searchBtn.pauseAnimation()
        binding.circle.pauseAnimation()
        binding.homeStatus.setText(R.string.findsopan)
        binding.homeDesc.setText(R.string.home_desc_1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite_menu -> startActivity(Intent(this@HomeActivity, FavoriteActivity::class.java))
            R.id.profile_menu -> startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}