package com.wh.lib

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

/**
 * @author: wh
 * @date: 2022/11/30 10:05
 * @desc:
 */
class WhTabLayout : LinearLayout, OnTabSelectedListener, View.OnLayoutChangeListener {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [TAB_MODE_FIXED, TAB_MODE_AUTO])
    annotation class TabMode
    companion object {
        const val TAB_MODE_FIXED = 0
        const val TAB_MODE_AUTO = 1

        const val UNDEFINED_INDICATOR_RES_ID = -1
    }

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SelectableTabsAdapter<*>? = null
    private var mViewPager2: ViewPager2? = null
    private var mObjectAnimator: ObjectAnimator? = null
    var mOnTabSelectedListener: OnTabSelectedListener? = null

    @TabMode
    private var mTabMode: Int = TAB_MODE_FIXED

    @LayoutRes
    private var mIndicatorResId = 0
    private var mIndicatorView: View? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (null == attrs) return
        var array: TypedArray? = null
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.WhTabLayout)
            mIndicatorResId = array.getResourceId(
                R.styleable.WhTabLayout_whIndicatorLayout,
                UNDEFINED_INDICATOR_RES_ID
            )
            mTabMode = array.getInt(R.styleable.WhTabLayout_whTabMode, TAB_MODE_FIXED)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            array?.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addTabUI()
        addIndicatorUI()
    }

    private fun addTabUI() {
        mRecyclerView = RecyclerView(context)
        mRecyclerView?.itemAnimator = null
        addView(mRecyclerView, generateRvLayoutParams())
    }

    private fun addIndicatorUI() {
        if (UNDEFINED_INDICATOR_RES_ID != mIndicatorResId) {
            mIndicatorView = inflate(context, mIndicatorResId, null)
            addView(mIndicatorView)
        }
    }

    private fun generateRvLayoutParams(): LayoutParams {
        var layoutParams: LayoutParams? = null
        if (VERTICAL == orientation) {
            if (TAB_MODE_FIXED == mTabMode) {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
        } else {
            if (TAB_MODE_FIXED == mTabMode) {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            }
        }
        if (null == layoutParams) {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        return layoutParams
    }

    private fun generateRvLayoutManager(): LayoutManager {
        return if (VERTICAL == orientation) {
            if (TAB_MODE_FIXED == mTabMode) {
                GridLayoutManager(context, getCount(), GridLayoutManager.VERTICAL, false)
            } else {
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        } else {
            if (TAB_MODE_FIXED == mTabMode) {
                GridLayoutManager(context, getCount(), GridLayoutManager.HORIZONTAL, false)
            } else {
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun getCount(): Int {
        return mAdapter?.itemCount ?: 0
    }

    /**
     * ??????tab?????????
     */
    fun setAdapter(adapter: SelectableTabsAdapter<*>) {
        mAdapter = adapter
        mAdapter?.mOnTabSelectedListener = this
        mRecyclerView?.let {
            it.adapter = adapter
            it.layoutManager = generateRvLayoutManager()
        }
    }

    /**
     * ??????ViewPager2
     *
     * @param viewPager2
     */
    fun setupViewPager2(viewPager2: ViewPager2) {
        mViewPager2 = viewPager2
        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mAdapter?.let {
                    it.selectedPosition = position
                }
            }
        })
    }

    override fun onReSelected(view: View, position: Int) {
        mOnTabSelectedListener?.onReSelected(view, position)
    }

    override fun onSelected(view: View, lastPosition: Int, position: Int) {
        if (mViewPager2 != null) {
            mViewPager2!!.currentItem = position
        }
        mOnTabSelectedListener?.onSelected(view, lastPosition, position)

        view.addOnLayoutChangeListener(this)
        fixedNotDisplayRange(position)
    }

    override fun onInitUI(view: View, position: Int) {
        mOnTabSelectedListener?.onInitUI(view, position)
        view.removeOnLayoutChangeListener(this)
    }

    private fun fixedNotDisplayRange(position: Int) {
        mRecyclerView?.let {
            if (it.layoutManager is LinearLayoutManager) {
                val linearLayoutManager: LinearLayoutManager =
                    it.layoutManager as LinearLayoutManager
                val first = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                val last = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (position !in first..last) {
                    linearLayoutManager.scrollToPosition(position)
                }
            }
        }
    }

    private fun startTranslation(view: View?, propertyName: String, start: Float?, end: Float?) {
        if (null == view || null == start || null == end) {
            return
        }
        clearAnimator()

        mObjectAnimator = ObjectAnimator.ofFloat(view, propertyName, start, end)
        mObjectAnimator?.let {
            it.duration = 100
            it.start()
        }
    }

    private fun clearAnimator() {
        mObjectAnimator?.let {
            if (it.isRunning)
                it.cancel()
        }
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        v?.let {
            if (VERTICAL == orientation) {
                mIndicatorView?.layoutParams?.width = v.width
                startTranslation(mIndicatorView, "x", mIndicatorView?.x, v.x)
            } else {
                mIndicatorView?.layoutParams?.height = v.height
                startTranslation(mIndicatorView, "y", mIndicatorView?.y, v.y)
            }
        }
    }
}