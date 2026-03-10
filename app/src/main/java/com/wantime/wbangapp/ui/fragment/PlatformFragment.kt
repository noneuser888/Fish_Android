package com.wantime.wbangapp.ui.fragment

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentPlatformBinding
import com.wantime.wbangapp.model.PlatformBean
import com.wantime.wbangapp.ui.activity.IWebActivity
import com.wantime.wbangapp.ui.adapter.PlatformAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.widget.SearchView
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.PlatformViewModel
import kotlinx.android.synthetic.main.fragment_platform.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

//平台
class PlatformFragment : BaseFragment<PlatformViewModel, FragmentPlatformBinding>() {


    private var mAdapter: PlatformAdapterRecycler? = null
    private var headHeight: Int = -1
    private var floatHeight: Int = -1
    private var messageDialog: MessageDialog? = null


    override val layoutResource: Int
        get() = R.layout.fragment_platform

    override fun afterInitView() {
        messageDialog = MessageDialog(activity!!)
        initToolbar()
        initSearchView()
        initPageAndRecyclerView()
        onInitRefresh()
    }

    private fun changeTitle() {
        val mTitle = mToolbar.getChildAt(0)
        if (mTitle is AppCompatTextView) {
            mTitle.textSize = 14f
        }
    }


    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(true)
            actionBar.setHomeAsUpIndicator(R.mipmap.nav_ic_back)
            actionBar.title = resources.getStringArray(R.array.app_nav)[1]
        }
        setHasOptionsMenu(true)
        changeTitle()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.authority_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                searchView.visibility = View.VISIBLE
            }
            android.R.id.home -> activity!!.finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initSearchView() {
        searchView.addSearchViewListener(object : SearchView.SearchViewListener {
            override fun onSearch(mText: String) {
                mAdapter?.onFilter(mText)
            }
        })
    }

    private fun onInitRefresh() {
        refreshLayout.setOnRefreshListener {
            mViewModel?.onGetPlatformInfo()?.observe(this, Observer {
                onRefreshPageAndRecycleView(it)
            })
        }
        refreshLayout.setEnableLoadMore(false)
    }

    private fun initPageAndRecyclerView() {
        //添加自定义分割线
        mAdapter = PlatformAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@PlatformFragment.onRecycleItemClick(itemView, iBean)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@PlatformFragment.onPagerItemClick(itemView, iBean)
            }
        }, object : IFormListener {
            override fun onFormPost(view: View, iBean: Any) {
                this@PlatformFragment.onFormPost(view, iBean)
            }
        })
        mRecyclerView.adapter = mAdapter
//        val manager = mRecyclerView.layoutManager as LinearLayoutManager
//        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (headHeight < 0) {
//                    headHeight = manager.findViewByPosition(0)!!.height
//                    floatHeight = floatTittleLayout.height
//                }
//                val position = manager.findFirstVisibleItemPosition()
//                val scrollHeight = mRecyclerView.computeVerticalScrollOffset()
//                floatTittleLayout.visibility =
//                    if (position > 0 || (headHeight > 0 &&
//                                scrollHeight >= (headHeight - floatHeight))
//                    ) View.VISIBLE else View.INVISIBLE
//            }
//        })
    }

    override fun onRefresh() {
        onProcessLogic()
    }

    override fun onProcessLogic() {
        mViewModel?.onGetPlatformInfo()?.observe(this, Observer {
            onRefreshPageAndRecycleView(it)
        })
    }

    private fun onRefreshPageAndRecycleView(iBean: PlatformBean) {
        mRecyclerView?.post {
            mAdapter?.onClear()
            if (iBean.platformList != null)
                mAdapter?.pushDataList(iBean.platformList as ArrayList<PlatformBean.PlatformListBean>)
            if (iBean.banner != null)
                mAdapter?.pushBannerList(iBean.banner as ArrayList<PlatformBean.BannerBean>)
            mAdapter?.notifyDataSetChanged()
            refreshLayout.finishRefresh()
        }
    }

    private fun onPagerItemClick(itemView: View, iBean: Any) {
        val iItemBean = iBean as PlatformBean.BannerBean
        val mIntent = Intent(activity!!, IWebActivity::class.java)
        val mJson = JSONObject()
        mJson.put("webUrl", iItemBean.webLink)
        mIntent.putExtra("data", mJson.toString())
        startActivity(mIntent)
    }

    //只有登录后才能做下面动作
    private fun onRecycleItemClick(itemView: View, iBean: Any) {
        val iItemBean = iBean as PlatformBean.PlatformListBean
        if (Constants.isLogin(activity!!)) {
//            Integer.parseInt("2.ds")
            // only root
            if (TextUtils.isEmpty(iItemBean.scope)) {
                messageDialog?.setContent(getString(R.string.ui_weibang_xp))!!.show()
                return
            }
            val paramJson = JSON.toJSONString(iItemBean)
            Constants.setAppParamJson(JSONObject(paramJson))
            val messageEvent = MessageEvent()
            messageEvent.mType = MessageEvent.EventType.appParam.ordinal
            messageEvent.dataJson = paramJson
            EventBus.getDefault().post(messageEvent)
            Constants.openWxAuth(this, JSON.toJSONString(iItemBean))
        }
    }

    //点击了扫描按钮后的动作
    private fun onFormPost(view: View, iBean: Any) {
        val iItemBean = iBean as PlatformBean.PlatformListBean
        if (Constants.isLogin(activity!!)) {
            Constants.openCaptureByFragment(
                this,
                JSON.toJSONString(iItemBean),
                if (!TextUtils.isEmpty(iItemBean.platformName)) iItemBean.platformName else iItemBean.nickname,
                0
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        messageDialog?.dismiss()
        super.onDestroy()
    }
}