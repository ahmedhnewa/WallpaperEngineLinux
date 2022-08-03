package com.ahmedraid.wallpaperenginelinux.utils.extensions

import com.ahmedraid.wallpaperenginelinux.WallpapersWindow
import com.ahmedraid.wallpaperenginelinux.utils.Constants
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFileFilter
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*
import javax.activation.MimetypesFileTypeMap
import kotlin.system.exitProcess


fun File.isImage(): Boolean {
    val mimetype: String = MimetypesFileTypeMap().getContentType(this)
    val type = mimetype.split("/").toTypedArray()[0]
    return type == "image"
}

fun isImage(filePath: String): Boolean = File(filePath).isImage()
//fun File.isImage(): Boolean = this.name.endsWith(".jpg") || this.name.endsWith(".img")

// org.apache.commons.io.filefilter.FileFileFilter.FILE is Deprecated Use FileFileFilter.INSTANCE.
val isValidImage = FileFilter { pathname ->
    val name = pathname.name
    var ext: String? = null
    val filter = FileFileFilter.INSTANCE as FileFilter
    if (!filter.accept(pathname)) {
        return@FileFilter false
    }
    val i = name.lastIndexOf('.')
    if (i > 0 && i < name.length - 1) {
        ext = name.substring(i + 1).lowercase(Locale.getDefault())
    }
    if (ext == null) false else if (ext != "jpg" && ext != "jpeg" && ext != "png" && ext != "gif") false else true
}

fun createFile(content: String, path: String): Boolean {
    return try {
        val file = File(path)
        file.parentFile.mkdirs()
        val fileWriter = FileWriter(file)
        fileWriter.write(content)
        fileWriter.close()
        true
    } catch (e: IOException) {
        println("Failed to create file ${e.message}")
        e.printStackTrace()
        false
    }
}

fun File.createFile(content: String, path: String = this.path) {
    com.ahmedraid.wallpaperenginelinux.utils.extensions.createFile(content, path)
}

fun File.readFile(): String {
    val bufferReader = this.bufferedReader()
    return bufferReader.use { it.readText() }
}

fun getFileFromResourcesAsURL(
    fileName: String,
    classLoader: ClassLoader = getDefaultClassLoader()
): URL? = classLoader.getResource(fileName)

fun getFilePath(fileName: String): String = File(fileName).absolutePath

fun getFilePathResources(fileName: String, fileDir: String = Constants.FileDirs.DEFAULT) =
    "$fileDir$fileName"

fun getFilePathFromResources(
    fileName: String,
    classLoader: ClassLoader = getDefaultClassLoader()
): String = Paths.get(getFileFromResourcesAsURL(fileName, classLoader)!!.toURI()).toFile().path

fun getFileFromResourcesAsInputSteam(
    fileName: String,
    classLoader: ClassLoader = getDefaultClassLoader()
): InputStream = classLoader.getResourceAsStream(fileName)!!

/*fun getFileFromInputSteam(
    inputStream: InputStream,
    onSuccess: (File) -> Unit = {},
    onFailed: (String) -> Unit = {}
) {
    val file: File? = null
    try {
        FileOutputStream(file).use { outputStream -> IOUtils.copy(inputStream, outputStream) }
        onSuccess(file!!)
    } catch (e: FileNotFoundException) {
        val errorMsg = e.message!!
        logError(errorMsg)
        e.printStackTrace()
    } catch (e: IOException) {
        val errorMsg = e.message!!
        logError(errorMsg)
        e.printStackTrace()
    }
}*/
/*
fun getFileFromInputSteam(inputStream: InputStream): File? {
    val file: File? = null
    FileUtils.copyInputStreamToFile(inputStream, file)
    return file
}*/

//fun InputStream.toFile(): File = getFileFromInputSteam(this) ?: File("")

fun getFileAsURL(path: String): URL = File(path).toURI().toURL()

// Temp
/*fun Any.getFile(fileName: String) {
    javaClass.getResourceAsStream("/$fileName").use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val contents = reader.lines()
                .collect(Collectors.joining(System.lineSeparator()))
        }
    }
}*/

@Throws(IOException::class)
fun saveImage(imageUrl: String?, destinationFile: String) {
    val url = URL(imageUrl)
    val `is` = url.openStream()
    val os: OutputStream = FileOutputStream(destinationFile)
    val b = ByteArray(2048)
    var length: Int
    while (`is`.read(b).also { length = it } != -1) {
        os.write(b, 0, length)
    }
    `is`.close()
    os.close()
}

@Throws(IOException::class)
private fun getFileChecksum(digest: MessageDigest, file: File): String {
    //Get file input stream for reading the file content
    val fis = FileInputStream(file)

    //Create byte array to read data in chunks
    val byteArray = ByteArray(1024)
    var bytesCount = 0

    //Read file data and update in message digest
    while (fis.read(byteArray).also { bytesCount = it } != -1) {
        digest.update(byteArray, 0, bytesCount)
    }

    //close the stream; We don't need it now.
    fis.close()

    //Get the hash's bytes
    val bytes = digest.digest()

    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    val sb = StringBuilder()
    for (i in bytes.indices) {
        sb.append(Integer.toString((bytes[i].toInt() and 0xff) + 0x100, 16).substring(1))
    }

    //return complete hash
    return sb.toString()
}

fun getSHA265(file: File): String {
    val shaDigest = MessageDigest.getInstance("SHA-256")
    return try {
        val shaChecksum = getFileChecksum(shaDigest, file)
        shaChecksum
    } catch (ex: IOException) {
        "Unknown"
    }
}

fun isValidFile(file: File, sha256: String): Boolean = getSHA265(file) == sha256

fun File.extractFileFromJar(
    fileName: String,
    exitIfFailed: Boolean = true,
    onSuccess: () -> Unit = {},
    onFailed: (String) -> Unit = {}
) {
    val resource: InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
    if (resource == null || this.isDirectory) {
        throw IllegalArgumentException("File not found")
    } else {

        // failed if files have whitespaces or special characters

        try {
            FileUtils.copyInputStreamToFile(resource, this)
            logSuccess("File ${this.name} extracted from $fileName (in jar file) to ${this.absolutePath}")
            onSuccess()
        } catch (e: IOException) {
            logError("Can't extracted File ${this.name} from $fileName (in jar file) to ${this.absolutePath}, error = ${e.message}")
            onFailed(e.message ?: "Unknown error")
            e.printStackTrace()
            if (exitIfFailed) exitProcess(0)
        }
        resource.close()
    }
}

class FilePathBuilder(filePath: String = "") {
    private val stringBuilder = StringBuilder()

    init {
        stringBuilder.append(filePath)
    }

    fun child(path: String, endsWithFileSeparator: Boolean = path.endsWithFileSeparator()): FilePathBuilder {
        if (path.isEmpty()) throw FilePathEmptyException(path)
        if (endsWithFileSeparator) stringBuilder.append(path)
        else stringBuilder.append("${getFileSeparator()}$path")
        return this
    }

    fun child(file: File): FilePathBuilder = child(file.path)

    fun build(putLastFileSeparator: Boolean = true): String {
        if (putLastFileSeparator) stringBuilder.append(File.separator)
        return stringBuilder.toString()
    }

}

fun File.filePathBuilder() = FilePathBuilder().child(this)

fun String.pathWithOsFileSeparator(): String {
    val stringBuilder: StringBuilder = StringBuilder("")
    this.split("/", "\\").forEach {
        stringBuilder.append(it + getFileSeparator())
    }
    return stringBuilder.toString()
}

fun String.removeLastFileSeparator(): String = Paths.get(this).toString() /*{
    val stringBuilder: StringBuilder = StringBuilder("")
    val list = this.split("/", "\\")
    list.forEachIndexed { index, item ->
        logError("item $item")
        if (index != list.lastIndex) {
            stringBuilder.append(item + getFileSeparator())
        }
        // Not Implemented
    }
    return stringBuilder.toString()
}*/

fun String.endsWithFileSeparator() = this.endsWith(getFileSeparator())

/*fun pathJoin(delimiter: String, vararg elements: CharSequence) =
    java.lang.String.join(File.separator, elements)*/

fun getFileSeparator(): String = System.getProperty("file.separator") //File.separator

fun getJarDirPath(): File = File(WallpapersWindow::class.java.protectionDomain.codeSource.location.toURI()).parentFile