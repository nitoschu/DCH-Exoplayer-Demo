package com.digitalconcerthall.dev.challenge.content

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.digitalconcerthall.dev.challenge.main.MainActivity

class VideoListRecycleAdapter(private val context: MainActivity) :
    RecyclerView.Adapter<VideoItemViewHolder>() {

    private val items = mutableListOf<VideoItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
        return VideoItemViewHolder.create(parent, context)
    }

    override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun add(videos: List<VideoItem>) {
        val pos = itemCount
        items.addAll(videos)
        notifyItemRangeInserted(pos, videos.size)
    }

    fun replace(position: Int, newItem: VideoItem) {
        items[position] = newItem
        notifyItemChanged(position)
    }

    fun add(item: VideoItem) {
        val pos = itemCount
        items.add(item)
        notifyItemInserted(pos)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
