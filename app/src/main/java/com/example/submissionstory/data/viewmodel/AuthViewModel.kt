package com.example.submissionstory.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.response.LoginResult
import com.example.submissionstory.data.response.SignUpResponse
import com.example.submissionstory.data.utils.Events
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRep: AuthRepos) : ViewModel() {

    val signupMsg: LiveData<Events<String>>
        get() = authRep.signupMsg
    val signinMsg: LiveData<Events<String>>
        get() = authRep.signinMsg
    val signinUser: LiveData<LoginResult> = authRep.signinUser
    val signupUser: LiveData<SignUpResponse> = authRep.signupUser
    val isEnabled: LiveData<Boolean> = authRep.isEnabled
    val isLoading: LiveData<Boolean> = authRep.isLoading

    fun signin(email: String, password: String) =
        authRep.signIn(email, password)

    fun signup(name: String, email: String, password: String) =
        authRep.signUp(name, email, password)
}