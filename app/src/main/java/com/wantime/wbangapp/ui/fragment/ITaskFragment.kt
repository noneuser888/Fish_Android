package com.wantime.wbangapp.ui.fragment

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.tabs.TabLayout
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentItaskBinding
import com.wantime.wbangapp.inter.ITabLayoutListener
import com.wantime.wbangapp.viewmodel.ITaskViewModel
import kotlinx.android.synthetic.main.fragment_itask.mRecordTabLayout
import kotlinx.android.synthetic.main.ui_top_navigation.*

//我发布的任务
class ITaskFragment : BaseFragment<ITaskViewModel, FragmentItaskBinding>() {

    private var mTabsTitle: Array<String>? = null
    private var currentTab: TabLayout.Tab? = null


    private var mTabPosition: Int = 0

    companion object {
        private var mITaskFragment: ITaskFragment? = null
        fun newInstance(): ITaskFragment {
            if (mITaskFragment == null) {
                synchronized(ITaskFragment::class.java) {
                    mITaskFragment =
                        ITaskFragment()
                }
            }
            return mITaskFragment!!
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_itask


    override fun afterInitView() {
        initToolbar()
        initNavTab()
    }

    private fun initNavTab() {
        mTabsTitle = resources.getStringArray(R.array.itask)
        for (i in mTabsTitle!!.indices) {
            currentTab = mRecordTabLayout.getTabAt(i)
            if (currentTab == null) {
                currentTab = mRecordTabLayout.newTab()
                mRecordTabLayout.addTab(currentTab!!)
            }
            currentTab?.customView = getTabView(i)
        }
        mRecordTabLayout.addOnTabSelectedListener(object : ITabLayoutListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mTabPosition = tab!!.position

            }
        })

    }

    private fun initToolbar() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { activity!!.finish() }
        navTitle.text = getString(R.string.ui_my_task_list)
    }


    private fun getTabView(position: Int): View {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.ui_record_nav_item, null)
        val title: TextView = view.findViewById(R.id.mNavText)
        title.text = mTabsTitle!![position]
        return view
    }

    override fun onProcessLogic() {

    }

}