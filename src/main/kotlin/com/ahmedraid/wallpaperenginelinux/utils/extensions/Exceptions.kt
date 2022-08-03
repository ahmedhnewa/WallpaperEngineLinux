package com.ahmedraid.wallpaperenginelinux.utils.extensions

class FileSeparatorException(wallpaperPath: String = "") :
    Exception("Wallpaper path should not end with file separator ${getFileSeparator()}, $wallpaperPath")

class FilePathEmptyException(s: String = ""): Exception("File path should not be empty, $s")

class NotLinuxLinuxDesktopEnvironment(): Exception("You can get desktop environment name only in Linux")