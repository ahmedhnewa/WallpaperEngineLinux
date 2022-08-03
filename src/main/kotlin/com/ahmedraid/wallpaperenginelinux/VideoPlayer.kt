package com.ahmedraid.wallpaperenginelinux

import com.ahmedraid.wallpaperenginelinux.utils.extensions.logError
import com.ahmedraid.wallpaperenginelinux.utils.extensions.logSuccess
import jaco.mp3.player.MP3Player
import java.io.File
import java.net.URL

class VideoPlayer() {
    private var player: MP3Player = MP3Player()

    init {
        player.isRepeat = true

        /*player.border = BorderFactory.createEmptyBorder(50, 100, 50, 100)
        this.defaultCloseOperation = HIDE_ON_CLOSE
        this.contentPane.add(player)
        super.pack()
        this.setLocationRelativeTo(null)
        this.isVisible = showWindow
        this.isResizable = false*/
    }

    private fun addToPlayList(file: File) = player.addToPlayList(file)

    fun addToPlayList(url: URL) = player.addToPlayList(url)

    fun clearPlayList() = player.playList.clear()

    fun play() = player.play()

    fun stop() = player.stop()

    fun pause() = player.pause()

    fun playAudio(filePath: String) {
        val file = File(filePath)
        stop()
        if (file.exists()) {
            clearPlayList()
            addToPlayList(file)
            play()
            logError("Hellllllo ${file.exists()}")
            logSuccess("Playing ${file.path}")
        } else error("Playing ${file.path}")
    }
}