package com.mevius.kepcocal.ui.project_detail.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arlib.floatingsearchview.util.Util
import com.mevius.kepcocal.R
import com.mevius.kepcocal.data.db.entity.Machine


class SearchResultsListAdapter : RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder>() {
    private var mDataSet: List<Machine?> = ArrayList()
    private var mLastAnimatedItemPosition = -1

    interface OnItemClickListener {
        fun onClick(onClickMachine: Machine?)
    }

    private var mItemsOnClickListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mMachineCNum: TextView = view.findViewById(R.id.color_name)
        val mMachineName: TextView = view.findViewById(R.id.color_value)
        val mTextContainer: View = view.findViewById(R.id.text_container)

    }

    fun swapData(mNewDataSet: List<Machine?>?) {
        mDataSet = mNewDataSet as List<Machine?>
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
        val machineSuggestion: Machine? = mDataSet[position]
        if (machineSuggestion != null) {
            holder.mMachineCNum.text = machineSuggestion.computerizedNumber
        }
        if (machineSuggestion != null) {
            holder.mMachineName.text = machineSuggestion.lineName
        }
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

