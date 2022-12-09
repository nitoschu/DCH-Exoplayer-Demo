package com.digitalconcerthall.dev.challenge.content

import android.graphics.Bitmap
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalconcerthall.dev.challenge.R
import com.digitalconcerthall.dev.challenge.main.MainActivity

class VideoItemViewHolder(
    private val view: View,
    private val context: MainActivity
) : RecyclerView.ViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup, context: MainActivity): VideoItemViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
            return VideoItemViewHolder(view, context)
        }
    }

    fun bind(item: VideoItem) = with(item) {
        loadThumbnail(thumbnail, view)
        view.findViewById<TextView>(R.id.videoItemTitle).text = title
        view.findViewById<TextView>(R.id.videoItemCredit).text = subtitle
        view.findViewById<TextView>(R.id.videoItemDescription).text = description
        view.findViewById<ImageView>(R.id.videoItem4kIcon).visibility = highDefinitionVisibility

        val playBtn = view.findViewById<ImageView>(R.id.videoItemPlayIcon)
        playBtn.setOnClickListener { clearPlaylistAndPlayVideo(videoUri) }

        val addToPlaylistBtn = view.findViewById<ImageView>(R.id.addToPlaylistIcon)
        addToPlaylistBtn.setOnClickListener { context.addVideoToPlaylist(videoUri) }
    }

    private fun loadThumbnail(thumbnail: Bitmap?, view: View) {
        if (thumbnail != null) {
            view.findViewById<ImageView>(R.id.videoItemImage).setImageBitmap(thumbnail)
        } else {
            view.findViewById<ImageView>(R.id.videoItemImage).setImageBitmap(null)
        }
    }

    private fun clearPlaylistAndPlayVideo(videoUri: String) {
        context.clearPlaylist()
        context.playVideo(videoUri)
    }
}
