package com.ahmedraid.wallpaperenginelinux.utils.extensions

fun isNumber(s: String?): Boolean = if (s.isNullOrEmpty()) false else s.all { Character.isDigit(it) }