package com.mevius.kepcocal.ui.report_cell_form_edit.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.SelectOptionData
import com.mevius.kepcocal.ui.report_cell_form_edit.ReportCellFormEditViewModel
import java.lang.Exception

class FragmentTypeThree : Fragment() {
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
        val view = inflater.inflate(R.layout.report_cell_form_edit_type3, container, false)
        val spinner = view.findViewById<Spinner>(R.id.type3_spinner)

        // Spinner 설정
        ArrayAdapter.createFromResource(
            mContext,
            R.array.spinner_labels_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                reportCellFormEditViewModel.typeThreeSelectOptionDataPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // TODO Observe 없이 구현
        reportCellFormEditViewModel.getSelectOptionDataWithCellFormIdAndAutoFlag(
            reportCellFormEditViewModel.cellFormId,
            true
        )
            .observe(viewLifecycleOwner, { selectOptionData ->
                selectOptionData?.let {
                    if (it.isNotEmpty()) {
                        val selectedPosition = it[0].content.toInt()
                        reportCellFormEditViewModel.typeThreeSelectOptionDataPosition =
                            selectedPosition
                        spinner.setSelection(selectedPosition)
                    }
                }
            })

        return view
    }
}