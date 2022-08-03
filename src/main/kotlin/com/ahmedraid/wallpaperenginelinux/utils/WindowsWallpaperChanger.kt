package com.ahmedraid.wallpaperenginelinux.utils

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.platform.win32.WinDef.UINT_PTR
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIFunctionMapper
import com.sun.jna.win32.W32APITypeMapper


object WindowsWallpaperChanger {
    fun changeWallpaper(path: String) {
        SPI.INSTANCE.SystemParametersInfo(
            UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
            UINT_PTR(0),
            path,
            UINT_PTR(SPI.SPIF_UPDATEINIFILE or SPI.SPIF_SENDWININICHANGE)
        )
    }

    interface SPI : StdCallLibrary {
        fun SystemParametersInfo(
            uiAction: UINT_PTR?,
            uiParam: UINT_PTR?,
            pvParam: String?,
            fWinIni: UINT_PTR?
        ): Boolean
        companion object {
            //from MSDN article
            const val SPI_SETDESKWALLPAPER: Long = 20
            const val SPIF_UPDATEINIFILE: Long = 0x01
            const val SPIF_SENDWININICHANGE: Long = 0x02
            val INSTANCE: SPI = Native.loadLibrary("user32",
                SPI::class.java, object : HashMap<String?, Any?>() {
                    init {
                        put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE)
                        put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE)
                    }
                }) as SPI
        }
    }
}