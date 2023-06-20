package com.example.submissionstory.data.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionstory.data.response.ListStory
import com.example.submissionstory.databinding.ItemRowUsersBinding
import com.example.submissionstory.ui.DetailActivity

class StoryAdapter : PagingDataAdapter<ListStory, StoryAdapter.UserViewHolder>(DIFF) {

    inner class UserViewHolder(private val binding: ItemRowUsersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStory) {
            with(binding) {
                tvItemName.text = story.name
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(imgItemPhoto)

            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_ID, story)
                }
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(binding.imgItemPhoto, "detail_photo"),
                        androidx.core.util.Pair(binding.tvItemName, "detail_name"),
                    )

                it.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemRowUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }

        }
        val listUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {
            }

            override fun onRemoved(position: Int, count: Int) {
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
            }
        }
    }

}