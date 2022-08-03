package com.ahmedraid.wallpaperenginelinux.utils

object Constants {
    const val DEFAULT_CONFIGURATION_JSON = "{\"steamLibraryDir\": \"user\"}"
    const val WALLPAPER_ENGINE_APP_ID = "431960"
    const val DEBUG = false
    const val REPKG_SHA265 = "c18de5340d45b76a3f8d30eb217207896178b22d9145f2345b26ee62ea165181"
    // https://developer.valvesoftware.com/wiki/Steam_browser_protocol

    object FileDirs {
        const val EXES = "/exes/"
        const val IMAGES = "/images/"
        const val DEFAULT = ""
    }

    const val PROGRAM_FILE_NAME: String = "WallpaperHnewa"
    object ProgramFiles {
        const val CONFIGURATION_FILE_NAME = "configuration.json"
        const val WALLPAPERS = "wallpapers"
    }

    object ColorCodes {
        const val ANSI_RESET = "\u001B[0m"

        const val ANSI_BLACK = "\u001B[30m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
        const val ANSI_PURPLE = "\u001B[35m"
        const val ANSI_CYAN = "\u001B[36m"
        const val ANSI_WHITE = "\u001B[37m"

        // Regular Colors
        const val BLACK = "\u001b[0;30m" // BLACK
        const val RED = "\u001b[0;31m" // RED
        const val GREEN = "\u001b[0;32m" // GREEN
        const val YELLOW = "\u001b[0;33m" // YELLOW
        const val BLUE = "\u001b[0;34m" // BLUE
        const val PURPLE = "\u001b[0;35m" // PURPLE
        const val CYAN = "\u001b[0;36m" // CYAN
        const val WHITE = "\u001b[0;37m" // WHITE

        // Bold
        const val BLACK_BOLD = "\u001b[1;30m" // BLACK
        const val RED_BOLD = "\u001b[1;31m" // RED
        const val GREEN_BOLD = "\u001b[1;32m" // GREEN
        const val YELLOW_BOLD = "\u001b[1;33m" // YELLOW
        const val BLUE_BOLD = "\u001b[1;34m" // BLUE
        const val PURPLE_BOLD = "\u001b[1;35m" // PURPLE
        const val CYAN_BOLD = "\u001b[1;36m" // CYAN
        const val WHITE_BOLD = "\u001b[1;37m" // WHITE

        // Underline
        const val BLACK_UNDERLINED = "\u001b[4;30m" // BLACK
        const val RED_UNDERLINED = "\u001b[4;31m" // RED
        const val GREEN_UNDERLINED = "\u001b[4;32m" // GREEN
        const val YELLOW_UNDERLINED = "\u001b[4;33m" // YELLOW
        const val BLUE_UNDERLINED = "\u001b[4;34m" // BLUE
        const val PURPLE_UNDERLINED = "\u001b[4;35m" // PURPLE
        const val CYAN_UNDERLINED = "\u001b[4;36m" // CYAN
        const val WHITE_UNDERLINED = "\u001b[4;37m" // WHITE

        // Background
        const val BLACK_BACKGROUND = "\u001b[40m" // BLACK
        const val RED_BACKGROUND = "\u001b[41m" // RED
        const val GREEN_BACKGROUND = "\u001b[42m" // GREEN
        const val YELLOW_BACKGROUND = "\u001b[43m" // YELLOW
        const val BLUE_BACKGROUND = "\u001b[44m" // BLUE
        const val PURPLE_BACKGROUND = "\u001b[45m" // PURPLE
        const val CYAN_BACKGROUND = "\u001b[46m" // CYAN
        const val WHITE_BACKGROUND = "\u001b[47m" // WHITE

        // High Intensity
        const val BLACK_BRIGHT = "\u001b[0;90m" // BLACK
        const val RED_BRIGHT = "\u001b[0;91m" // RED
        const val GREEN_BRIGHT = "\u001b[0;92m" // GREEN
        const val YELLOW_BRIGHT = "\u001b[0;93m" // YELLOW
        const val BLUE_BRIGHT = "\u001b[0;94m" // BLUE
        const val PURPLE_BRIGHT = "\u001b[0;95m" // PURPLE
        const val CYAN_BRIGHT = "\u001b[0;96m" // CYAN
        const val WHITE_BRIGHT = "\u001b[0;97m" // WHITE

        // Bold High Intensity
        const val BLACK_BOLD_BRIGHT = "\u001b[1;90m" // BLACK
        const val RED_BOLD_BRIGHT = "\u001b[1;91m" // RED
        const val GREEN_BOLD_BRIGHT = "\u001b[1;92m" // GREEN
        const val YELLOW_BOLD_BRIGHT = "\u001b[1;93m" // YELLOW
        const val BLUE_BOLD_BRIGHT = "\u001b[1;94m" // BLUE
        const val PURPLE_BOLD_BRIGHT = "\u001b[1;95m" // PURPLE
        const val CYAN_BOLD_BRIGHT = "\u001b[1;96m" // CYAN
        const val WHITE_BOLD_BRIGHT = "\u001b[1;97m" // WHITE

        // High Intensity backgrounds
        const val BLACK_BACKGROUND_BRIGHT = "\u001b[0;100m" // BLACK
        const val RED_BACKGROUND_BRIGHT = "\u001b[0;101m" // RED
        const val GREEN_BACKGROUND_BRIGHT = "\u001b[0;102m" // GREEN
        const val YELLOW_BACKGROUND_BRIGHT = "\u001b[0;103m" // YELLOW
        const val BLUE_BACKGROUND_BRIGHT = "\u001b[0;104m" // BLUE
        const val PURPLE_BACKGROUND_BRIGHT = "\u001b[0;105m" // PURPLE
        const val CYAN_BACKGROUND_BRIGHT = "\u001b[0;106m" // CYAN
        const val WHITE_BACKGROUND_BRIGHT = "\u001b[0;107m" // WHITE

    }

    object WallpapersType {
        const val SCENE = "scene"
        const val VIDEO = "video"
        const val WEB = "web"
        const val APPLICATIONS = "applications"
        const val NONE = ""
    }
}