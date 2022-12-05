package com.digitalconcerthall.dev.challenge.content

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.SpannableString
import android.view.View
import com.digitalconcerthall.dev.challenge.util.Log
import com.digitalconcerthall.dev.challenge.util.SpannableStringHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.URL

data class VideoItem(
    val title: SpannableString,
    val subtitle: SpannableString,
    val description: SpannableString,
    val highDefinitionVisibility: Int,
    var thumbnail: Bitmap?,
    val video: Video
)

fun List<Video>.toVideoItems(): List<VideoItem> {
    val res = mutableListOf<VideoItem>()
    this.forEach {
        res.add(
            VideoItem(
                title = SpannableStringHelper.replaceBracketsWithItalic(it.title),
                subtitle = SpannableStringHelper.replaceBracketsWithItalic(it.subtitle),
                description = SpannableStringHelper.replaceBracketsWithItalic(it.description),
                highDefinitionVisibility = if (it.hd) View.VISIBLE else View.INVISIBLE,
                thumbnail = null,
                video = it
            )
        )
    }
    return res
}

 fun VideoItem.loadThumbnailAsync(onSuccess: (VideoItem) -> Unit) {
    val imageUrl = video.thumb
    val loadImageSingle = Single.fromCallable {
        Log.d("Loading image from: $imageUrl")
        val url = URL(imageUrl)
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

    runAsyncIO(loadImageSingle, { bmp ->
        Log.d("Image $imageUrl loaded, setting bitmap in view")
        thumbnail = bmp
        onSuccess(this)
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