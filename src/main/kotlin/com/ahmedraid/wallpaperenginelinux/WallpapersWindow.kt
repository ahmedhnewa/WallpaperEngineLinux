package com.ahmedraid.wallpaperenginelinux

import com.ahmedraid.wallpaperenginelinux.models.wallpaperengine.WallpaperEngineProjectFile
import com.ahmedraid.wallpaperenginelinux.utils.extensions.*
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Image
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel


class WallpapersWindow(
    val onItemClick: (wallpaperEngineProjectFile: WallpaperEngineProjectFile) -> Unit = {}
) : JFrame() {
    private val workshopWallpapersPath: String = getWorkshopDir()
    private val defaultWallpapersPath: String = getDefProjectsDir()
    private val myProjectsPath: String = getMyProjectsDir()
    private val items = mutableListOf<JLabel>()
    private var selectedItem: JLabel? = null
    private var lastHovered: JLabel? = null

    init {
        this.title = "Wallpaper Engine By Ahmed Hnewa"
        this.iconImage = ImageIO.read(getFileFromResourcesAsURL("images/logo.png", javaClass.classLoader))
        this.setSize(1000, 800)
//        this.maximumSize = Dimension(1920, 1080)
        this.minimumSize = Dimension(700, 600)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.isVisible = true
        this.isResizable = true

        this.layout = GridLayout(10, 10, 10, 10)

        init()
    }

    private fun init() {
        val workshopWallpapers = File(workshopWallpapersPath)
        val defaultWallpapers = File(defaultWallpapersPath)
        val myProjectsPath = File(myProjectsPath)

        logProcess("Load wallpapers from ${workshopWallpapers.path}")

        // getting files name from folder
        val directoryListing = mutableListOf<File>()
        workshopWallpapers.listFiles()?.let { directoryListing.addAll(it) }
        defaultWallpapers.listFiles()?.let { directoryListing.addAll(it) }
        myProjectsPath.listFiles()?.let { directoryListing.addAll(it) }
        val selectedWallpaper = getSelectedWallpaper()
        val wallpapersToHide = mutableSetOf(
            "9557961063"
        )
        for ((index, wallpaperFile) in directoryListing.withIndex()) {
            val wallpaperPath = wallpaperFile.path
            val wallpaperId = getWallpaperIdByPath(wallpaperPath)
            if (wallpapersToHide.contains(wallpaperId)) {
                continue
            }
            val label = JLabel()
            items.add(label)

            val wallpaperEngineProjectFile = getWallpaperProjectFile(wallpaperPath)
            wallpaperEngineProjectFile.path = wallpaperPath
            wallpaperEngineProjectFile.index = index
            label.text = wallpaperEngineProjectFile.title
            label.icon = ImageIcon(
                ImageIcon(getWallpaperPreviewIcon(wallpaperEngineProjectFile, wallpaperPath)).image.getScaledInstance(
                    150,
                    150,
                    Image.SCALE_FAST
                )
            )
            if (selectedWallpaper == wallpaperId) {
                label.foreground = Color.CYAN
                selectedItem = label
            }
            label.addMouseListener(object : MouseAdapter() {

                override fun mouseExited(e: MouseEvent?) {
                    if (label != selectedItem)
                        lastHovered?.foreground = Color.WHITE
                }

                override fun mouseClicked(e: MouseEvent) {
                    selectedItem?.foreground = Color.WHITE
                    onItemClick(wallpaperEngineProjectFile)
                    label.foreground = Color.CYAN
                    selectedItem = label
                }

                override fun mouseEntered(e: MouseEvent) {
                    if (label != selectedItem) {
                        label.foreground = Color.MAGENTA
                        lastHovered = label
                    }
                }
            })
            add(label)
        }

    }
}