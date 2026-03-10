package com.wantime.wbangapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.block.dog.common.util.ToastUtil
import com.google.android.material.tabs.TabLayout
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentAuthorityRecordBinding
import com.wantime.wbangapp.inter.ITabLayoutListener
import com.wantime.wbangapp.model.LoginAuthorityBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.model.PlatformBean
import com.wantime.wbangapp.ui.activity.AppealActivity
import com.wantime.wbangapp.ui.adapter.AuthorityBindRecordAdapterRecycler
import com.wantime.wbangapp.ui.adapter.AuthorityLoginRecordAdapterRecycler
import com.wantime.wbangapp.ui.adapter.AuthorityZSRecordAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.EditDialog
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.IFormListener
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.AuthorityRecordViewModel
import kotlinx.android.synthetic.main.fragment_authority_record.*
import kotlinx.android.synthetic.main.fragment_authority_record.refreshLayout
import kotlinx.android.synthetic.main.ui_top_navigation.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.properties.Delegates


/***授权记录 登录授权 专属授权记录 代绑授权**/
class AuthorityRecordFragment :
    BaseFragment<AuthorityRecordViewModel, FragmentAuthorityRecordBinding>() {
    private var mTabsTitle: Array<String>? = null
    private var currentTab: TabLayout.Tab? = null

    private var mLoginAuthorityRecordAdapter: AuthorityLoginRecordAdapterRecycler? = null

    //    private var mAuthorityZSRecordAdapterRecycler: AuthorityZSRecordAdapterRecycler? = null
//    private var mAuthorityBindRecordAdapterRecycler: AuthorityBindRecordAdapterRecycler? = null
    private var mEditDialog: EditDialog? = null
    private var mMessage: MessageDialog by Delegates.notNull()
    private var mTabPosition = 0
    private var mOrder :LoginAuthorityBean.ListBean.RecordsBean? =null

    override val layoutResource: Int
        get() = R.layout.fragment_authority_record

    override fun afterInitView() {
        initToolbar()
        initNavTab()
        initAuthorityRecord()
        onInitRefresh()
    }

    private fun onInitRefresh() {
        refreshLayout.setOnRefreshListener {
            loadRefreshData(false)
        }
        refreshLayout.setOnLoadMoreListener {
            loadMoreData()
        }
    }

    override fun onProcessLogic() {

    }

    /***
     * isAutoRefresh 自动刷新还是手动刷新
     * */
    private fun loadRefreshData(isAutoRefresh: Boolean) {
        showProgress()
        when (mTabPosition) {
            0 -> {//登录授权记录第一页加载
                //加载第一页数据
                if (!isAutoRefresh || (isAutoRefresh && mLoginAuthorityRecordAdapter?.itemCount!! < 1)) {
                    mViewModel?.onLoginAuthorityRefreshNews("", "", "")?.observe(this, Observer {
                        xRecyclerView.post {
                            if (it.list != null && it.list.records != null) {
                                mLoginAuthorityRecordAdapter?.onClear()
                                mLoginAuthorityRecordAdapter?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
                                mLoginAuthorityRecordAdapter?.notifyDataSetChanged()
                            }
                            refreshLayout.finishRefresh()
                            hiddenProgress()
                        }
                    })
                } else hiddenProgress()
            }
//            1 -> {//专属授权第一页加载
//                if (!isAutoRefresh || (isAutoRefresh && mAuthorityZSRecordAdapterRecycler?.itemCount!! < 1)) {
//                    mViewModel?.onZSAuthorityRefreshNews()?.observe(this, Observer {
//                        xRecyclerView.post {
//                            if (it.list != null && it.list.records != null) {
//                                mAuthorityZSRecordAdapterRecycler?.onClear()
//                                mAuthorityZSRecordAdapterRecycler?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
//                                mAuthorityZSRecordAdapterRecycler?.notifyDataSetChanged()
//                            }
//                            refreshLayout.finishRefresh()
//                            hiddenProgress()
//                        }
//                    })
//                } else hiddenProgress()
//            }
//
//            2 -> {//绑定授权第一页加载
//                if (!isAutoRefresh || (isAutoRefresh && mAuthorityBindRecordAdapterRecycler?.itemCount!! < 1)) {
//                    mViewModel?.onBindAuthorityRefreshNews()?.observe(this, Observer {
//                        xRecyclerView.post {
//                            mAuthorityBindRecordAdapterRecycler?.onClear()
//                            refreshLayout.finishRefresh()
//                            hiddenProgress()
//                        }
//                    })
//                } else hiddenProgress()
//            }
        }
    }

    //加载更多数据
    private fun loadMoreData() {
        when (mTabPosition) {
            0 -> {//登录授权记录第一页加载
                //加载第一页数据
                mViewModel?.onLoginAuthorityLoadMore("", "", "")?.observe(this, Observer {
                    xRecyclerView.post {
                        mLoginAuthorityRecordAdapter?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
                        mLoginAuthorityRecordAdapter?.notifyDataSetChanged()
                        refreshLayout.finishLoadMore()
                    }
                })
            }
//            1 -> {//专属授权第一页加载
//                mViewModel?.onZSAuthorityLoadMore()?.observe(this, Observer {
//                    xRecyclerView.post {
//                        mAuthorityZSRecordAdapterRecycler?.pushData(it.list.records as ArrayList<LoginAuthorityBean.ListBean.RecordsBean>)
//                        mAuthorityZSRecordAdapterRecycler?.notifyDataSetChanged()
//                        refreshLayout.finishLoadMore()
//                    }
//                })
//            }
//
//            2 -> {//绑定授权第一页加载
//                mViewModel?.onBindAuthorityLoadMore()?.observe(this, Observer {
//                    xRecyclerView.post {
//                        refreshLayout.finishLoadMore()
//                    }
//                })
//            }
        }
    }

    //根据Tab切换适配器,从而显示不同的数据
    private fun changeAuthorityTab() {
//        when (mTabPosition) {
//            0 -> {
//                xRecyclerView.adapter = mLoginAuthorityRecordAdapter
//                mLoginAuthorityRecordAdapter?.notifyDataSetChanged()
//            }
//            1 -> {
//                xRecyclerView.adapter = mAuthorityZSRecordAdapterRecycler
//                mAuthorityZSRecordAdapterRecycler?.notifyDataSetChanged()
//            }
//            2 -> {
//                xRecyclerView.adapter = mAuthorityBindRecordAdapterRecycler
//                mAuthorityBindRecordAdapterRecycler?.notifyDataSetChanged()
//            }
//        }
        xRecyclerView.adapter = mLoginAuthorityRecordAdapter
        mLoginAuthorityRecordAdapter?.notifyDataSetChanged()
        loadRefreshData(true)
    }

    //初始化 3个适配器
    private fun initAuthorityRecord() {
        mLoginAuthorityRecordAdapter =
            AuthorityLoginRecordAdapterRecycler(activity!!, object : RecyclerItemClick {
                override fun onItemClick(itemView: View, iBean: Any) {
                    onRecyclerItemClick(itemView, iBean)
                }
            })
//        mAuthorityZSRecordAdapterRecycler =
//            AuthorityZSRecordAdapterRecycler(activity!!, object : RecyclerItemClick {
//                override fun onItemClick(itemView: View, iBean: Any) {
//
//                }
//            }, object : IFormListener {
//                override fun onFormPost(view: View, iBean: Any) {
//                    onPostAppeal(iBean as LoginAuthorityBean.ListBean.RecordsBean)
//                }
//            })
//        mAuthorityBindRecordAdapterRecycler =
//            AuthorityBindRecordAdapterRecycler(activity!!, object : RecyclerItemClick {
//                override fun onItemClick(itemView: View, iBean: Any) {
//
//                }
//            })

        changeAuthorityTab()
    }


    private fun initNavTab() {
        mEditDialog = EditDialog(activity!!)
        mMessage = MessageDialog(activity!!)
        mMessage.setTitle(getString(R.string.ui_system_title))
//        mTabsTitle = resources.getStringArray(R.array.record)
//        for (i in mTabsTitle!!.indices) {
//            if (i == 2) break
//            currentTab = mRecordTabLayout.getTabAt(i)
//            if (currentTab == null) {
//                currentTab = mRecordTabLayout.newTab()
//                mRecordTabLayout.addTab(currentTab!!)
//            }
//            currentTab?.customView = getTabView(i)
//        }
//        mRecordTabLayout.addOnTabSelectedListener(object : ITabLayoutListener() {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                mTabPosition = tab!!.position
//                changeAuthorityTab()
//            }
//        })

    }

    private fun initToolbar() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { activity!!.finish() }
        navTitle.text = resources.getStringArray(R.array.secondTitle)[0]
    }


//    private fun getTabView(position: Int): View {
//        val view: View = LayoutInflater.from(activity).inflate(R.layout.ui_record_nav_item, null)
//        val title: TextView = view.findViewById(R.id.mNavText)
//        title.text = mTabsTitle!![position]
//        return view
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> activity!!.finish()
//        }
//        return super.onOptionsItemSelected(item)
//    }

    //申诉
    private fun onPostAppeal(iBean: LoginAuthorityBean.ListBean.RecordsBean) {
        val dataJson = JSONObject()
        dataJson.put("taskId", iBean.id)
        val mIntent = Intent(mActivity, AppealActivity::class.java)
        mIntent.putExtra("data", dataJson.toString())

        startActivity(mIntent)
    }

    private fun onDestroyParam() {
        mTabsTitle = null
        currentTab = null
        mLoginAuthorityRecordAdapter = null
        mTabPosition = 0
    }

    override fun onDestroyView() {
        onDestroyParam()
        mEditDialog?.dismiss()
        mMessage.dismiss()
        super.onDestroyView()
    }

    private fun onRefreshPageAndRecycleView(iBean: PlatformBean) {
        if (iBean.platformList != null){
            val list = iBean.platformList as ArrayList<PlatformBean.PlatformListBean>
            for (item in list) {
                if ((item.appId != null && item.appId.contains(mOrder?.appId.toString()))) {

                    val paramJson = JSON.toJSONString(item)
                    /*Constants.setAppParamJson(JSONObject(paramJson))
                    val messageEvent = MessageEvent()
                    messageEvent.mType = MessageEvent.EventType.appParam.ordinal
                    messageEvent.dataJson = paramJson
                    EventBus.getDefault().post(messageEvent)*/
                    val mJson = JSONObject(paramJson)

                    mJson.put("order", JSON.toJSONString(mOrder))
                    Constants.openWxAuth(this, mJson.toString())
                    activity?.finish()
                }
            }
        }
    }

    // 处理续费按钮点击
    private fun onRenewalClick(iBean: LoginAuthorityBean.ListBean.RecordsBean) {
        showProgress()
        mViewModel?.onRenewalAuth(
            appId = iBean.appId ?: "",
            orderId = iBean.id ?: "",
            state = iBean.uuid ?: "", // 使用uuid作为state参数
            onkeyType = 1, // 续费固定传1
            modelType = 1, // 默认使用code模式
            grade = Constants.accountType
        )?.observe(this, Observer { result ->
            hiddenProgress()
            ToastUtil.show(activity!!, result.message ?: "续费请求已发送")
            
            // 如果续费成功，刷新列表
            if (result.ok == Constants.NET_OK) {
                loadRefreshData(false)
            }
        })
    }

    private fun onRecyclerItemClick(itemView: View, iBean: Any) {
        iBean as LoginAuthorityBean.ListBean.RecordsBean
        when (itemView) {
            is ImageView -> {
                showProgress()
                mViewModel?.onAfterSale(iBean.id)?.observe(this,
                    Observer {
                        ToastUtil.show(activity!!, it.message)
                        hiddenProgress()
                    })
            }
            is Button -> {
                // 检查是否是续费按钮
                if (itemView.id == R.id.renewalButton) {
                    // 处理续费按钮点击
                    onRenewalClick(iBean)
                } else {
                    // 处理复扫按钮点击
                    mOrder = iBean
                    if (mOrder?.type != 0) {
                        showProgress()
                        mViewModel?.onGetPlatformInfo()?.observe(this, Observer {
                            onRefreshPageAndRecycleView(it)
                            hiddenProgress()
                        })
                    }
                }
            }
            else -> {
                mEditDialog?.setTitle("设置订单备注\n当前订单：" + iBean.id)
                mEditDialog?.addConfirmListener(View.OnClickListener {
                    if (TextUtils.isEmpty(mEditDialog?.getMessage())) {
                        ToastUtil.show(activity!!, getString(R.string.please_input_mark))
                        return@OnClickListener
                    }
                    showProgress()
                    mViewModel?.onAddOrderRecordMark(iBean.id, mEditDialog?.getMessage()!!)
                        ?.observe(this,
                            Observer {
                                ToastUtil.show(activity!!, it.message)
                                hiddenProgress()
                                GlobalScope.launch(Dispatchers.Main) {
                                    mEditDialog?.dismiss()
                                }
                            })
                })
                mEditDialog?.show()
            }
        }

    }
}