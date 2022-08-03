package com.ahmedraid.wallpaperenginelinux.utils

import java.io.File

object MacWallpaperChanger {
    fun changeWallpaper(file: File) {
        val list = arrayOf(
            "osascript",
            "-e", "tell application \"Finder\"",
            "-e", "set desktop picture to POSIX file \"" + file.getAbsolutePath() + "\"",
            "-e", "end tell"
        )
        val runtime = Runtime.getRuntime()
        val process: Process = runtime.exec(list)
    }
}