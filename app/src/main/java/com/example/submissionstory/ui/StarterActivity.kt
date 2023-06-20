package com.example.submissionstory.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.submissionstory.R
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.utils.Auth
import com.example.submissionstory.data.utils.callToast

class StarterActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences
    private lateinit var auth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        auth = Auth(authPreferences)

        Handler(Looper.getMainLooper()).postDelayed({
            selectToken()
        }, 1000)
    }

    private fun selectToken() {
        auth.getToken().observe(this) { key ->
            if (key != null) {
                if (!key.equals("N/A")) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                callToast(this, "Error Occurred!")
            }
        }
    }
}