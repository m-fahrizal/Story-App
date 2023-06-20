@file:Suppress("DEPRECATION")

package com.example.submissionstory.ui

import com.example.submissionstory.R
import com.example.submissionstory.databinding.ActivityDetailBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.submissionstory.data.response.ListStory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val info = intent.getParcelableExtra<ListStory>(EXTRA_ID)

        binding.apply {
            username.text = info?.name
            desc.text = info?.description
        }
        Glide.with(this)
            .load(info?.photoUrl)
            .into(binding.photoDetail)
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
        const val EXTRA_ID = "extra_id"
    }
}