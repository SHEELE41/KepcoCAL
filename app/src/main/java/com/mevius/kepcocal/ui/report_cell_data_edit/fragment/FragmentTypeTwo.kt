package com.mevius.kepcocal.ui.report_cell_data_edit.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mevius.kepcocal.R
import com.mevius.kepcocal.ui.report_cell_data_edit.ReportCellDataEditViewModel

class FragmentTypeTwo: Fragment() {
    private lateinit var mContext: Context
    private val reportCellDataEditViewModel: ReportCellDataEditViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cell_data_edit_type2, container, false)
    }
}