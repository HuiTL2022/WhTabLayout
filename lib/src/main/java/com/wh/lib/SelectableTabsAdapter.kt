package com.wh.lib

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: wh
 * @date: 2022/9/14 09:15
 * @desc:
 */
abstract class SelectableTabsAdapter<VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {
    var mOnTabSelectedListener: OnTabSelectedListener? = null
    private var mLastSelectedPosition = -1
    private var mSelectedPosition = 0

    override fun onBindViewHolder(holder: VH, @SuppressLint("RecyclerView") position: Int) {
        if (position == mSelectedPosition) {
            holder.itemView.isSelected = true
            mOnTabSelectedListener?.onSelected(holder.itemView, mLastSelectedPosition, position)
        } else {
            holder.itemView.isSelected = false
            mOnTabSelectedListener?.onInitUI(holder.itemView, position)
        }
        holder.itemView.setOnClickListener { v: View? ->
            mOnTabSelectedListener?.let {
                if (mSelectedPosition == position) {
                    it.onReSelected(holder.itemView, position)
                } else {
                    //取消之前选择
                    mLastSelectedPosition = mSelectedPosition

                    mSelectedPosition = position
                    notifyItemChanged(mSelectedPosition)
                    notifyItemChanged(mLastSelectedPosition)
                }
            }
        }
    }

    var selectedPosition: Int
        get() = mSelectedPosition
        set(selectedPosition) {
            if (mSelectedPosition != selectedPosition) {
                mLastSelectedPosition = mSelectedPosition
                mSelectedPosition = selectedPosition
                notifyItemChanged(mLastSelectedPosition)
                notifyItemChanged(mSelectedPosition)
            }
        }
}