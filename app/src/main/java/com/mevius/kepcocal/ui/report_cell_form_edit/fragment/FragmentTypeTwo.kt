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

class FragmentTypeTwo: Fragment() {
    private lateinit var mContext: Context
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

        val button = view.findViewById<Button>(R.id.type2_btn_add)

        button.setOnClickListener {
            val selectOptionData = SelectOptionData(
                null,
                reportCellFormEditViewModel.cellFormId,
                type2_select_option_data_input.text.toString()
            )
            reportCellFormEditViewModel.insertSelectOptionData(selectOptionData)
        }

        // RecyclerView Btn Onclick
        val itemBtnClick: (SelectOptionData) -> Unit = {
            reportCellFormEditViewModel.deleteSelectOptionData(it)
        }

        // RecyclerView 설정
        val recyclerViewAdapter = TypeTwoRVAdapter(mContext, itemBtnClick)
        val recyclerViewLayoutManager = LinearLayoutManager(mContext)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_type2)

        recyclerView.adapter = recyclerViewAdapter   // Set Adapter to RecyclerView in xml
        recyclerView.layoutManager = recyclerViewLayoutManager

        if (reportCellFormEditViewModel.cellFormId != 0L)
        reportCellFormEditViewModel.getSelectOptionDataWithCellFormId(reportCellFormEditViewModel.cellFormId)
            .observe(viewLifecycleOwner, { selectOptionData ->
                recyclerViewAdapter.setSelectOptionData(selectOptionData)
            })

        return view
    }
}