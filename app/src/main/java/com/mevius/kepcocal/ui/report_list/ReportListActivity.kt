package com.mevius.kepcocal.ui.project_list

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Project
import com.mevius.kepcocal.ui.project_detail.ProjectDetailActivity
import com.mevius.kepcocal.ui.project_list.adapter.ProjectRVAdapter
import com.mevius.kepcocal.utils.AndroidBug5497Workaround
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_project_list.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * [ReportListActivity]
 * 프로젝트(엑셀 파일) 리스트를 띄우고 리스트의 아이템을 추가 / 삭제할 수 있는 기능을 하는 Activity
 * ListView Item 이 클릭될 시 해당 프로젝트의 상세 정보 액티비티(지도 및 기기 리스트)인 ProjectDetailActivity 로 이동함
 * 길게 클릭 시 프로젝트 삭제 가능 ( with Dialog )
 */

@AndroidEntryPoint
class ReportListActivity : AppCompatActivity() {
    private var projectLiveDataSize: Int = -1
    private val safRequestCode: Int = 42     // Request Code for SAF
    private val todayDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private lateinit var recyclerViewAdapter: ProjectRVAdapter
    private lateinit var recyclerViewLayoutManager: LinearLayoutManager
    private val projectListViewModel: ProjectListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_list)

        setupUI()
        setupViewModel()
    }


    /**
     * NestedScrollView Height Test Code
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    Toast.makeText(this, scroll_view.height.toString(), Toast.LENGTH_SHORT).show()
    }
     */

    /**
     * [onActivityResult Method]
     * When SAF Intent Activity is terminated (Copy(Rename) File to App File Path)
     * 엑셀 파일 선택 후 원래 Activity 로 결과를 들고 돌아오며 onActivityResult 가 호출됨
     * 이때 Text Input 이 있는 Dialog 를 띄워 보고서 제목을 물어보고, 그걸 파일명으로 하여 앱 내부 저장공간에 저장
     * 저장은 OK 눌렀을 때만 이루어짐.
     * Cancel 누를 경우 아무것도 안함.
     */
    @SuppressLint("InflateParams")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == safRequestCode && resultCode == Activity.RESULT_OK) {     // When is SAF Request & Result is successful
            var projectNameInput: String
            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.dialog_with_edit_text, null)

            AlertDialog.Builder(this).apply {
                setTitle("프로젝트 추가")
                setView(viewInflated)
                setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    projectNameInput =
                        viewInflated.findViewById<AutoCompleteTextView>(R.id.input).text.toString()
                    data?.data?.also { uri ->
                        val project = Project(
                            null,
                            projectNameInput,
                            todayDateFormat,
                            uri.toString()
                        )
                        projectListViewModel.insertProject(project)
                        dialog.dismiss()
                    }
                }
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }      // If Click Cancel, Do Nothing
                show()
            }
        }
    }

    private fun setupUI() {
        AndroidBug5497Workaround.assistActivity(this)   // https://issuetracker.google.com/issues/36911528
        setupRecyclerView()
        setupFloatingActivityButton()
    }

    private fun setupRecyclerView() {
        /*
         * [RecyclerView Project Item onClick]
         * 아이템 클릭시 프로젝트 상세 액티비티로 넘어가기 위한 코드
         * 아이템을 선택하면 해당 프로젝트의 ProjectDetailActivity 로 넘어감.
         */
        val itemClick: (Long?) -> Unit = {
            val intent = Intent(this, ProjectDetailActivity::class.java).apply {
                putExtra(
                    "projectId",
                    it
                )
            }
            startActivity(intent)
        }

        /*
         * [RecyclerView Project Item onLongClick]
         * 프로젝트(엑셀 파일) 삭제를 위한 코드
         * 길게 눌러서 프로젝트 삭제 확인 다이얼로그 띄움
         */
        val itemLongClick: (Project) -> Boolean = {
            AlertDialog.Builder(
                this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
            ).apply {
                setTitle("프로젝트 삭제") //제목
                setMessage("정말로 삭제하시겠어요?")
                setPositiveButton(android.R.string.ok) { dialog, _ ->
                    projectListViewModel.deleteProject(it)
                    dialog.dismiss()
                }
                setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }
                this.show()
            }
            true
        }

        // RecyclerView 설정
        // rv_project_list.setHasFixedSize(true)
        recyclerViewAdapter = ProjectRVAdapter(this, itemClick, itemLongClick)
        recyclerViewLayoutManager = LinearLayoutManager(this)
        rv_project_list.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        rv_project_list.layoutManager = recyclerViewLayoutManager
    }

    private fun setupFloatingActivityButton() {
        /*
         * [Floating Action Button onClickListener ]
         * 프로젝트(엑셀 파일) 추가를 위한 버튼
         * 누르면 SAF 를 통해 엑셀 파일을 선택할 수 있음
         * 파일을 선택하면 onActivityResult 액티비티로 넘어감.
         */
        fab_project_list.setOnClickListener {
            // Type of Target File
            val mimeTypes = arrayOf(
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            // Attach Excel File With SAF(Storage Access Framework)
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                if (mimeTypes.isNotEmpty()) {
                    this.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            }
            startActivityForResult(
                intent,
                safRequestCode
            )   // Start Activity with RequestCode for onActivityResult
        }
    }

    private fun setupViewModel() {
        // ViewModel observe
        projectListViewModel.allProjects.observe(this, { projects ->    // 초기 데이터 로드시에도 호출됨
            projects?.let {
                recyclerViewAdapter.setProjects(it)
                iv_isEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
                if (projectLiveDataSize != -1 && projectLiveDataSize < it.size) {   // 초기 로드가 아니고, 프로젝트가 추가되었을 때
                    projectListViewModel.insertMachinesFromExcel(it.last())
//                    val mIntent = Intent(this, ProjectDetailActivity::class.java).apply {
//                        putExtra(
//                            "projectId",
//                            it.lastIndex.toLong()
//                        )
//                    }
//                    startActivity(mIntent)
                }
                projectLiveDataSize = projects.size // 초기 데이터 로드 완료 후
            }
        })
    }
}