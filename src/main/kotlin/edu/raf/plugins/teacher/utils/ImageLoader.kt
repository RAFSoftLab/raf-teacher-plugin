package edu.raf.plugins.teacher.utils

import java.util.*
import javax.swing.ImageIcon
import java.awt.Image
import java.net.URL

object ImageLoader {
    private val properties = Properties()

    init {
        val inputStream = ImageLoader::class.java.classLoader.getResourceAsStream("image_urls.properties")
            ?: throw IllegalStateException("Fajl 'image_urls.properties' nije pronađen!")
        properties.load(inputStream)
    }

    private val cache = mutableMapOf<String, String>()

    fun getImageUrl(key: String): String {
        return cache[key] ?: properties.getProperty(key)?.also { cache[key] = it }
        ?: throw IllegalArgumentException("Ključ za sliku '$key' nije pronađen!")
    }

    fun loadIcon(imagePath: String, width: Int, height: Int): ImageIcon {
        return ImageIcon(
            ImageIcon(URL(getImageUrl(imagePath)))
                .image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        )
    }
}
