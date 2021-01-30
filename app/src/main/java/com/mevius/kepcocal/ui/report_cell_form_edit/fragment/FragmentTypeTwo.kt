package com.mevius.kepcocal.ui.report_cell_form_edit.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.ui.report_cell_form_edit.ReportCellFormEditViewModel
import com.mevius.kepcocal.ui.report_cell_form_edit.adapter.TypeTwoRVAdapter
import kotlinx.android.synthetic.main.report_cell_form_edit_type2.*

class FragmentTypeTwo : Fragment() {
    private lateinit var mContext: Context
    // Activity - Fragment 동일한 ViewModel 공유. (KTX 방식)
    // 기준이 되는 LifecycleOwner 는 Activity
    private val reportCellFormEditViewModel: ReportCellFormEditViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.report_cell_form_edit_type2, container, false)
        val btnAddSOD = view.findViewById<Button>(R.id.type2_btn_add)   // 버튼 변수 초기화 (안하면 NPE 발생)

        // RecyclerView 설정
        val recyclerViewAdapter =
            TypeTwoRVAdapter(mContext, reportCellFormEditViewModel.typeTwoSelectOptionDataCacheList)
        val recyclerViewLayoutManager = LinearLayoutManager(mContext)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_type2)

        recyclerView.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        recyclerView.layoutManager = recyclerViewLayoutManager

        // 추가 버튼 onClickListener
        btnAddSOD.setOnClickListener {
            val selectOptionData = SelectOptionData(
                null,
                reportCellFormEditViewModel.cellFormId,
                reportCellFormEditViewModel.reportId,
                false,
                type2_select_option_data_input.text.toString()
            )
            reportCellFormEditViewModel.typeTwoSelectOptionDataCacheList.add(selectOptionData)
            recyclerViewAdapter.notifyDataSetChanged()
            type2_select_option_data_input.setText("")  // Editor 내용 초기화
        }

        // TODO LiveData 말고 초기에 딱 한번 불러오도록 변경하기
        reportCellFormEditViewModel.getSelectOptionDataWithCellFormIdAndAutoFlag(
            reportCellFormEditViewModel.cellFormId,
            false   // 자동 입력 데이터가 아닌 선택 입력 데이터이므로
        )
            .observe(viewLifecycleOwner, { selectOptionData ->
                selectOptionData?.let {
                    reportCellFormEditViewModel.typeTwoSelectOptionDataCacheList.clear()
                    reportCellFormEditViewModel.typeTwoSelectOptionDataCacheList.addAll(it)    // 깊은 복사
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            })

        return view
    }
}