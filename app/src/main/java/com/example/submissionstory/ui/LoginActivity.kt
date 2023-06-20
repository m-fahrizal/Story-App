package com.example.submissionstory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.viewmodel.AuthViewModel
import com.example.submissionstory.databinding.ActivityLoginBinding
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.utils.Factory
import com.example.submissionstory.data.utils.Auth
import com.example.submissionstory.data.utils.callLoading
import com.example.submissionstory.data.utils.callToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authViewModel: AuthViewModel
    private lateinit var auth: Auth
    private lateinit var authRep: AuthRepos
    private val emailPattern = Regex("[a-zA-Z\\d._]+@[a-z]+\\.+[a-z]+")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        auth = Auth(authPreferences)
        authRep = AuthRepos()

        authViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRep, this)
        )[AuthViewModel::class.java]


        binding.buttonLogin.setOnClickListener { setupAction() }
        binding.textSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        animate()
    }

    private fun animate() {
        val anText = ObjectAnimator.ofFloat(binding.RTxt1, View.ALPHA, 1f).setDuration(300)
        val anEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(300)
        val anPass =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(300)
        val anButton = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(300)
        val anReg = ObjectAnimator.ofFloat(binding.textSignup, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(anText, anEmail, anPass, anButton, anReg)
            start()
        }

    }

    private fun setupAction() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()
        authViewModel.isEnabled.observe(this) { isEnabled ->
            binding.buttonLogin.isEnabled = isEnabled
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            callLoading(binding.progressBar, isLoading)
        }
        when {
            password.isEmpty() or email.isEmpty() -> {
                Toast.makeText(this, "Please fill the password!", Toast.LENGTH_SHORT).show()
            }
            !email.matches(emailPattern) -> {
                Toast.makeText(this, "Wrong email format!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                authViewModel.signin(email, password)
                authViewModel.signinMsg.observe(this) {
                    it.getUnhandledContent()?.let {
                        callToast(this, "Email and password doesn't match!")
                    }
                }
                authViewModel.signinUser.observe(this) { signIn ->
                    auth.setToken(signIn.token)
                    val i = Intent(this, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                    finish()
                    callToast(this, "Sign In Successful")
                }
            }
        }
    }
}