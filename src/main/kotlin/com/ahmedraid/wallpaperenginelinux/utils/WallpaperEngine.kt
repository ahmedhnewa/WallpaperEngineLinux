package com.ahmedraid.wallpaperenginelinux.utils

import com.ahmedraid.wallpaperenginelinux.VideoPlayer
import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineProjectFile
import com.ahmedraid.wallpaperenginelinux.utils.extensions.*
import java.io.File
import java.util.*
import javax.swing.JOptionPane
import kotlin.concurrent.thread


class WallpaperEngine {

    fun setWallpaper(wallpaperEngineProjectFile: WallpaperEngineProjectFile, playAudio: Boolean = true) {
        val wallpaperPath = wallpaperEngineProjectFile.path
        val wallpaperId = getWallpaperIdByPath(wallpaperPath)
        if (playAudio) {
            val soundFilePath = findWallpaperSound(wallpaperPath)
            videoPlayer.playAudio(soundFilePath)
        } else {
            videoPlayer.stop()
        }

        val wallpaperType = wallpaperEngineProjectFile.type?.lowercase(Locale.getDefault()) ?: Constants.WallpapersType.NONE
        when (wallpaperType) {
//            Constants.WallpapersType.SCENE -> setWallpaperUsingLibrary(wallpaperPath)
            Constants.WallpapersType.SCENE -> setWallpaperAsImage(wallpaperEngineProjectFile)
            Constants.WallpapersType.APPLICATIONS -> JOptionPane.showMessageDialog(null, "Application wallpaper type is not implemented")
            Constants.WallpapersType.VIDEO -> {
//                playSound(wallpaperEngineProjectFile.file)
                setWallpaperUsingLibrary(wallpaperPath, 60)
            }
            Constants.WallpapersType.WEB -> {
                openUrl(
                    File(FilePathBuilder(wallpaperPath).child(wallpaperEngineProjectFile.file).build()).toURI()
                )
            } // JOptionPane.showMessageDialog(null, "Web is not implemented $wallpaperId")
            Constants.WallpapersType.NONE -> JOptionPane.showMessageDialog(null, "Can't get wallpaper type")
            else -> JOptionPane.showMessageDialog(null, "Can't Load this Wallpaper $wallpaperId")
        }

//        setWallpaperUsingLibrary(wallpaperPath)

        getConfiguration().apply {
            currentWallpaperId = getWallpaperIdByPath(wallpaperPath)
            saveConfiguration()
            logSuccess("$currentWallpaperId is the current wallpaper")
        }
    }

    private fun setWallpaperUsingLibrary(wallpaperPath: String, fps: Int = 144) {
        println(executeProcess("${getExecutableWallpaperEngineLibrary()} $wallpaperPath --fps $fps"))
    }

    private fun getWallpaperEngineLibraryPath() =
        FilePathBuilder(getHomeDirectory()).child("Documents").child("WallpaperEngineLinuxUtils")
            .child("linux-wallpaperengine-main").child("build").build()

    private fun getExecutableWallpaperEngineLibrary() = FilePathBuilder(getWallpaperEngineLibraryPath()).child("wallengine").build(false)

    fun getMp3Player(): VideoPlayer = videoPlayer

}