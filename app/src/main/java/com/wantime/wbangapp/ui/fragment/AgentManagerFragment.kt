package com.wantime.wbangapp.ui.fragment

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.alibaba.fastjson.JSON
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentAgentManagerBinding
import com.wantime.wbangapp.model.AgentMangerBean
import com.wantime.wbangapp.model.MenuPopupBean
import com.wantime.wbangapp.ui.activity.RechargeActivity
import com.wantime.wbangapp.ui.activity.RechargeRecordActivity
import com.wantime.wbangapp.ui.adapter.AgentManagerRecycler
import com.wantime.wbangapp.ui.adapter.MenuPopupRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerAdapterItemClick
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.EditDialog
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.widget.MenuPopupWindow
import com.wantime.wbangapp.ui.widget.SearchView
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.AgentManagerViewModel
import kotlinx.android.synthetic.main.fragment_agent_manager.mRecyclerView
import kotlinx.android.synthetic.main.fragment_agent_manager.refreshLayout
import kotlinx.android.synthetic.main.new_ui_agent_manager_adapter_head.searchView
import kotlinx.android.synthetic.main.ui_top_navigation.*
import kotlinx.coroutines.selects.whileSelect
import kotlin.properties.Delegates

//代理管理和用户管理
class AgentManagerFragment : BaseFragment<AgentManagerViewModel, FragmentAgentManagerBinding>() {

    private var mAdapter: AgentManagerRecycler by Delegates.notNull()
    private var mSearchKey = ""
    private var mEditDialog: EditDialog by Delegates.notNull()
    private var mMessageDialog: MessageDialog by Delegates.notNull()
    private var mMenuPopupWindow: MenuPopupWindow by Delegates.notNull()


    override val layoutResource: Int
        get() = R.layout.fragment_agent_manager

    override fun afterInitView() {
        initPopMenuWindow()
        initRecyclerView()
        mEditDialog = EditDialog(activity!!).setTitle(getString(R.string.new_ui_set_remark))
        mMessageDialog = MessageDialog(activity!!).setTitle(getString(R.string.ui_system_title))
    }


    private fun initPopMenuWindow() {
        mMenuPopupWindow = MenuPopupWindow(activity!!)
        mMenuPopupWindow.onChangeMenuType(onGetShowMenuType())

        mMenuPopupWindow.addRecyclerItemClick(object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AgentManagerFragment.onMenuItemClick(itemView, iBean)
            }
        })
    }

    private fun onGetShowMenuType(): MenuPopupBean.MenuType {
        var mType: MenuPopupBean.MenuType = MenuPopupBean.MenuType.MENU_NULL
        val iBean = Constants.onGetUserBaseInfoWithNoToken()
        val viewType = mParamJson!!.optInt("viewType", 0)
        when (iBean.level) {
            "1" -> when (viewType) {
                0 -> mType = MenuPopupBean.MenuType.Agent_PrimaryAgent
                1 -> mType = MenuPopupBean.MenuType.User_PrimaryAgent
            }
            "2" -> when (viewType) {
                0 -> mType = MenuPopupBean.MenuType.MENU_NULL
                1 -> mType = MenuPopupBean.MenuType.User_SecondaryAgent
            }
            else -> when (viewType) {
                0 -> mType = MenuPopupBean.MenuType.MENU_NULL
                1 -> mType = MenuPopupBean.MenuType.MENU_NULL
            }
        }

        return mType
    }

    override fun onProcessLogic() {
        onRefreshList("")
    }

    override fun onRefresh() {
        showProgress()
        binding?.agentManagerHead?.personInfoLayout?.xUserBean =
            Constants.onGetUserBaseInfoWithNoToken()
        onRefreshList(mSearchKey)
    }

    private fun onRefreshList(searchKeys: String) {
        this.mSearchKey = searchKeys
        when (mParamJson!!.optInt("viewType", 0)) {
            0 -> mViewModel?.onRefreshAgentManager(mSearchKey)!!.observe(this, Observer {
                if (it.records != null) {
                    mAdapter.onClear()
                    mAdapter.pushDataList(it.records as ArrayList<AgentMangerBean.RecordsBean>)
                    mAdapter.notifyDataSetChanged()
                    refreshLayout.finishRefresh(true)
                    hiddenProgress()
                }
            })
            1 -> mViewModel?.onRefreshUserManager(mSearchKey)!!.observe(this, Observer {
                if (it.records != null) {
                    mAdapter.onClear()
                    mAdapter.pushDataList(onResetRecord(it.records as ArrayList<AgentMangerBean.RecordsBean>))
                    mAdapter.notifyDataSetChanged()
                    refreshLayout.finishRefresh(true)
                    hiddenProgress()
                }
            })
        }
    }

    private fun onLoadMoreList(searchKeys: String) {
        when (mParamJson!!.optInt("viewType", 0)) {
            0 -> mViewModel?.onLoadMoreAgentManager(searchKeys)!!.observe(this, Observer {
                if (it.records != null) {
                    mAdapter.pushDataList(it.records as ArrayList<AgentMangerBean.RecordsBean>)
                    mAdapter.notifyDataSetChanged()
                    refreshLayout.finishLoadMore(true)
                }
            })
            1 -> mViewModel?.onLoadMoreUserManager(searchKeys)!!.observe(this, Observer {
                if (it.records != null) {
                    mAdapter.pushDataList(onResetRecord(it.records as ArrayList<AgentMangerBean.RecordsBean>))
                    mAdapter.notifyDataSetChanged()
                    refreshLayout.finishLoadMore(true)
                }
            })
        }
    }

    private fun onResetRecord(recordList: ArrayList<AgentMangerBean.RecordsBean>): ArrayList<AgentMangerBean.RecordsBean> {
        val menuType = onGetShowMenuType()
        val isVisiable = menuType == MenuPopupBean.MenuType.User_PrimaryAgent
        if (isVisiable)
            for (recordItem in recordList) recordItem.isVisiable = View.VISIBLE
        return recordList
    }

    private fun initRecyclerView() {
        val iBean = Constants.onGetUserBaseInfoWithNoToken()
        setNavigationDrawableForLeft(navRightIcon, R.mipmap.caidan)
        navRightIcon.visibility = View.VISIBLE
        navRightIcon.setOnClickListener { mMenuPopupWindow.showAsDropDown(navRightIcon) }

        refreshLayout.setOnRefreshListener { onRefreshList(mSearchKey) }
        refreshLayout.setOnLoadMoreListener { onLoadMoreList(mSearchKey) }
        searchView.addSearchViewListener(object : SearchView.SearchViewListener {
            override fun onSearch(mText: String) {
                showProgress()
                onRefreshList(mText)
            }
        })
        //添加自定义分割线
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.custom_divider)!!)
        mRecyclerView.addItemDecoration(divider)
        //设置适配器
        mAdapter = AgentManagerRecycler(activity!!, object : RecyclerAdapterItemClick {
            override fun onItemClick(itemView: View, iBean: Any, position: Int) {
                this@AgentManagerFragment.onItemClick(itemView, iBean, position)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AgentManagerFragment.onPagerItemClick(itemView, iBean)
            }
        })
        mRecyclerView.adapter = mAdapter
        initUserInfo()
    }

    private fun initUserInfo() {
        when (mParamJson!!.optInt("viewType", 0)) {
            0 -> {
                binding?.agentManagerHead?.mNavigationBar?.findViewById<TextView>(R.id.navTitle)?.text =
                    getString(R.string.new_ui_agent_manager)
                binding?.agentManagerHead?.userMyAgent?.text = getString(R.string.new_ui_my_agent)
            }
            1 -> {
                binding?.agentManagerHead?.mNavigationBar?.findViewById<TextView>(R.id.navTitle)?.text =
                    getString(R.string.new_ui_user_manager)
                binding?.agentManagerHead?.userMyAgent?.text = getString(R.string.new_ui_my_user)
            }
        }
        binding?.agentManagerHead?.mNavigationBar!!.findViewById<TextView>(R.id.navBack)
            .setOnClickListener {
                activity!!.finish()
            }

        binding?.agentManagerHead?.personInfoLayout?.xUserBean =
            Constants.onGetUserBaseInfoWithNoToken()
    }

    private fun onPagerItemClick(itemView: View, iBean: Any) {

    }

    private fun onMenuItemClick(itemView: View, iBean: Any) {
        mMenuPopupWindow.dismiss()
        iBean as MenuPopupBean
        when (iBean.type) {
            MenuPopupBean.MenuType.Agent_PrimaryAgent -> {
                startActivity(Intent(activity!!, RechargeRecordActivity::class.java))
            }
            MenuPopupBean.MenuType.User_PrimaryAgent -> {
                when (iBean.option) {
                    0 -> startActivity(Intent(activity!!, RechargeRecordActivity::class.java))
                }
            }

            MenuPopupBean.MenuType.User_SecondaryAgent -> {
                when (iBean.option) {
                    0 -> startActivity(Intent(activity!!, RechargeRecordActivity::class.java))
                }
            }
            else -> {
            }
        }
    }

    private fun onItemClick(itemView: View, iBean: Any, position: Int) {
        iBean as AgentMangerBean.RecordsBean
        when (position) {
            0 -> {//添加备注,
                mEditDialog.apply {
                    addConfirmListener(View.OnClickListener {
                        if (TextUtils.isEmpty(mEditDialog.getMessage())) {
                            ToastUtil.show(activity!!, getString(R.string.new_ui_set_remark))
                            return@OnClickListener
                        }
                        mEditDialog.dismiss()
                        showProgress()
                        mViewModel?.onAddRemark(iBean.id, mEditDialog.getMessage())!!
                            .observe(activity!!,
                                Observer {
                                    hiddenProgress()
                                    ToastUtil.show(activity!!, it.message)
                                    onRefreshList(mSearchKey)
                                })
                    })
                    show()
                }
            }
            1 -> {//充值
                val mIntent = Intent(activity, RechargeActivity::class.java)
                mIntent.putExtra("data", JSON.toJSONString(iBean))
                startActivity(mIntent)
            }
            2 -> {//封禁或者解禁用户
                mMessageDialog.setContent(
                    if (iBean.status == 0) getString(R.string.new_ui_bannedUser) else getString(
                        R.string.new_ui_not_bannedUser
                    )
                ).addConfirmListener(View.OnClickListener {
                    mMessageDialog.dismiss()
                    showProgress()
                    mViewModel?.onBannedUser(iBean.id)!!
                        .observe(activity!!,
                            Observer {
                                ToastUtil.show(activity!!, it.message)
                                onRefreshList(mSearchKey)
                                hiddenProgress()
                            })
                }).show()
            }
            3 -> {
                mMessageDialog.setContent(getString(R.string.new_ui_upgrade_tip))
                    .addConfirmListener(View.OnClickListener {
                        mMessageDialog.dismiss()
                        showProgress()
                        mViewModel?.onUserUpgrade(iBean.id)!!
                            .observe(activity!!,
                                Observer {
                                    ToastUtil.show(activity!!, it.message)
                                    onRefreshList(mSearchKey)
                                    hiddenProgress()
                                })
                    }).show()
            }
        }
    }
}