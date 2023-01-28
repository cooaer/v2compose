package io.github.v2compose.util

import io.github.v2compose.App
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Logf {
    fun appendLog(text: String?) {
        val path = App.instance.externalCacheDir?.absolutePath ?: return
        val logFile = File("$path/log.file")
        if (!logFile.exists()) {
            try {
                logFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            val buf = BufferedWriter(FileWriter(logFile, true))
            buf.append(text)
            buf.newLine()
            buf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}