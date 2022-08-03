package com.ahmedraid.wallpaperenginelinux.models.wallpaperengine

import com.google.gson.annotations.SerializedName

class WallpaperEngineMaterialsProject(
    val passes: List<Pass>
) {
    data class Pass(
        val blending: String,
        @SerializedName("cullmode")
        val cullMode: String,
        @SerializedName("depthtest")
        val depthTest: String,
        @SerializedName("depthwrite")
        val depthWrite: String,
        val shader: String,
        val textures: List<String>
    )
}