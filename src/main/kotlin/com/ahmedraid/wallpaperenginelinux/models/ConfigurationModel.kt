package com.ahmedraid.wallpaperenginelinux.models

import com.ahmedraid.wallpaperenginelinux.utils.extensions.convertToJson
import com.ahmedraid.wallpaperenginelinux.utils.extensions.createFile
import com.ahmedraid.wallpaperenginelinux.utils.extensions.getConfigurationFilePath
import com.ahmedraid.wallpaperenginelinux.utils.extensions.getDefaultSteamLibraryPath
import java.io.File

data class ConfigurationModel(
    var steamLibraryPath: String = getDefaultSteamLibraryPath(),
    var currentWallpaperId: String = ""
) {
    fun saveConfiguration() = File(getConfigurationFilePath()).createFile(this.convertToJson())

}