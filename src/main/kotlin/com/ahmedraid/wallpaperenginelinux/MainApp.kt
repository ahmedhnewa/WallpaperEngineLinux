package com.ahmedraid.wallpaperenginelinux

import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineProjectFile
import com.ahmedraid.wallpaperenginelinux.utils.extensions.*
import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import com.google.gson.Gson
import com.jthemedetecor.OsThemeDetector
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

fun main() {
    val detector = OsThemeDetector.getDetector()
    if (detector.isDark) FlatDarculaLaf.setup()
    else FlatLightLaf.setup()
    detector.registerListener { isDark: Boolean ->
        SwingUtilities.invokeLater {
            if (isDark) {
                FlatDarculaLaf.setup()
                logSuccess("Switched to dark theme")
            } else {
                // The OS switched to a light theme
                FlatLightLaf.setup()
                logSuccess("Switched to light theme")
            }
        }
    }

//    println("/home/ahmed01/.steam/steam/steamapps/workshop/content/431960/1384393936".toFile().parentf)
//    return

    /*val he = "overlay:\n  enabled: false\n  forceunhookgame: false\n  fps_enabled: false\n  warning_enabled: false\nuser:\n  closebehavior: CloseBehavior_Close\nuser:\n  start_on_windows_start: false"
    println(he)
    return*/

//    if (getProgramFiles().contains(" ")) error("Program file path contain white spaces")

//    logNote(getDesktopEnvironmentSimpleName("ubuntu:GNOME").lowercase())
//    logNote(getProgramFiles())
//    return
//    println(System.getenv("XDG_CURRENT_DESKTOP")); return
//    println(wallpaperPath)
//    println(wallpaperPath.trim())
//    println(getWallpaperIdByPath(wallpaperPath))
//    val extractedWallpaperPath = FilePathBuilder(getProgramFiles()).child("wallpapers").child(getWallpaperIdByPath("C:\\Users\\AhmedHnewa\\Documents\\steam\\steamapps\\workshop\\content\\431960\\818603284")).build()
//    println(extractedWallpaperPath)

//    val file = File("/home/ahmed01/Documents")
//    println(file.exists())
//    println(file.path)

//    println(getWallpaperIdByPath("C:\\Users\\AhmedHnewa\\Documents\\steam\\steamapps\\workshop\\content\\431960\\2787701631"))

    /*
    *
    * C:\Users\AhmedHnewa\Documents\steam\steamapps\workshop\content\431960/2787701631,
    *  ["C:\\Users\\AhmedHnewa\\Documents\\steam\\steamapps\\workshop\\content\\431960/2787701631"], \
    * [C:\Users\AhmedHnewa\Documents\steam\steamapps\workshop\content\431960/2787701631]
    * */

    /*val app = FilePathBuilder(File(getHomeDirectory())).child("Documents").child("test").child("app").build()

    println(app.exists())
    return*/
//    logSuccess(File("C:\\Users\\AhmedHnewa\\Documents\\steam\\steamapps\\workshop\\content\\431960\\2787701631").path)
//    return

//    println(FilePathBuilder(getHomeDirectory().toFile()).child("Documents").child("test").build())
//    return

    /*println(FilePathBuilder(getProgramFiles(false)).child("wallpapers").build())
    return*/

    val os = identifyOS()
    logSuccess("Program has been started")
    logNote("jarDir = ${getJarDirPath()}, currentDir = ${getCurrentDirectory()}, homeDir = ${getHomeDirectory()}")
    logOS(os)
    if (!isLinuxOS(os)) logError("This program is not designed for your OS")//exitProcess(0)

    if (isLinuxOS(os) && !isMonoInstalled()) {
        logError("Mono is not installed, Please install it first")
        exitProcess(0)
    } else if (isWindows(os)) logNote("No need for mono in Windows")
    else logSuccess("Mono is installed")
    validateRePKGFile()
    File("wallset").extractFileFromJar("exes/wallset")

//    println(executeProcess("gsettings set org.gnome.desktop.background picture-uri \"file:///home/ahmed01/.wallpaper-engine-linux/wallpapers/1674972620/WhatsApp Image 2019-02-23 at 20.52.25.jpg\""))

    loadWallpapers(getWorkshopDir())
}


@Throws(IOException::class)
private fun copyInputStreamToFile(inputStream: InputStream, file: File) {

    // append = false
    FileOutputStream(file, false).use { outputStream ->
        var read: Int
        val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
        while (inputStream.read(bytes).also { read = it } != -1) {
            outputStream.write(bytes, 0, read)
        }
    }
}

fun loadWallpapers(workshopWallpapersPath: String) {
    val folder = File(workshopWallpapersPath)
    val directories = folder.list { current, name -> File(current, name).isDirectory }
        ?: error("Can't load wallpapers, directories is null, please check your steam library path")

    logProcess("Extracting wallpapers...")
    directories.forEach { wallpaperId ->
        val wallpaperFolderPath = "$workshopWallpapersPath$wallpaperId"
        val wallpaperEngineProjectFile = getWallpaperProjectFile(wallpaperFolderPath)
        logSuccess(wallpaperEngineProjectFile.convertToJson())

        val wallpaperType = wallpaperEngineProjectFile.type?.lowercase() ?: "none"
        println(
            wallpaperType + " for ${wallpaperEngineProjectFile.title} ${getWallpaperIdByPath(wallpaperFolderPath)}" +
                    " ${wallpaperEngineProjectFile.file}, path = $wallpaperFolderPath"
        )

        if (getExtractedWallpaperPathById(wallpaperId).isEmpty()) {
            extractWallpaperPKG(wallpaperFolderPath)
        }
    }

    logNote("Window will show up soon...")

    val window = WallpapersWindow { wallpaperProjectFile ->
        getWallpaperEngine().setWallpaper(wallpaperProjectFile)
    }
    val pauseOnLossFocus = false
    window.addWindowFocusListener(object : WindowAdapter() {
        override fun windowGainedFocus(e: WindowEvent?) {
//            if (pauseOnLossFocus) videoPlayer.play()
            super.windowGainedFocus(e)
        }

        override fun windowIconified(e: WindowEvent?) {
            super.windowIconified(e)
        }

        override fun windowLostFocus(e: WindowEvent?) {
//            if (pauseOnLossFocus) videoPlayer.pause()
            super.windowLostFocus(e)
        }
    })
}
