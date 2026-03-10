package com.wantime.wbangapp.ui.fragment

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentExclusiveBinding
import com.wantime.wbangapp.model.ExclusiveBean
import com.wantime.wbangapp.model.PlatformBean
import com.wantime.wbangapp.ui.adapter.ExclusiveAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.ui.widget.SearchView
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.ExclusiveViewModel
import kotlinx.android.synthetic.main.fragment_authority.mRecyclerView
import kotlinx.android.synthetic.main.fragment_exclusive.*


//专属对接列表
class ExclusiveFragment : BaseFragment<ExclusiveViewModel, FragmentExclusiveBinding>() {

    private var mAdapter: ExclusiveAdapterRecycler? = null
    private var headHeight: Int = -1
    private var floatHeight: Int = -1
    private var messageDialog: MessageDialog? = null

    override val layoutResource: Int
        get() = R.layout.fragment_exclusive

    override fun afterInitView() {
        messageDialog = MessageDialog(activity!!)
        navBack.setOnClickListener { activity!!.finish() }
        val padding = Constants.dip2px(activity!!, 10f)
        exclusiveHeadLine.setPadding(padding, 0, padding, 0)
        initRecyclerView()
    }

    override fun onProcessLogic() {
        mViewModel?.onRefreshNews()?.observe(this, Observer {
            onInitRefreshRecycler(it)
        })
    }

    override fun onRefresh() {
        onProcessLogic()
    }

    private fun initRecyclerView() {
        searchView.addSearchViewListener(object : SearchView.SearchViewListener {
            override fun onSearch(mText: String) {
                mAdapter?.onFilter(mText)
            }

        })
        //设置适配器
        mAdapter = ExclusiveAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@ExclusiveFragment.onItemClick(itemView, iBean)
            }
        }, object : IFormListener {
            override fun onFormPost(view: View, iBean: Any) {
                this@ExclusiveFragment.onFormPost(view, iBean)
            }
        })
        mRecyclerView.adapter = mAdapter

        val manager = mRecyclerView.layoutManager as LinearLayoutManager

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (headHeight < 0) {
                    headHeight = manager.findViewByPosition(0)!!.height
                    floatHeight = exclusiveHeadLine.height
                }
                val position = manager.findFirstVisibleItemPosition()
                val scrollHeight = mRecyclerView.computeVerticalScrollOffset()
                exclusiveHeadLine.visibility =
                    if (position > 0 || (headHeight > 0 &&
                                scrollHeight >= (headHeight - floatHeight))
                    ) View.VISIBLE else View.INVISIBLE
            }
        })
    }

    private fun onInitRefreshRecycler(iBean: ExclusiveBean) {
        mRecyclerView.post {
            mAdapter?.setOnlineNumber(iBean.onlineNumber)
            exclusiveHeadLine.findViewById<TextView>(R.id.headLine).text =
                getString(R.string.ui_online_number) + iBean.onlineNumber
            if (iBean.list != null) {
                mAdapter?.pushDataList(iBean.list as ArrayList<ExclusiveBean.ListBean>)
                mAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun onItemClick(itemView: View, iBean: Any) {
        val iItemBean = iBean as ExclusiveBean.ListBean
        if (Constants.isLogin(activity!!)) {
            // only root
            if(TextUtils.isEmpty(iItemBean.scope)){
                messageDialog?.setContent(getString(R.string.ui_weibang_xp))!!.show()
                return
            }
            Constants.openWxAuth(this, JSON.toJSONString(iItemBean))
        }
    }

    //点击扫描按钮跳转
    private fun onFormPost(view: View, iBean: Any) {
        val iItemBean = iBean as ExclusiveBean.ListBean
        if (Constants.isLogin(activity!!)) {
            Constants.openCaptureByFragment(
                this,
                JSON.toJSONString(iItemBean), iItemBean.platformName, 1
            )
        }
    }

    override fun onDestroy() {
        messageDialog?.dismiss()
        super.onDestroy()
    }
}