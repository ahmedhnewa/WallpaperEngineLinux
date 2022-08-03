package com.ahmedraid.wallpaperenginelinux.utils.extensions

import com.ahmedraid.wallpaperenginelinux.VideoPlayer
import com.ahmedraid.wallpaperenginelinux.models.ConfigurationModel
import com.ahmedraid.wallpaperenginelinux.utils.Constants
import com.ahmedraid.wallpaperenginelinux.utils.WallpaperEngine
import com.google.gson.Gson
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import kotlin.system.exitProcess


private var gson: Gson = Gson()
private var wallpaperEngine: WallpaperEngine = WallpaperEngine()
fun getGson(): Gson = gson
fun getWallpaperEngine(): WallpaperEngine = wallpaperEngine

fun Any.convertToJson(): String {
    return getGson().toJson(this)
}

fun executeProcess(
    s: String,
    onNewLine: (String) -> Unit = {},
    onEnd: (String) -> Unit = {},
    onFailed: (String) -> Unit = {}
): String {
    if (isWindows()) {
        return s.runCommand() ?: ""
    }
    val pb = ProcessBuilder("bash", "-c", s)
    pb.redirectErrorStream(true)
    var p: Process? = null
    try {
        p = pb.start()
    } catch (e: IOException) {
        e.printStackTrace()
        onFailed(e.message ?: "Unknown Error")
        return ""
    }
    val reader = p.inputStream.bufferedReader()//BufferedReader(InputStreamReader(p!!.inputStream))
    val res = StringBuilder()
    var line: String?
    try {
        while (reader.readLine().also {
                line = it; it?.let {
                onNewLine(it)
            }
            } != null) {
            res.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    onEnd(res.toString())
    return res.toString()
}

/*fun executeProcess(s: String): String {
    val pb = ProcessBuilder("bash", "-c", s)
    pb.redirectErrorStream(true)
    var p: Process? = null
    try {
        p = pb.start()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val reader = BufferedReader(InputStreamReader(p!!.inputStream))
    val res = StringBuilder()
    var line: String?

    while(reader.readLine().also { line = it } != null) {
        res.appendLine(line)
    }

    p.destroy()

    return res.toString()
}*/

/*fun String.runCommand(workingDir: File): String? {
    return try {
        val parts = this.split("\\s".toRegex())
        val process = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        process.waitFor(60, TimeUnit.MINUTES)
        process.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}*/

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 2,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String? = runCatching {
    ProcessBuilder("\\s".toRegex().split(this))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
        .inputStream.bufferedReader().readText()
}.onFailure { it.printStackTrace() }.getOrNull()

fun getDefaultClassLoader(): ClassLoader = Thread.currentThread().contextClassLoader

fun getRePKGFromResources(classLoader: ClassLoader = getDefaultClassLoader()): String =
    getFilePath("RePKG.exe") //getFilePathFromResources("exes/RePKG.exe", classLoader)
//fun Any.getRePKGFromResources(): String = javaClass.classLoader.getResource("RePKG.exe").toURI().path

fun getCustomRePKGFromResources(classLoader: ClassLoader = getDefaultClassLoader()): String =
    getFilePathFromResources("exes/RePKG.exe") //getFileFromResourcesAsInputSteam("exes/RePKG.exe").toFile().path

fun getRePKGFromPath(path: String = FilePathBuilder(getJarDirPath().path).child("RePKG.exe").build()): String = path

fun executeRePKG(
    command: String,
    exePath: String = getRePKGFromPath(),
    onNewLine: (String) -> Unit = {},
    onFailed: (String) -> Unit = {}
): String {
    val os = identifyOS()
    return if (isWindows(os)) {
        executeProcess("$exePath $command", onNewLine = onNewLine, onFailed = onFailed)
    } else if (isLinuxOS(os)) {
        executeProcess("mono $exePath $command", onNewLine = onNewLine, onFailed = onFailed)
    } else {
        error("OS is not supported")
        ""
    }
}

//fun isMonoInstalled(executeProcess: String = executeProcess("mono")): Boolean = !executeProcess.contains("command not found") || !executeProcess.contains("The system cannot find the file specified")
fun isMonoInstalled(executeProcess: String = executeProcess("mono")): Boolean =
    executeProcess.contains("Usage is: mono [options] program [program-options]")

fun identifyOS(): String {
    return System.getProperty("os.name")
}

fun logOS(os: String = identifyOS()) {
    if (os.contains("Windows")) logSuccess("You are using Windows, $os")
    else if (os == "Linux") {
        val de = identifyDesktopEnvironment()
        logSuccess("You are using Linux, ${de.uppercase(Locale.getDefault())} desktop environment")
    } else logError("Can't recognize installed OS: $os")

    println()
    logNote(identifyDesktopEnvironment())
    val env = System.getenv().toList()
    for (i in env) {
        println(i)
    }
    println()

    System.getProperties().forEach {
        logNote(it.toString())
    }

    println()
}

fun isLinuxOS(os: String = identifyOS()) = os == "Linux"

fun isWindows(os: String = identifyOS()) = os.contains("Windows")

fun identifyDesktopEnvironment(): String {
    if (!isLinuxOS()) throw NotLinuxLinuxDesktopEnvironment()
    var simpleDesktopEnvironmentName = ""

    var de: String = System.getenv("XDG_CURRENT_DESKTOP")?.lowercase(Locale.getDefault()) ?: ""
    simpleDesktopEnvironmentName = getDesktopEnvironmentSimpleName(de)
    if (simpleDesktopEnvironmentName.isNotEmpty()) return simpleDesktopEnvironmentName
    logError("Not identifiable with: echo \$XDG_CURRENT_DESKTOP: $de")

    de = System.getenv("GDM_SESSION")?.lowercase(Locale.getDefault()) ?: ""
    simpleDesktopEnvironmentName = getDesktopEnvironmentSimpleName(de)
    if (simpleDesktopEnvironmentName.isNotEmpty()) return simpleDesktopEnvironmentName
    logError("Not identifiable with: echo \$GDM_SESSION: $de")

    de = System.getProperty("sun.desktop")?.lowercase(Locale.getDefault()) ?: ""
    simpleDesktopEnvironmentName = getDesktopEnvironmentSimpleName(de)
    if (simpleDesktopEnvironmentName.isNotEmpty()) return simpleDesktopEnvironmentName
    logError("Not identifiable with: system property(sun.desktop): $de")
    return simpleDesktopEnvironmentName
}

fun getDesktopEnvironmentSimpleName(de: String): String {
    return if (de.contains("xfce") || de.endsWith("xfce", ignoreCase = true)) {
        "xfce"
    } else if (de.contains("kde") || de.endsWith("kde", ignoreCase = true)) {
        "kde"
    } else if (de.contains("unity") || de.endsWith("unity", ignoreCase = true)) {
        "unity"
    } else if (de.contains("gnome") || de.endsWith("gnome", ignoreCase = true)) {
        "gnome"
    } else if (de.contains("cinnamon") || de.endsWith("cinnamon", ignoreCase = true)) {
        "cinnamon"
    } else if (de.contains("mate") || de.endsWith("mate", ignoreCase = true)) {
        "mate"
    } else if (de.contains("deepin") || de.endsWith("deepin", ignoreCase = true)) {
        "deepin"
    } else if (de.contains("budgie") || de.endsWith("budgie", ignoreCase = true)) {
        "budgie"
    } else if (de.contains("lxqt") || de.endsWith("lxqt", ignoreCase = true)) {
        "lxqt"
    } else {
        ""
    }
}

fun getUsername(): String = System.getProperty("user.name")

fun getCurrentDirectory(): String = System.getProperty("user.dir")

fun getHomeDirectory(): String = System.getProperty("user.home")

fun getProgramFiles(putLastFileSeparator: Boolean = false): String =
    FilePathBuilder(getJarDirPath().path).child(Constants.PROGRAM_FILE_NAME).build(putLastFileSeparator)
//    "${getJarDirPath().path}${getFileSeparator()}ExtractedWallpapers${getFileSeparator()}"

fun getConfigurationFilePath(): String =
    FilePathBuilder(getProgramFiles()).child(Constants.ProgramFiles.CONFIGURATION_FILE_NAME).build()

private fun getConfigurationFile(): File =
    File(getConfigurationFilePath())

fun openUrl(url: URI) {
    Desktop.getDesktop().browse(url)
}

fun getDefaultSteamLibraryPath(os: String = identifyOS()): String {
    val file: File = (if (isLinuxOS(os)) FilePathBuilder(getHomeDirectory()).child(".steam").child("steam")
        .build(false) else FilePathBuilder("C:").child("Program Files (x86)").child("Steam").build()).toFile()
    return if (file.exists()) file.path else ""
}

fun getDefaultConfigurationContents(): ConfigurationModel = getGson().fromJson<ConfigurationModel>(
    Constants.DEFAULT_CONFIGURATION_JSON.replace("user", getDefaultSteamLibraryPath()),
    ConfigurationModel::class.java
)

var isFirstTime = true
fun getConfiguration(configurationFile: File = getConfigurationFile()): ConfigurationModel {

    var configurationModel: ConfigurationModel = getDefaultConfigurationContents()

    if (configurationFile.exists() && !configurationFile.isDirectory) {

        try {
            val configurationFileContents = configurationFile.readText()
            configurationModel =
                getGson().fromJson(configurationFileContents, ConfigurationModel::class.java)

            if (isFirstTime) {
                isFirstTime = false
                logSuccess("File is exists, Content: $configurationFileContents")
                logNote("Steam library path = ${configurationModel.steamLibraryPath}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    } else {

        println("Configuration file is not exits, Let's create a new configuration file...")
        var steamLibraryPath = getDefaultSteamLibraryPath()
        if (steamLibraryPath.isEmpty()) steamLibraryPath = choseSteamLibraryDir()
        configurationModel = ConfigurationModel(steamLibraryPath = steamLibraryPath)
        if (configurationModel.steamLibraryPath.isNotEmpty()) configurationModel.saveConfiguration()
    }

    return configurationModel
}

fun choseSteamLibraryDir(): String {
    val fileChooser = JFileChooser()

    fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    // disable the "All files" option.
    fileChooser.isAcceptAllFileFilterUsed = false
    fileChooser.isFileHidingEnabled = false
    fileChooser.dialogTitle = "Select your steam library"
    val returnValue = fileChooser.showOpenDialog(null)

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        var file = fileChooser.selectedFile
        println(fileChooser.selectedFile.path)

        val path = fileChooser.selectedFile.path
        val name = file.name
        file = File(path)

        println("Opening $name from \n ${file.path} \n \n")

        return path
    } else {
        println("Open file chooser cancelled by user")
    }
    exitProcess(0)
    return ""
}

fun getSteamLibraryPath() = getConfiguration().steamLibraryPath

fun getAssetsPath(steamLibrary: String): String = "$steamLibrary/steamapps/common/wallpaper_engine/assets"

fun isDebug(): Boolean = Constants.DEBUG

fun showMessageDialog(msg: Any) = JOptionPane.showMessageDialog(null, msg)

val videoPlayer: VideoPlayer by lazy {
    VideoPlayer()
}

fun validateRePKGFile(file: File = File(getRePKGFromPath())) {
    if (!file.exists() || file.isDirectory || !isValidFile(file, Constants.REPKG_SHA265)) {
        if (!file.delete()) {
            logNote("Can't delete RePKG.exe, Maybe the file is not exists")
        } else logSuccess("${file.name} deleted")
        file.extractFileFromJar("exes/RePKG.exe")
    }
}

//inline fun <reified T> Gson.(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)