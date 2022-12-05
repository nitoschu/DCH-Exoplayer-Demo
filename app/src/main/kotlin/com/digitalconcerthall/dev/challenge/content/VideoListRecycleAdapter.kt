package com.digitalconcerthall.dev.challenge.content

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.digitalconcerthall.dev.challenge.main.MainActivity

class VideoListRecycleAdapter(private val context: MainActivity) :
    RecyclerView.Adapter<VideoItemViewHolder>() {

    private val items = mutableListOf<Video>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemViewHolder {
        return VideoItemViewHolder.create(parent, context)
    }

    override fun onBindViewHolder(holder: VideoItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun add(videos: List<Video>) {
        val pos = itemCount
        items.addAll(videos)
        notifyItemRangeInserted(pos, videos.size)
    }

    fun add(item: Video) {
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
