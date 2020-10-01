package com.mevius.kepcocal

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_project_list.*


/*
* [ProjectListActivity]
* 프로젝트(엑셀 파일) 리스트를 띄우고 리스트의 아이템을 추가 / 삭제할 수 있는 기능을 하는 Activity
* ListView Item 이 클릭될 시 해당 프로젝트의 상세 정보 액티비티(지도 및 기기 리스트)인 ProjectDetailActivity 로 이동함
*
* Floating Action Button 을 이용하여 프로젝트를 추가할 수 있으며 과정은 다음과 같음
* - SAF(Storage Access Framework)를 이용하여 엑셀 파일을 불러옴
* - Dialog 를 띄워 프로젝트명을 입력받음
* - 불러온 엑셀 파일의 파일명을 입력받은 프로젝트명으로 Rename 하여 앱 내부 저장공간인 /Android/data/com.mevius.kepcocal/files 에 복사
* - 파일명 = 프로젝트명 이므로 프로젝트 리스트를 띄울 때는 파일명 리스트만 읽으면 됨
*
* 길게 눌러서(Long Click) 삭제 기능은 구현 예정
*
* ListView 프로젝트 리스트 갱신
* 파일 목록 상의 파일명과 수정 날짜를 읽어서 ArrayList 에 갱신... 따로 클래스화하여 FAB onClick 이벤트에 추가
*/


class ProjectListActivity : AppCompatActivity() {
    private val READ_REQUEST_CODE: Int = 42     // Request Code for SAF
    val TAG : String = "ProjectListActivity"    // Log TAG String

    private val projectFileManager = ProjectFileManager()   // ProjectFile(xls)Manager for save, copy, list

    private var itemDataList = arrayListOf<ProjectListViewItemData>(
        ProjectListViewItemData("삼척지사 절연유 점검", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검1", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검2", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검3", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검4", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검5", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검6", "2020.01.30"),
        ProjectListViewItemData("삼척지사 절연유 점검7", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검8", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검9", "2020.09.30"),
        ProjectListViewItemData("삼척지사 절연유 점검09", "2020.09.30")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_list)

        supportActionBar?.title = "프로젝트 리스트"    // Set AppBar Title

        // 나중에 객체화 하자. 파일 리스팅

        // 우선 디렉토리 읽고 디렉토리 자체가 없으면 리스트 아무것도 없음(이미지뷰)

//        File(path).walk().filter { it.isFile }.toList()

        // 디렉토리는 있으나 엑셀 파일이 아무것도 없어도 리스트 아무것도 없음
        // 파일이 있으면 각각의 파일명, 수정 날짜를 읽어 어레이 리스트에 추가
        // 파일 새로 추가와 동시에 이름 바꾸기(프로젝트명과 동일하게)
        //

        val listViewAdapter = ProjectListViewAdapter(this, itemDataList)    // new ListViewAdapter
        lv_project_list.adapter = listViewAdapter   // Set Apapter to Listview in xml
        itemDataList.removeAt(1)
        itemDataList.add(1, ProjectListViewItemData("삼척지사 절연유 점검 123", "2020.09.30"))
//        Toast.makeText(this,ListDataArray[1].toString(), Toast.LENGTH_SHORT).show()
//        Adapter.notifyDataSetChanged()

        // ListView Item onClickEvent (Start ProjectDetailActivity)
        lv_project_list.setOnItemClickListener () { parent, view, position, id ->
            Log.d(
                "디렉토리 테스트",
                "Setaie;fwjg;aowhgewvndosizjoiefmjaiweojfli;zdsjicfnwefovnzj;iofdjwiefmc;owiejcz;ielwojfwjf;zo"
            )
            val element = listViewAdapter.getItem(position)
            val intent = Intent(this, ProjectDetailActivity::class.java)
            startActivity(intent)
        }


        // Floating Action Button OnClickEvent (Add Project)
        fab_project_list.setOnClickListener(){


            // Type of Target File
            val mimeTypes = arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )

            // Attach Excel File With SAF(Storage Access Framework)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {      // Case of Higher Version than KITKAT
                    type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"     // All Files
                    if (mimeTypes.isNotEmpty()) {
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                    }
                } else {
                    var mimeTypesStr = ""
                    for (mimeType in mimeTypes) {
                        mimeTypesStr += "$mimeType|"
                    }
                    type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)   // "application/vnd.ms-excel|application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                }
            }

            startActivityForResult(intent, READ_REQUEST_CODE)   // Start Activity with RequestCode for onActivityResult
        }

//        lv_project_list.setOnItemLongClickListener() { parent, view, position, id ->
//            val element = listViewAdapter.getItem(position)
//            val intent = Intent(this, ProjectDetailActivity::class.java)
//            return true
//        }
    }

    /*
     * [onActivityResult Method]
     * When SAF Intent Activity is terminated (Copy(Rename) File to App File Path)
     * 엑셀 파일 선택 후 원래 Activity 로 결과를 들고 돌아오며 onActivityResult 가 호출됨
     * 이때 Text Input 이 있는 Dialog 를 띄워 프로젝트명을 물어보고, 그걸 파일명으로 하여 앱 내부 저장공간에 저장
     * 저장은 OK 눌렀을 때만 이루어짐.
     * Cancel 누를 경우 아무것도 안함.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {     // When Result is successful

            var projectNameInput : String = "example"

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Project Name")

            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.dialog_with_edit_text, null)

            builder.setView(viewInflated)

            builder.setPositiveButton(
                android.R.string.ok
            ) { dialog, which ->
                projectNameInput =
                    viewInflated.findViewById<AutoCompleteTextView>(R.id.input).text.toString()
                Log.d(TAG, projectNameInput)

                data?.data?.also { uri ->
                    projectFileManager.saveFileAs(uri, projectNameInput)      // Copy File Which is selected by SAF to Internal App Storage
                }
            }

            builder.setNegativeButton(
                android.R.string.cancel
            ) { dialog, which -> dialog.cancel() }      // If Click Cancel, Do Nothing

            builder.show()      // Show Dialog
        }
    }
}