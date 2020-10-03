package com.mevius.kepcocal

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat

class ProjectFileManager {
    private var pfd: ParcelFileDescriptor? = null
    private var fileInputStream: FileInputStream? = null
    private var globalApplicationContext = GlobalApplication.instance.applicationContext()
    private var mOutputDir = globalApplicationContext.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files


    // 앱 내부 디렉토리에 파일 복사 + Rename
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
            newFile = when(fileName.substringAfterLast(".")){
                "xls" -> File(mOutputDir, "/$newFileName.xls")
                "xlsx" -> File(mOutputDir, "/$newFileName.xlsx")
                else -> return      // 엑셀 파일이 아닌 경우
            }
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

    // 앱 내부 디렉토리에서 파일 삭제
    fun removeFile(targetFileName : String) {
        File(mOutputDir, "/$targetFileName").delete()
    }

    // 엑셀파일 여부 확인
    fun isExcelFile(fileName : String) : Boolean {
        val extension = fileName.substringAfterLast(".")
        return when(extension){
            "xls", "xlsx" -> true
            else -> false
        }
    }

    // 리스트뷰 데이터 리스트와 디렉토리 파일 목록 동기화
    @SuppressLint("SimpleDateFormat")
    fun syncList (itemDataList : ArrayList<ProjectListViewItemData>) : Int {
        itemDataList.clear()    // Clear Existing ArrayList items. (이거 안하면 리스트에 같은게 두번 들어감, 즉 정말로 현재 존재하는 것만 보겠다는 것)

        File(mOutputDir.toString()).walk().forEach {
            if(it.extension == "xls" || it.extension == "xlsx"){    // Add excel files only
                val projectListViewItemData = ProjectListViewItemData(it.name, SimpleDateFormat("yyyy-MM-dd").format(it.lastModified()))
                itemDataList.add(projectListViewItemData)
            }
        }

        return itemDataList.size
    }

    // SAF 에서 선택한 파일의 파일명 구하기.
    fun getFileName(uri: Uri): String? {
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