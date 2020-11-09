package com.mevius.kepcocal.ui.project_detail

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.util.Util
import com.mevius.kepcocal.R
import com.mevius.kepcocal.ui.project_detail.data.MachineWrapper


class SearchResultsListAdapter : RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder>() {
    private var mDataSet: List<MachineWrapper> = ArrayList()
    private var mLastAnimatedItemPosition = -1

    interface OnItemClickListener {
        fun onClick(colorWrapper: MachineWrapper?)
    }

    private var mItemsOnClickListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mColorName: TextView = view.findViewById(R.id.color_name)
        val mColorValue: TextView = view.findViewById(R.id.color_value)
        val mTextContainer: View = view.findViewById(R.id.text_container)

    }

    fun swapData(mNewDataSet: List<MachineWrapper?>?) {
        mDataSet = mNewDataSet as List<MachineWrapper>
        notifyDataSetChanged()
    }

    fun setItemsOnClickListener(onClickListener: OnItemClickListener?) {
        mItemsOnClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_results_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val machineSuggestion: MachineWrapper = mDataSet[position]
        holder.mColorName.setText(machineSuggestion.cnum)
        holder.mColorValue.setText(machineSuggestion.name)
        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView)
            mLastAnimatedItemPosition = position
        }
        if (mItemsOnClickListener != null) {
            holder.itemView.setOnClickListener {
                mItemsOnClickListener!!.onClick(mDataSet[position])
            }
        }
    }


    private fun animateItem(view: View) {
        view.translationY = Util.getScreenHeight(view.context as Activity).toFloat()
        view.animate()
            .translationY(0F)
            .setInterpolator(DecelerateInterpolator(3f))
            .setDuration(700)
            .start()
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }
}

