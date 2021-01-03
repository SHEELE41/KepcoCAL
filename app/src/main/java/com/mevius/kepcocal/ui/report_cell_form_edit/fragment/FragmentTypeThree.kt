package com.mevius.kepcocal.ui.report_cell_form_edit.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.mevius.kepcocal.R
import kotlinx.android.synthetic.main.report_cell_form_edit_type3.*

class FragmentTypeThree: Fragment() {
    private lateinit var mContext: Context

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

        // 저장되어있는 값에 따라 스피너 체크된 것 달라지도록
        ArrayAdapter.createFromResource(
            mContext,
            R.array.spinner_labels_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            val spinner = view.findViewById<Spinner>(R.id.type3_spinner)
            spinner.adapter = adapter
        }

        return view
    }

    fun getSpinnerSelected(): String {
        return type3_spinner.selectedItem.toString()
    }
}