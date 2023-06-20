package com.example.submissionstory.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstory.data.repositories.MainRepos
import com.example.submissionstory.data.response.ListStory

class MainViewModel(
    mainRep: MainRepos
) : ViewModel() {

    val loading = MutableLiveData<Boolean>()

    val getStory: LiveData<PagingData<ListStory>> = mainRep.getStory().cachedIn(viewModelScope)
}

