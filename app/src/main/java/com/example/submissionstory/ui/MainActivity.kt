package com.example.submissionstory.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.submissionstory.R
import com.example.submissionstory.data.adapter.StoryAdapter
import com.example.submissionstory.data.preferences.AuthPreferences
import com.example.submissionstory.data.repositories.AuthRepos
import com.example.submissionstory.data.room.StoriesDB
import com.example.submissionstory.data.utils.Factory
import com.example.submissionstory.data.utils.Auth
import com.example.submissionstory.data.utils.callLoading
import com.example.submissionstory.data.viewmodel.AuthViewModel
import com.example.submissionstory.data.viewmodel.MainViewModel
import com.example.submissionstory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var authRepos: AuthRepos
    private lateinit var authPreferences: AuthPreferences
    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var authViewModel: AuthViewModel
    private lateinit var storiesDb: StoriesDB

    private lateinit var auth: Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        storiesDb = StoriesDB.getInstance(this)

        authRepos = AuthRepos()
        storyAdapter = StoryAdapter()
        authPreferences = AuthPreferences(this)
        auth = Auth(authPreferences)

        authViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRepos, this)
        )[AuthViewModel::class.java]


        mainViewModel = ViewModelProvider(
            this,
            Factory(authPreferences, authRepos, this)
        )[MainViewModel::class.java]

        auth.getToken().observe(this) { token ->
            if (token != null) {
                initiateAdapter()
                mainViewModel.getStory.observe(this) {
                    storyAdapter.submitData(lifecycle, it)
                }
                mainViewModel.loading.value = false
            } else {
                Log.e("tag", "wrong key")
                finishAffinity()
            }
        }
        mainViewModel.loading.observe(this) { isLoading ->
            callLoading(binding.progressBar, isLoading)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initiateAdapter() {
        storyAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvList.smoothScrollToPosition(0)
                }
            }
        })
        binding.rvList.apply {
            smoothScrollToPosition(0)
            adapter = storyAdapter
        }
    }

    override fun onOptionsItemSelected(menu: MenuItem): Boolean {
        when (menu.itemId) {
            R.id.addStory -> {
                val intent = Intent(this@MainActivity, NewStoryActivity::class.java)
                startActivity(intent)
            }
            R.id.mapStory -> {
                val intent = Intent(this@MainActivity, MapStoryActivity::class.java)
                startActivity(intent)
            }
            R.id.logout -> {
                this.getSharedPreferences("data", 0)
                    .edit().clear().apply()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .also {
                        auth.delToken()
                        startActivity(it)
                    }
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(menu)
    }
}