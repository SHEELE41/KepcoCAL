package com.mevius.kepcocal.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton


/**
 * [FileHelper]
 * 일단은 엑셀 파일 관련 작업(생성, 삭제, 복사 등)을 위해 만들었지만 일반적인 파일 작업에도 적용 가능하도록 작성함.
 * 다만 대부분의 함수들이 SAF(Storage Access Framework) 반환 결과값을 파라미터로 받기 때문에 SAF에 한정적으로 유용할듯
 */
@Singleton
class FileHelper @Inject constructor(@ApplicationContext private val ctx: Context) {
    private var pfd: ParcelFileDescriptor? = null
    private var fileInputStream: FileInputStream? = null
    private var mOutputDir =
        ctx.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files

    /**
     * [saveFileAs function]
     * SAF를 통해 선택한 파일 정보를 받은 뒤 앱 전용 디렉토리로 복사 및 Rename 해주는 함수
     * 엑셀 파일은 xls, xlsx 두 개의 확장자를 가지고 있으므로 이 부분 고려
     * 애초에 SAF를 통해 선택해준 파일이기 때문에 파일 자체가 null일리는 없겠지만 파일명은 null이 될 수 있으므로 주의
     * 반환은 xls 파일일 경우 true, xlsx 파일일 경우 false
     */
    // 앱 내부 디렉토리에 파일 복사 + Rename
    fun saveFileAs(uri: Uri /*원본 파일 경로*/, newFileName: String /*새로운 파일명*/): Boolean {
        val fileName = getFileName(uri)
        try {
            pfd = uri.let { ctx.contentResolver?.openFileDescriptor(it, "r") }
            fileInputStream = FileInputStream(pfd?.fileDescriptor)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        var newFile: File? = null
        var isXls = true

        when (fileName.substringAfterLast(".")) {
            "xls" -> newFile = File(mOutputDir, "/$newFileName.xls")
            "xlsx" -> {
                newFile = File(mOutputDir, "/$newFileName.xlsx")
                isXls = false
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
        return isXls
    }

    /**
     * [removeFile]
     * 단순히 파일명(리스트 아이템의 ProjectName)을 파라미터로 받아 앱 전용 디렉토리에서 삭제하는 메소드
     * 굳이 외부 다른 디렉토리로 나갈 필요 없으므로 어려운 작업 필요 없음
     */
    fun removeFile(targetFileName: String) {
        File(mOutputDir, "/$targetFileName").delete()
    }

    /**
     * [getFileName function]
     * SAF에서 선택된 파일의 파일명을 반환함
     */
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? =
                ctx.contentResolver?.query(uri, null, null, null, null)
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