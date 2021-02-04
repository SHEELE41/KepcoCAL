package com.mevius.kepcocal.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.mevius.kepcocal.data.db.entity.CellData
import com.mevius.kepcocal.data.db.entity.Machine
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.math.pow

/**
 * [ExcelHelper]
 * 엑셀 데이터를 파싱하는 메소드를 가진 클래스
 */
@Singleton
class ExcelHelper @Inject constructor(@ApplicationContext private val ctx: Context) {
    private var mOutputDir =
        ctx.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files
    private val logTAG = "ExcelHelper"

    /**
     * [readProjectAsList]
     * 엑셀 데이터를 ArrayList 형태로 읽어들이는 함수.
     */
    fun readProjectAsList(uri: Uri): ArrayList<Machine> {
        val machineList = arrayListOf<Machine>()
        var fileInputStream: FileInputStream? = null

        try {
            val pfd = uri.let { ctx.contentResolver?.openFileDescriptor(it, "r") }
            fileInputStream = FileInputStream(pfd?.fileDescriptor)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            /*
            * 엑셀 파일 제어 가능한 객체로 불러오기
            */
            // FileInputStream
            val mInputStream = fileInputStream

            // WorkBook auto xls, xlsx
            // 읽는 것 뿐이라면 WorkbookFactory 를 이용해도 문제 없음
            val mWorkBook = WorkbookFactory.create(mInputStream)

            // WorkBook 에서 시트 가져오기 (첫 번째 시트)
            val mSheet = mWorkBook.getSheetAt(0)

            /*
            * 엑셀 파일 데이터 읽기
            */
            val rowIter = mSheet.rowIterator()  // 다음 행 비었는지 확인할 Iterator
            var rowNum = 0  // 반복문 안에서 사용할 행 번호

            while (rowIter.hasNext()) {   // 행 반복문
                val currentRow = rowIter.next() // 현재 행 가져오기
                val machineData = Machine()   // 행 한번 순회할때마다 machineData 객체 생성하여 정보 채우고 리스트에 add
                var cellNum = 0 // 행 바뀔 때마다 cellNum 초기화
                while (cellNum < 11) {     // 열 반복문, MachineData 클래스의 초기 필수 필드 개수가 11개이므로 11
                    val currentCell = currentRow.getCell(
                        cellNum,
                        Row.CREATE_NULL_AS_BLANK
                    )   // CREATE_NULL_AS_BLANK : Cell이 빈칸일 경우 "" 스트링 객체 생성
                    // 더 단순하게...

                    when (cellNum) {
                        0 -> machineData.machineIdInExcel = cellTypeCasting(currentCell)
                        1 -> machineData.branch = cellTypeCasting(currentCell)
                        2 -> machineData.computerizedNumber = cellTypeCasting(currentCell)
                        3 -> machineData.lineName = cellTypeCasting(currentCell)
                        4 -> machineData.lineNumber = cellTypeCasting(currentCell)
                        5 -> machineData.company = cellTypeCasting(currentCell)
                        6 -> machineData.manufacturingYear = cellTypeCasting(currentCell)
                        7 -> machineData.manufacturingDate = cellTypeCasting(currentCell)
                        8 -> machineData.manufacturingNumber = cellTypeCasting(currentCell)
                        9 -> machineData.address1 = cellTypeCasting(currentCell)
                        10 -> machineData.address2 = cellTypeCasting(currentCell)
                        else -> break
                    }
                    cellNum++
                }
                machineList.add(machineData)
                rowNum++
            }
        } catch (e: Exception) {
            Log.d(logTAG, e.toString())
        } finally {
            fileInputStream?.close()
        }
        return machineList
    }

    /**
     * [cellTypeCasting]
     * 간단한 타입캐스팅 함수.
     * poi 특성상 정수데이터도 double로 읽어들이는 불편함때문에 제작.
     */
    private fun cellTypeCasting(cell: Cell): String {
        return when (cell.cellType) {
            Cell.CELL_TYPE_NUMERIC -> cell.numericCellValue.toInt().toString()
            else -> cell.toString()
        }
    }

    /**
     * [writeReport]
     * inputFile 에 cellDataList 내용을 작성하여 outputFile 로 보고서 파일을 내보내는 메소드
     */
    fun writeReport(inputFileName: String, outputFileName: String, cellDataList: List<CellData>) {
        try {
            val mInputStream = FileInputStream("$mOutputDir/$inputFileName")

            val mWorkBook = if (inputFileName.endsWith(".xls")) {
                val mFileSystem = POIFSFileSystem(mInputStream)
                HSSFWorkbook(mFileSystem)
            } else {
                XSSFWorkbook(mInputStream)
            }

            val mSheet = mWorkBook.getSheetAt(0)

            for (cellData in cellDataList) {
                val cellIndexArray = parseCellIndex(cellData.cell)
                val rowNum = cellIndexArray[0]
                val colNum = cellIndexArray[1]
                writeCell(mSheet, rowNum, colNum, cellData.content)
            }

            val mOutputStream = FileOutputStream("$mOutputDir/$outputFileName")
            mWorkBook.write(mOutputStream)
            mInputStream.close()
            mOutputStream.close()
        } catch (e: Exception) {
            Log.d(logTAG, e.toString())
        }
    }

    /**
     * [parseCellIndex]
     * "AA70" 등의 CellLabel 을 [26, 70] 형태의 정수 배열로 바꿔주는 메소드
     */
    private fun parseCellIndex(cellLabel: String): IntArray {
        val p1 = Pattern.compile("([a-zA-Z]+)([0-9]+)")
        val matcher = p1.matcher(cellLabel)
        matcher.find()

        var colNum = 0
        matcher.group(1).toUpperCase(Locale.ROOT).reversed().forEachIndexed { i, c ->
            val delta = c.toInt() - 'A'.toInt() + 1
            colNum += delta * 26.toDouble().pow(i.toDouble()).toInt()
        }
        colNum -= 1

        val rowNum = matcher.group(2).toInt() - 1
        return intArrayOf(rowNum, colNum)
    }

    /**
     * [writeCell]
     * 셀 하나에 데이터를 쓰는 메소드
     */
    private fun writeCell(sheet: Sheet, rowNum: Int, colNum: Int, content: String) {
        var row = sheet.getRow(rowNum)
        if (row == null) {
            row = sheet.createRow(rowNum)
        }

        var cell = row.getCell(colNum)
        if (cell == null) {
            cell = row.createCell(colNum)
        }
        cell.setCellValue(content)
    }
}