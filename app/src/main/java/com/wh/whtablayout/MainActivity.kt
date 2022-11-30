package com.wh.whtablayout

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wh.lib.OnTabSelectedListener
import com.wh.lib.SelectableTabsAdapter
import com.wh.whtablayout.databinding.ActivityMainBinding
import com.wh.whtablayout.databinding.ItemTabTextBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mViewBinding.whTabLayout.setAdapter(object : SelectableTabsAdapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val itemTabTextBinding: ItemTabTextBinding =
                    ItemTabTextBinding.inflate(layoutInflater)
                return object : ViewHolder(itemTabTextBinding.root) {}
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                val tv: TextView = holder.itemView.findViewById(R.id.tv_text)
                tv.text = position.toString()
            }

            override fun getItemCount(): Int {
                return 5
            }
        })
        mViewBinding.whTabLayout.mOnTabSelectedListener = object : OnTabSelectedListener {
            override fun onReSelected(view: View, position: Int) {
                Toast.makeText(view.context, "重新点击", Toast.LENGTH_LONG).show()
            }

            override fun onSelected(view: View, position: Int) {
                val tv: TextView = view.findViewById(R.id.tv_text)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            }

            override fun onUnSelected(view: View, position: Int) {
                val tv: TextView = view.findViewById(R.id.tv_text)
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            }

        }

        mViewBinding.vp2.adapter = object : FragmentStateAdapter(this){
            override fun getItemCount(): Int {
                return 5
            }

            override fun createFragment(position: Int): Fragment {
                return Fragment()
            }
        }

        mViewBinding.whTabLayout.setupViewPager2(mViewBinding.vp2)
    }
}