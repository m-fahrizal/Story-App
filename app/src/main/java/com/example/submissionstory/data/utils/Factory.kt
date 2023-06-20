@file:Suppress("UNCHECKED_CAST")

package com.example.submissionstory.data.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.viewmodel.*

class Factory(
    private val authPreferences: AuthPreferences,
    private val authRep: AuthRepos,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRep) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(Inject.mainRep(authPreferences, context)) as T
        }
        if (modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
            return NewStoryViewModel() as T
        }
        if (modelClass.isAssignableFrom(MapStoryViewModel::class.java)) {
            return MapStoryViewModel() as T
        }
        throw java.lang.IllegalArgumentException("Illegal Action")
    }
}