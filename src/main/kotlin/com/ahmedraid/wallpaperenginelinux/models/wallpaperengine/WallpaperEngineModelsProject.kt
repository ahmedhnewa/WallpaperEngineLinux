package com.ahmedraid.wallpaperenginelinux.models.wallpaperengine

import com.google.gson.annotations.SerializedName

class WallpaperEngineModelsProject(
    @SerializedName("autosize")
    val autoSize: Boolean,
    val material: String
) {
}