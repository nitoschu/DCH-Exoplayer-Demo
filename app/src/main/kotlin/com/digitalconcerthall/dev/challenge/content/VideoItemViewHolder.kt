package com.digitalconcerthall.dev.challenge.content

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitalconcerthall.dev.challenge.R
import com.digitalconcerthall.dev.challenge.main.MainActivity
import com.digitalconcerthall.dev.challenge.util.Log
import com.digitalconcerthall.dev.challenge.util.SpannableStringHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URL

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

    fun bind(item: VideoItem) {
        with(item) {

            view.findViewById<TextView>(R.id.videoItemTitle).text = title
            view.findViewById<TextView>(R.id.videoItemCredit).text = subtitle
            view.findViewById<TextView>(R.id.videoItemDescription).text = description
            view.findViewById<ImageView>(R.id.videoItem4kIcon).visibility = highDefinitionVisibility

            if (thumbnail != null) {
                Log.e("#####", "${this.title} *** ${this.video.thumb}")
                view.findViewById<ImageView>(R.id.videoItemImage)
                    .setImageBitmap(thumbnail)
            } else {
                view.findViewById<ImageView>(R.id.videoItemImage)
                    .setImageBitmap(null)
            }

            view.setOnClickListener {
                context.playVideo(video)
            }
        }
    }
}
