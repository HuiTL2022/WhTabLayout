package com.wh.lib

import android.view.View

/**
 * @author: wh
 * @date: 2022/9/14 09:38
 * @desc:
 */
interface OnTabSelectedListener {
    fun onReSelected(view: View, position: Int)
    fun onSelected(view: View, position: Int)
    fun onUnSelected(view: View, position: Int)
}