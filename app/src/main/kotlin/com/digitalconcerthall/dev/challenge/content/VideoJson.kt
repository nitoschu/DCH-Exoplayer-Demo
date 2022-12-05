package com.digitalconcerthall.dev.challenge.content

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("description") val description: String,
    @SerializedName("source") val source: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("thumb") val thumb: String,
    @SerializedName("title") val title: String,
    @SerializedName("hd") val hd: Boolean
)

data class VideoJson(
    @SerializedName("videos") val videos: List<Video>
)
