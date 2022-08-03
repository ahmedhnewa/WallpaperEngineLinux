package com.ahmedraid.wallpaperenginelinux.utils.extensions

import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineMaterialsProject
import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineModelsProject
import com.ahmedraid.wallpaperenginelinux.utils.WindowsWallpaperChanger
import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineProjectFile
import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineScene
import com.ahmedraid.wallpaperenginelinux.utils.Constants
import com.ahmedraid.wallpaperenginelinux.utils.MacWallpaperChanger
import java.io.File
import java.io.FileReader
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.io.path.toPath


fun extractWallpaperPKG(
    wallpaperPath: String,
    returnExtractedWallpaperPath: Boolean = true,
    /*isShouldPrintLog: Boolean = Constants.DEBUG,*/
    onError: (String) -> Unit = {},
    onSuccess: () -> Unit = {},
): String {

    val extractedWallpaperPath =
        FilePathBuilder(getProgramFiles()).child("wallpapers").child(getWallpaperIdByPath(wallpaperPath)).build()
    //"${getProgramFiles()}wallpapers${getFileSeparator()}${getWallpaperIdByPath(wallpaperPath)}${getFileSeparator()}"
    logNote("extracted $extractedWallpaperPath")
    logNote("wallpaperPath1 $wallpaperPath")
    val response =
        executeRePKG(
            "extract --overwrite -e tex,png,jpg,gif,mp3,mp4,json -o \"$extractedWallpaperPath\" $wallpaperPath${getFileSeparator()}scene.pkg",
            onNewLine = { response ->
                if (response.contains("Extracting package")) {
                    logSuccess(response)
                    onSuccess()
                } else if (response.lowercase(Locale.getDefault()).contains("error") || response.contains(
                        "Input file not",
                        ignoreCase = true
                    ) || response.contains(
                        "Cannot open assembly",
                        ignoreCase = true
                    ) || response.contains(
                        "No such file or directory",
                        ignoreCase = true
                    ) || response.contains("Skipping, already exists", ignoreCase = true)
                ) {
                    logError(response)
                    onError(response)
                } else {
                    logProcess(response)
                }
            },
            onFailed = {
                logError(it)
            })

    return if (returnExtractedWallpaperPath) {
        extractedWallpaperPath
    } else {
        response
    }
}

/*@Deprecated("useless method")
fun findWallpaperImages(folder: File): HashMap<String, String> {
    val images = hashMapOf<String, String>()
    val directoryListing = folder.listFiles()
    if (directoryListing != null) {

        for (file in directoryListing) {
            val fileName = file.name
            if (file.isImage()) {
                images[fileName] = File("${folder.path}/${fileName}").path
            }
        }

    } else {
        logError("Extracted Wallpaper Images folder is null!!")
    }
    return images
}*/

/*@Deprecated(message = "This is Deprecated, use findWallpaperImages() instead")
fun findWallpaperImagesOld(folder: File): HashMap<String, String> {
    val images = hashMapOf<String, String>()
    for (fileName in folder.list()) {
        if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith("jpeg")) {
            images[fileName] = File("${folder.path}/${fileName}").path
        }
    }
    return images
}*/

/*@Deprecated("use getWallpaperImageBackgroundPath() instead")
fun findWallpaperImage(extractedImages: HashMap<String, String>): String {
    var image: String? = getFileFromResourcesAsURL("images/error-wallpaper.jpeg")?.let { it.toURI()?.path } ?: ""
    if (extractedImages.values.isEmpty()) {
        logError("extractedImages.values.isEmpty() is either true, findWallpaperImage()")
        return image ?: ""
    }

    // FileFileFilter.FILE as FileFilter
    val directory = File(extractedImages.values.first()).parentFile
    if (directory != null) {
        val files: Array<File> = directory.listFiles(isValidImage)

        val filesSize = hashMapOf<Long, String>()
        val filesSizeList = mutableListOf<Long>()
        for (file in files) {
            val bytes = file.length()

            val kilobytes = bytes / 1024

            if (isDebug()) {
                logNote("fileName '${file.name}', fileSize= $kilobytes kilobytes")
            }
            filesSize[kilobytes] = file.path
            filesSizeList.add(kilobytes)
        }

        val max = Collections.max(filesSizeList)

        if (filesSize[max] != null) {
            image = filesSize[max]!!
        }
        if (isDebug()) {
            logNote("Biggest image size is max kilobytes")
        }
    } else {
        logError("Can't find image, maybe collection is empty")
    }

    return image ?: ""
}*/


fun getWallpaperImageBackgroundPath(wallpaperImagesFolder: File): String {
    var image = ""
    if (!wallpaperImagesFolder.exists()) {
        logError("There is not any images because wallpaper images folder is not exists")
        return image
    }
    val dir: Array<File>? = wallpaperImagesFolder.listFiles()
    val fileSizeList = mutableMapOf<Long, String>().toMutableMap()

    dir?.let {
        it.forEachIndexed { index: Int, file: File ->
            if (file.isImage()) {
                fileSizeList[file.length()] = file.path
            }
        }
        val max: Long = Collections.max(fileSizeList.keys)
        image = fileSizeList[max] ?: ""
    }
    return image
}

fun setWallpaperAsImage(wallpaperEngineProjectFile: WallpaperEngineProjectFile) {
    val wallpaperPath = wallpaperEngineProjectFile.path
    val extractedWallpaperPath =
        getExtractedWallpaperPathByWallpaperPath(wallpaperPath)
    logSuccess(extractedWallpaperPath)
    /*val images = findWallpaperImages(wallpaperImagesFolder)
    val image = findWallpaperImage(images)*/
//    val image = getWallpaperImageBackgroundPath(wallpaperImagesFolder)
    val image = try {
        getRightWallpaperImageBackgroundPath(extractedWallpaperPath)
    } catch (e: Exception) {
        ""
    }

    if (image.isEmpty()) {
        logError("image is null, setWallpaperAsImage fun")
        return
    }
    logProcess("\nSetting $image as wallpaper...")
    changeOsWallpaper(image)
}

fun getRightWallpaperImageBackgroundPath(
    extractedWallpaperPath: String
): String {
    var image = ""
    val extractedWallpaperFolder = File(extractedWallpaperPath)
    var file = extractedWallpaperFolder.filePathBuilder().child("scene.json").build().toFile()

    val wallpaperEngineScene = getGson().fromJson(file.readText(), WallpaperEngineScene::class.java)
    if (wallpaperEngineScene.objects.isNotEmpty()) {
        wallpaperEngineScene.objects[0].image?.let {
            file = extractedWallpaperFolder/*.child("models")*/.filePathBuilder().child(it).build().toFile()
            val wallpaperEngineProject = getGson().fromJson(file.readText(), WallpaperEngineModelsProject::class.java)

            if (wallpaperEngineProject.material.isNotEmpty()) {
                file =
                    extractedWallpaperFolder.filePathBuilder().child(wallpaperEngineProject.material).build().toFile()
                val wallpaperEngineMaterialProject =
                    getGson().fromJson(file.readText(), WallpaperEngineMaterialsProject::class.java)
                val tmpImg = wallpaperEngineMaterialProject.passes[0].textures[0]

                if (tmpImg.isNotEmpty()) {
                    image = "${
                        extractedWallpaperFolder.filePathBuilder().child("materials").child(tmpImg).build()
                            .toFile().path
                    }.jpg"
                    file = File(image)
                    if (file.exists()) return image
                    else {
                        image = "${
                            extractedWallpaperFolder.filePathBuilder().child("materials").child(tmpImg).build()
                                .toFile().path
                        }.png"
                        file = File(image)
                        if (file.exists()) return image
                        else {
                            // Get highest image size
                            file = extractedWallpaperFolder.filePathBuilder().child("materials").build().toFile()
                            image = getWallpaperImageBackgroundPath(
                                file
                            )
                        }
                    }
                    logSuccess(image)
                }
            }
        }
    }
    return image
}

fun changeOsWallpaper(image: String, os: String = identifyOS()) {
    val imageFile = File(image)
    if (!imageFile.exists()) {
        logError("$image is not exists")
        return
    }
    if (isWindows(os)) {
        WindowsWallpaperChanger.changeWallpaper(image)
    } else if (isLinuxOS(os)) {
        val imageUri = imageFile.toURI()
        logSuccess(imageUri.toString())

        var command = ""
        when (val desktopEnvironment = identifyDesktopEnvironment()) {
            "xfce" -> executeProcess("xfconf-query -c xfce4-desktop -p /backdrop/screen0/monitorVGA-1/workspace0/last-image -s \"$image\"")
//            "gnome" -> executeProcess("gsettings set org.gnome.desktop.background picture-uri \"$imageUri\"")
            "gnome" -> {
                executeProcess("gsettings set org.gnome.desktop.background draw-background false")
                Runtime.getRuntime().exec("gsettings set org.gnome.desktop.background picture-uri \"$imageUri\"") //executeProcess()
                executeProcess("gsettings set org.gnome.desktop.background draw-background true")
//                executeProcess(" gsettings set org.gnome.desktop.background picture-uri \"file://$image\" && gsettings set org.gnome.desktop.background draw-background true")
            }
            "kde" -> executeProcess("qdbus org.kde.plasmashell /PlasmaShell org.kde.PlasmaShell.evaluateScript 'var allDesktops = desktops();print (allDesktops);for (i=0;i<allDesktops.length;i++) {d = allDesktops[i];d.wallpaperPlugin = \"org.kde.image\";d.currentConfigGroup = Array(\"Wallpaper\", \"org.kde.image\", \"General\");d.writeConfig(\"Image\", \"$image\")}'")
            "unity" -> executeProcess("gsettings set org.gnome.desktop.background picture-uri \"file://$image\"")
            "cinnamon" -> executeProcess("gsettings set org.cinnamon.desktop.background picture-uri  \"file://$image\"")
            else -> logError("Can't recognize DE: $desktopEnvironment")
        }
    } else if (isMac()) {
        MacWallpaperChanger.changeWallpaper(image.toFile())
    } else logError("Can't recognize installed OS: $os")
}

fun getSelectedWallpaper(): String {
    /*val currentWallpaperUri = executeProcess("gsettings get org.gnome.desktop.background picture-uri").replace("'", "").replace("'", "")
    val selectedWallpaper = URI(currentWallpaperUri).toPath().toFile().parentFile.parentFile
    if (!selectedWallpaper.exists()) return ""

    if (getWallpaperIdByExtractedPath(selectedWallpaper.path).isNotEmpty()) {
        return selectedWallpaper.path
    }
    return ""*/
    return getConfiguration().currentWallpaperId
}

fun getWallpaperProjectFile(wallpaperPath: String): WallpaperEngineProjectFile {
    val wallpaperProjectFileJson = FilePathBuilder(wallpaperPath).child("project.json").build().toFile().readText()
    return getGson().fromJson(wallpaperProjectFileJson, WallpaperEngineProjectFile::class.java)
}

fun findWallpaperSound(wallpaperPath: String): String {
    val extractedWallpaperPath = getExtractedWallpaperPathByWallpaperPath(wallpaperPath)
    val extractedWallpaperSoundFolder = File("${extractedWallpaperPath}sounds")
    if (extractedWallpaperSoundFolder.exists()) {
        val files = extractedWallpaperSoundFolder.listFiles()
        if (files != null) {
            val firstFile = files[0]
            return firstFile.path
        }
    }
    return ""
}

fun playSound(soundPath: String) = getWallpaperEngine().getMp3Player().playAudio(soundPath)

//    "$steamLibraryPath/steamapps/workshop/content/${Constants.WALLPAPER_ENGINE_APP_ID}"

fun getWallpaperEnginePath(steamLibraryPath: String = getSteamLibraryPath()): String =
    FilePathBuilder(steamLibraryPath).child("steamapps").child("common").child("wallpaper_engine").build()

fun getWorkshopDir(steamLibraryPath: String = getSteamLibraryPath()): String =
    FilePathBuilder(steamLibraryPath).child("steamapps").child("workshop")
        .child("content").child(Constants.WALLPAPER_ENGINE_APP_ID).build()

private fun getWallpaperEngineProjects(steamLibraryPath: String = getSteamLibraryPath()) =
    FilePathBuilder(getWallpaperEnginePath(steamLibraryPath).removeLastFileSeparator()).child("projects")

fun getDefProjectsDir(): String = getWallpaperEngineProjects().child("defaultprojects").build()
//    "$steamLibraryPath/steamapps/common/wallpaper_engine/projects/defaultprojects"

fun getMyProjectsDir(): String = getWallpaperEngineProjects().child("myprojects").build()
//    "$steamLibraryPath/steamapps/common/wallpaper_engine/projects/myprojects"

/*fun getProjectDirs(steamLibraryPath: String = getSteamLibraryPath()) = listOf(
    getWorkshopDir(steamLibraryPath),
    getDefProjectsDir(steamLibraryPath),
    getMyProjectsDir(steamLibraryPath)
)*/

fun getWorkshopUrl(workShopId: String) = "https://steamcommunity.com/sharedfiles/filedetails/?id=$workShopId"

fun getWallpaperIdByPath(wallpaperPath: String): String {
    if (wallpaperPath.endsWith(getFileSeparator())) throw FileSeparatorException(wallpaperPath)
    /*val delimiters =
        FilePathBuilder().child("steamapps").child("workshop").child("content").child(Constants.WALLPAPER_ENGINE_APP_ID)
            .build()
    if (wallpaperPath.isNotEmpty()) {
        val split = wallpaperPath.trim().split(delimiters)
        if (split.isNotEmpty()) {
            return try {
                split[1] //["/home/username/.steam/steam/","2067099479"]
            } catch (e: IndexOutOfBoundsException) {
                logError("$wallpaperPath, ${split.convertToJson()}, $split, ${e.message}")
                ""
            }
        }
    }
    logError("can't get wallpaper id by path using getWallpaperIdByPath()")*/
    return wallpaperPath.substring(wallpaperPath.lastIndexOf("/")).replace(getFileSeparator(), "")
}

fun getWallpaperIdByExtractedPath(wallpaperPath: String): String {
    val delimiters = FilePathBuilder().child("/${Constants.PROGRAM_FILE_NAME}/wallpapers/").build()
    if (wallpaperPath.isNotEmpty()) {
        val split = wallpaperPath.trim().split(delimiters)
        return try {
            split[1]
        }catch (e: IndexOutOfBoundsException) {
            logError("$wallpaperPath, ${split.convertToJson()}, $split, ${e.message}")
            ""
        }
    }
    return ""
}

fun getWallpaperPathById(wallpaperId: String): String {
    var wallpaperPath = "${getWorkshopDir()}${getFileSeparator()}$wallpaperId"
    var wallpaperFile = File(wallpaperPath)

    if (wallpaperFile.exists()) {
        return wallpaperPath
    } else {
        wallpaperPath = "${getWorkshopDir()}${getFileSeparator()}$wallpaperId"
        wallpaperFile = File(wallpaperPath)
        return if (wallpaperFile.exists()) {
            wallpaperPath
        } else {
            wallpaperPath = "${getDefProjectsDir()}${getFileSeparator()}$wallpaperId"
            wallpaperFile = File(wallpaperPath)
            if (wallpaperFile.exists()) {
                wallpaperPath
            } else {
                ""
            }
        }
    }
}

fun getExtractedWallpaperPathById(wallpaperId: String): String {
    val extractedWallpaperPath = FilePathBuilder(getProgramFiles()).child(Constants.ProgramFiles.WALLPAPERS).child(wallpaperId).build()
    val extractedWallpaperFile = File(extractedWallpaperPath)
    return if (extractedWallpaperFile.exists()) {
        extractedWallpaperPath
    } else {
        ""
    }
}

fun getExtractedWallpaperPathByWallpaperPath(wallpaperPath: String): String {
    val wallpaperFile = File(wallpaperPath)
    return if (wallpaperFile.exists()) {
        val wallpaperId = getWallpaperIdByPath(wallpaperPath)
        val extractedWallpaperPath = FilePathBuilder(getProgramFiles()).child("wallpapers").child(wallpaperId)
            .build() //"${getProgramFiles()}wallpapers${getFileSeparator()}$wallpaperId"
        logNote("extractedWallpaperPath")
        extractedWallpaperPath
    } else {
        logError("extracted wallpaper path is not exists")
        ""
    }
}

fun getWallpaperPreviewIcon(wallpaperEngineProjectFile: WallpaperEngineProjectFile, wallpaperPath: String): URL {
    var wallpaperPreviewIconURL = getFileAsURL("$wallpaperPath/${wallpaperEngineProjectFile.preview}")
    var wallpaperPreviewIconFile = File(wallpaperPreviewIconURL.toURI().path)
    if (!wallpaperPreviewIconFile.exists()) {
        wallpaperPreviewIconURL = getFileAsURL("$wallpaperPath/preview.jpg")
        wallpaperPreviewIconFile = File(wallpaperPreviewIconURL.toURI().path)
        if (!wallpaperPreviewIconFile.exists()) {
            logNote(
                "There is no wallpaper preview image for ${wallpaperEngineProjectFile.path}, will use ${
                    getFileFromResourcesAsURL("images/wallpaper-preview.png")?.toURI().toString()
                } instead"
            )
            return getFileFromResourcesAsURL("images/wallpaper-preview.png")!!
        }
    }

    return wallpaperPreviewIconURL
}