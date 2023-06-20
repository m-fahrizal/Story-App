package com.example.submissionstory.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.submissionstory.data.api.ApiConfig
import com.example.submissionstory.data.response.SignInResponse
import com.example.submissionstory.data.response.LoginResult
import com.example.submissionstory.data.response.SignUpResponse
import com.example.submissionstory.data.utils.Events
import com.example.submissionstory.data.utils.EspressoIdlingResource.wrapEspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepos {
    private val signin = MutableLiveData<SignInResponse>()

    private val _signupUser = MutableLiveData<SignUpResponse>()
    val signupUser: LiveData<SignUpResponse> = _signupUser

    private val _signinUser = MutableLiveData<LoginResult>()
    val signinUser: LiveData<LoginResult> = _signinUser

    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> = _isEnabled

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _signupMsg = MutableLiveData<Events<String>>()
    val signupMsg: LiveData<Events<String>>
        get() = _signupMsg

    private val _signinMsg = MutableLiveData<Events<String>>()
    val signinMsg: LiveData<Events<String>>
        get() = _signinMsg

    fun signUp(
        name: String,
        email: String,
        password: String,
    ): LiveData<SignUpResponse> {
        _isEnabled.value = false
        _isLoading.value = true

        wrapEspressoIdlingResource {
            ApiConfig.getApiService().register(name, email, password)
                .enqueue(object : Callback<SignUpResponse> {
                    override fun onResponse(
                        call: Call<SignUpResponse>,
                        response: Response<SignUpResponse>,
                    ) {
                        _isEnabled.value = true
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            _signupUser.postValue(response.body())
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                            _signupMsg.value = Events("")
                        }
                    }

                    override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            return _signupUser
        }
    }

    fun signIn(
        email: String,
        password: String,
    ): LiveData<SignInResponse> {
        _isEnabled.value = false
        _isLoading.value = true

        wrapEspressoIdlingResource {
            ApiConfig.getApiService().login(email, password)
                .enqueue(object : Callback<SignInResponse> {

                    override fun onResponse(
                        call: Call<SignInResponse>,
                        response: Response<SignInResponse>,
                    ) {
                        _isEnabled.value = true
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            response.body().let { login ->
                                login?.loginResult?.let {
                                    _signinUser.value = LoginResult(it.name, it.userId, it.token)
                                }
                            }
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                            _signinMsg.value = Events("")
                        }
                    }

                    override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            return signin
        }
    }

    companion object {
        private const val TAG = "AuthRep"
    }
}