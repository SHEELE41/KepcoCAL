package com.mevius.kepcocal.util

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.mevius.kepcocal.GlobalApplication
import com.mevius.kepcocal.view.project_list.adapter.ProjectRVItemData
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat


/**
 * [ProjectFileManager Class]
 * 일단은 엑셀 파일 관련 작업(생성, 삭제, 복사 등)을 위해 만들었지만 일반적인 파일 작업에도 적용 가능하도록 작성함.
 * 다만 대부분의 함수들이 SAF(Storage Access Framework) 반환 결과값을 파라미터로 받기 때문에 SAF에 한정적으로 유용할듯
 */
class ProjectFileManager {
    private var pfd: ParcelFileDescriptor? = null
    private var fileInputStream: FileInputStream? = null
    private var globalApplicationContext = GlobalApplication.instance.applicationContext()
    private var mOutputDir = globalApplicationContext.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files

    /**
     * [saveFileAs function]
     * SAF를 통해 선택한 파일 정보를 받은 뒤 앱 전용 디렉토리로 복사 및 Rename 해주는 함수
     * 엑셀 파일은 xls, xlsx 두 개의 확장자를 가지고 있으므로 이 부분 고려
     * 애초에 SAF를 통해 선택해준 파일이기 때문에 파일 자체가 null일리는 없겠지만 파일명은 null이 될 수 있으므로 주의
     */
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

    /**
     * [removeFile function]
     * 단순히 파일명(리스트 아이템의 ProjectName)을 파라미터로 받아 앱 전용 디렉토리에서 삭제하는 메소드
     * 굳이 외부 다른 디렉토리로 나갈 필요 없으므로 어려운 작업 필요 없음
     */
    fun removeFile(targetFileName : String) {
        File(mOutputDir, "/$targetFileName").delete()
    }

    /**
     * [isExcelFile function]
     * 말 그대로 엑셀 파일인지 아닌지를 판단해주는 함수이지만 아직 안씀.
     * saveFileAs에서 쓸지도?
     */
    fun isExcelFile(fileName : String) : Boolean {
        return when(fileName.substringAfterLast(".")){
            "xls", "xlsx" -> true
            else -> false
        }
    }

    /**
     * [syncList function]
     * ListView 데이터를 위한 ArrayList를 파라미터로 받아 현재 디렉토리 내부 파일 목록과 리스트를 동기화 시켜주는 메소드
     * 파일의 이름과 최종 수정 일자 가져옴
     * 리스트에는 기존 데이터가 남아있기 때문에 두 번 입력되는 문제를 막으려면 clear() 필요
     * 갱신된 ArrayList size를 반환하므로 0일 경우 이미지가 뜨게 하는 것 가능
     */
    @SuppressLint("SimpleDateFormat")
    fun syncList (itemDataList : ArrayList<ProjectRVItemData>) : Int {
        itemDataList.clear()    // Clear Existing ArrayList items. (이거 안하면 리스트에 같은게 두번 들어감, 즉 정말로 현재 존재하는 것만 보겠다는 것)

        File(mOutputDir.toString()).walk().forEach {
            if(it.extension == "xls" || it.extension == "xlsx"){    // Add excel files only
                val projectListViewItemData = ProjectRVItemData(it.name, SimpleDateFormat("yyyy-MM-dd").format(it.lastModified()))
                itemDataList.add(projectListViewItemData)
            }
        }

        return itemDataList.size
    }

    /**
     * [getFileName function]
     * SAF에서 선택된 파일의 파일명을 반환함
     */
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