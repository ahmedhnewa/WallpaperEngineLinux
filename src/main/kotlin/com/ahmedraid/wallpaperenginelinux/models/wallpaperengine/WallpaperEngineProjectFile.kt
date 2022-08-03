package com.ahmedraid.wallpaperenginelinux.models.wallpaperengine


import com.ahmedraid.wallpaperenginelinux.utils.Constants
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WallpaperEngineProjectFile(
    @SerializedName("contentrating")
    val contentRating: String,
    val description: String,
    val file: String,
    val general: General,
    val preview: String = "preview.jpg",
    val tags: List<String>,
    var title: String,
    val type: String? = Constants.WallpapersType.NONE,
    val version: Int,
    val visibility: String = "public",
    @SerializedName("workshopid")
    val workshopId: String,
    @Expose
    var path: String,
    @Expose
    var index: Int = -1
) {
    data class General(
        val properties: Properties,
        @SerializedName("supportsaudioprocessing")
        val supportsAudioProcessing: Boolean
    )
    data class Properties(
        @SerializedName("schemecolor")
        val schemeColor: SchemeColor
    )
    data class SchemeColor(
        val order: Int,
        val text: String,
        val type: String,
        val value: String
    )
}