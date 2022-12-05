package com.digitalconcerthall.dev.challenge.content

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

    fun bind(video: Video) {

        val title = SpannableStringHelper.replaceBracketsWithItalic(video.title)
        val subtitle = SpannableStringHelper.replaceBracketsWithItalic(video.subtitle)
        val description = SpannableStringHelper.replaceBracketsWithItalic(video.description)

        view.findViewById<TextView>(R.id.videoItemTitle).text = title
        view.findViewById<TextView>(R.id.videoItemCredit).text = subtitle
        view.findViewById<TextView>(R.id.videoItemDescription).text = description

        if (video.hd) {
            view.findViewById<ImageView>(R.id.videoItem4kIcon).visibility = View.VISIBLE
        }

        val imageView = view.findViewById<ImageView>(R.id.videoItemImage)
        loadImageAsync(imageView, video.thumb)

        view.setOnClickListener {
            context.playVideo(video)
        }
    }

    private fun loadImageAsync(imageView: ImageView, imageUrl: String) {
        val loadImageSingle = Single.fromCallable {
            Log.d("Loading image from: $imageUrl")
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        }

        runAsyncIO(loadImageSingle, { bmp ->
            Log.d("Image $imageUrl loaded, setting bitmap in view")
            imageView.setImageBitmap(bmp)
        }, { error ->
            Log.e(error, "Couldn't load image from: $imageUrl")
        })
    }

    private fun <T : Any> runAsyncIO(
        single: Single<out T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        single.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(onSuccess, onError)
    }
}
