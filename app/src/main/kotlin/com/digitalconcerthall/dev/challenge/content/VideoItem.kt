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

/**
 * A class containing video information to be displayed in a UI.
 *
 * I created this class to decouple [VideoItemViewHolder] from any business logic, making it
 * easier to unit test, and to make a possible future transfer to Jetpack Compose easier.
 */
data class VideoItem(
    val title: SpannableString,
    val subtitle: SpannableString,
    val description: SpannableString,
    val highDefinitionVisibility: Int,
    var thumbnail: Bitmap?,
    val videoUri: String
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
                thumbnail = null, // Will be loaded later to reduce app start time
                videoUri = it.source
            )
        )
    }
    return res
}

fun VideoItem.loadThumbnailAsync(thumbnailUrl: String, onSuccess: (VideoItem) -> Unit) {
    val loadImageSingle = Single.fromCallable {
        Log.d("Loading image from: $thumbnailUrl")
        val url = URL(thumbnailUrl)
        BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

    runAsyncIO(loadImageSingle, { bmp ->
        Log.d("Image $thumbnailUrl loaded, setting bitmap in view")
        thumbnail = bmp
        onSuccess(this)
    }, { error ->
        Log.e(error, "Couldn't load image from: $thumbnailUrl")
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