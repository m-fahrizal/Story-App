package com.example.submissionstory.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstory.R
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.utils.Factory
import com.example.submissionstory.data.utils.Auth
import com.example.submissionstory.data.viewmodel.AuthViewModel
import com.example.submissionstory.data.viewmodel.MapStoryViewModel
import com.example.submissionstory.databinding.ActivityMapStoryBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapStoryBinding
    private lateinit var authRepos: AuthRepos
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: Auth
    private val mapStoryViewModel: MapStoryViewModel by viewModels {
        Factory(authPreferences, authRepos, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Story By Location"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setViewModel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map))
    }

    private fun setViewModel() {
        authRepos = AuthRepos()

        authPreferences = AuthPreferences(this)
        auth = Auth(authPreferences)

        authViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRepos, this)
        )[AuthViewModel::class.java]
        auth.getToken().observe(this) { token ->
            if (token != null) {
                mapStoryViewModel.storyWithLoc("Bearer $token")
                mapStoryViewModel.getStoryWithLoc().observe(this) { story ->
                    story?.let { item ->
                        for (i in item.listIterator()) {
                            val coordinates = LatLng(i.lat!!.toDouble(), i.lon!!.toDouble())
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(coordinates)
                                    .title(i.name)
                                    .snippet(i.description)
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(back: MenuItem): Boolean {
        when (back.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(back)
    }
}