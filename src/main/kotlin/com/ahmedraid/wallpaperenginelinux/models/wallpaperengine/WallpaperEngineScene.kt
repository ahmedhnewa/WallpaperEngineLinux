package com.ahmedraid.wallpaperenginelinux.models.wallpaperengine

class WallpaperEngineScene(
    val objects: List<Objects>
) {
    data class Objects(
        val image: String?
    )
}