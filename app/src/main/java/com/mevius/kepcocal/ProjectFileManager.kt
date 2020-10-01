package com.mevius.kepcocal

import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ProjectFileManager {
    private var pfd: ParcelFileDescriptor? = null
    private var fileInputStream: FileInputStream? = null
    private var globalApplicationContext = GlobalApplication.instance.applicationContext()
    private var mOutputDir = globalApplicationContext.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files

    fun saveFileAs(uri: Uri, newFileName: String){
        val fileName = getFileName(uri)
        try {
            pfd = uri.let { globalApplicationContext.contentResolver?.openFileDescriptor(it, "r") }
            fileInputStream = FileInputStream(pfd?.fileDescriptor)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        var newFile: File? = null
        if(fileName!=null) {
            newFile = File(mOutputDir, "/$newFileName"+".xlsx")
        }

        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null

        try {
            inChannel = fileInputStream?.channel
            outChannel = FileOutputStream(newFile).channel
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
            fileInputStream?.close()
            pfd?.close()
        }
    }

    fun isExcelFile(fileName : String) : Boolean {
        val extension = fileName.substringAfterLast(".")
        return (extension == "xls" || extension == "xlsx")
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = globalApplicationContext.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}