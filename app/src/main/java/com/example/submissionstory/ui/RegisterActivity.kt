package com.example.submissionstory.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.utils.Factory
import com.example.submissionstory.data.utils.callLoading
import com.example.submissionstory.data.utils.callToast
import com.example.submissionstory.data.viewmodel.AuthViewModel
import com.example.submissionstory.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authPreferences: AuthPreferences
    private lateinit var authViewModel: AuthViewModel
    private lateinit var authRep: AuthRepos
    private val emailPattern = Regex("[a-zA-Z\\d._]+@[a-z]+\\.+[a-z]+")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        authPreferences = AuthPreferences(this)
        authRep = AuthRepos()

        authViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRep, this)
        )[AuthViewModel::class.java]


        binding.buttonRegister.setOnClickListener { setupAction() }

        animate()
    }

    private fun animate() {
        val anText = ObjectAnimator.ofFloat(binding.RTxt1, View.ALPHA, 1f).setDuration(300)
        val anEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(300)
        val anPass =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(300)
        val anButton =
            ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(300)
        val anReg = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(anText, anReg, anEmail, anPass, anButton)
            start()
        }

    }

    private fun setupAction() {
        val name = binding.edRegisterName.text.toString().trim()
        val email = binding.edRegisterEmail.text.toString().trim()
        val password = binding.edRegisterPassword.text.toString().trim()
        authViewModel.isEnabled.observe(this) { isEnabled ->
            binding.buttonRegister.isEnabled = isEnabled
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
                authViewModel.signup(name, email, password)
                authViewModel.signupMsg.observe(this) {
                    it.getUnhandledContent()?.let {
                        callToast(this, "Email already registered")
                    }
                }
                authViewModel.signupUser.observe(this) { signUp ->
                    if (signUp != null) {
                        finish()
                        callToast(this, "You are registered.")
                    }
                }
            }
        }
    }
}