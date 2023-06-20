package com.example.submissionstory.ui

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstory.R
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.repositories.UploadRepos
import com.example.submissionstory.data.utils.*
import com.example.submissionstory.data.viewmodel.NewStoryViewModel
import com.example.submissionstory.databinding.ActivityNewStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentPhotoPath: String
    private lateinit var newStoryViewModel: NewStoryViewModel
    private lateinit var authRepos: AuthRepos
    private lateinit var uploadRepos: UploadRepos
    private var getFile: File? = null
    private lateinit var auth: Auth
    private lateinit var authPreferences: AuthPreferences

    private var location: LatLng? = null

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                includeLocation()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Permission Not Granted.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.addStory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        authRepos = AuthRepos()
        uploadRepos = UploadRepos()
        authPreferences = AuthPreferences(this)
        auth = Auth(authPreferences)

        newStoryViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRepos, this)
        )[NewStoryViewModel::class.java]


        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadStory() }
        binding.includeLocation.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                includeLocation()
            }
        }
    }

    private fun includeLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                location = LatLng(loc.latitude, loc.longitude)
                Log.e("lokasi", location.toString())
            }
        } else {
            requestPermission.launch(ACCESS_FINE_LOCATION)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@NewStoryActivity,
                "com.example.submissionstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = getFile as File

            val desc = "${binding.desc.text}".toRequestBody("text/plain".toMediaType())
            val imgFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imgmultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                imgFile
            )

            auth.getToken().observe(this) { token ->

                if (location != null) {
                    newStoryViewModel.uploadStory(
                        "Bearer $token",
                        imgmultipart,
                        desc,
                        lat = (location as LatLng).latitude,
                        lon = (location as LatLng).longitude
                    )
                } else {
                    newStoryViewModel.uploadStory(
                        "Bearer $token",
                        imgmultipart,
                        desc
                    )
                }


            }

            newStoryViewModel.loading.observe(this) { loading ->
                callLoading(binding.progressBar, loading)
            }

            newStoryViewModel.response.observe(this) { response ->
                if (!response.error) {
                    val i = Intent(this, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                }
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@NewStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
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

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}