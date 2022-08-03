package com.ahmedraid.wallpaperenginelinux.utils.extensions

import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.ANSI_BLACK
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.ANSI_GREEN
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.ANSI_RED
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.ANSI_RESET
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.BLACK_BACKGROUND
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.BLACK_BRIGHT
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.CYAN
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.CYAN_BACKGROUND
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.GREEN_BACKGROUND
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.RED_BACKGROUND
import com.ahmedraid.wallpaperenginelinux.utils.Constants.ColorCodes.WHITE_BACKGROUND

fun logError(msg: Any?) {
    println("${ANSI_RED}$msg${ANSI_RESET}")
}

fun log(msg: String) {
    println(msg)
}

fun logSuccess(msg: Any) {
    println("${ANSI_GREEN}$msg$ANSI_RESET")
}

fun logNote(msg: Any) {
    println("${BLACK_BACKGROUND}$msg$ANSI_RESET")
}

fun logProcess(msg: Any) {
    println("${WHITE_BACKGROUND}${ANSI_BLACK}$msg$ANSI_RESET$ANSI_RESET")
}