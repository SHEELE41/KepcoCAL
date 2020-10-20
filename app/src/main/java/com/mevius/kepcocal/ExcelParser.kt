package com.mevius.kepcocal

import android.util.Log
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream
import com.mevius.kepcocal.MachineData as MachineData

/**
 * [ExcelParser]
 * 엑셀 데이터를 파싱하는 메소드를 가진 클래스
 * 조건부 컴파일 방법 찾아서 HSSF, XSSF 방법 생각하기
 */
class ExcelParser {
    private val globalApplicationContext = GlobalApplication.instance.applicationContext()  // 저장소 경로를 위한 AppContext 불가피.
    private val mOutputDir = globalApplicationContext.getExternalFilesDir(null)     // /Android/data/com.mevius.kepcocal/files

    /**
     * [excelToList]
     * 엑셀 데이터를 ArrayList 형태로 읽어들이는 함수.
     * 조건부 컴파일 방법 찾아서 HSSF, XSSF 방법 생각하기
     */
    fun excelToList(fileName : String, machineList : ArrayList<MachineData>){
        /*
        * Case 1.
        * When extension is "xls" -> HSSF(poi)
        */
        if (fileName.substringAfterLast(".") == "xls") {     // When extension is "xls" -> HSSF
            try {
                /*
                * 엑셀 파일 제어 가능한 객체로 불러오기
                */
                // FileInputStream
                val mInputStream = FileInputStream("$mOutputDir/$fileName")

                // POISFileSystem 객체 (xsl)
                val mFileSystem = POIFSFileSystem(mInputStream)

                // WorkBook(엑셀 파일)
                val mWorkBook = HSSFWorkbook(mFileSystem)

                // WorkBook 에서 시트 가져오기 (첫 번째 시트)
                val mSheet = mWorkBook.getSheetAt(0)

                /*
                * 엑셀 파일 데이터 읽기
                */
                val rowIter = mSheet.rowIterator()  // 다음 행 비었는지 확인할 Iterator
                var rowNum = 0  // 반복문 안에서 사용할 행 번호

                while (rowIter.hasNext()) {   // 행 반복문
                    val currentRow = rowIter.next() as HSSFRow // xls file type
                    val machineData = MachineData("")   // 행 한번 순회할때마다 machineData 객체 생성하여 정보 채우고 리스트에 add
                    var cellNum = 0 // 행 바뀔 때마다 cellNum 초기화
                    while (cellNum < 11) {     // 열 반복문, MachineData 클래스의 초기 필수 필드 개수가 11개이므로 11
                        val currentCell = currentRow.getCell(cellNum, Row.CREATE_NULL_AS_BLANK) as HSSFCell   // xls file type, CREATE_NULL_AS_BLANK : Cell이 빈칸일 경우 "" 스트링 객체 생성
                        when (cellNum) {
                            0 -> machineData.index = cellTypeCasting(currentCell)
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
                Log.d("엑셀파서에러로그", "에러 발생함.")
            }
        }

        /*
        * Case 2.
        * When extension is "xlsx" -> XSSF(ooxml)
        */
        else if(fileName.substringAfterLast(".") == "xlsx"){
            try {
                /*
                * 엑셀 파일 제어 가능한 객체로 불러오기
                */
                // FileInputStream
                val mInputStream = FileInputStream("$mOutputDir/$fileName")

                // WorkBook(엑셀 파일)
                val mWorkBook = XSSFWorkbook(mInputStream)

                // WorkBook 에서 시트 가져오기 (첫 번째 시트)
                val mSheet = mWorkBook.getSheetAt(0)

                /*
                * 엑셀 파일 데이터 읽기
                */
                val rowIter = mSheet.rowIterator()  // 다음 행 비었는지 확인할 Iterator
                var rowNum = 0  // 반복문 안에서 사용할 행 번호

                while (rowIter.hasNext()) {   // 행 반복문
                    val currentRow = rowIter.next() as XSSFRow // xlsx file type
                    val machineData = MachineData("")
                    var cellNum = 0 // 행 바뀔 때마다 cellNum 초기화
                    while (cellNum < 11) {     // 열 반복문, MachineData 클래스의 초기 필수 필드 개수가 11개이므로 11
                        val currentCell = currentRow.getCell(cellNum, Row.CREATE_NULL_AS_BLANK) as XSSFCell   // xlsx file type, CREATE_NULL_AS_BLANK : Cell이 빈칸일 경우 "" 스트링 객체 생성
                        when (cellNum) {
                            0 -> machineData.index = cellTypeCasting(currentCell)
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
                Log.d("엑셀파서에러로그","에러 발생함.")
            }
        }
    }

    /**
     * [cellTypeCasting]
     * 간단한 타입캐스팅 함수.
     * poi 특성상 정수데이터도 double로 읽어들이는 불편함때문에 제작.
     */
    private fun cellTypeCasting (cell : Cell) : String{
        return when (cell.cellType){
            XSSFCell.CELL_TYPE_NUMERIC -> cell.numericCellValue.toInt().toString()
            else -> cell.toString()
        }
    }
}