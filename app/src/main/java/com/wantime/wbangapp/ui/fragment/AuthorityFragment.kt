package com.wantime.wbangapp.ui.fragment


import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentAuthorityBinding
import com.wantime.wbangapp.model.MenuPopupBean
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.ui.activity.AgentManagerActivity
import com.wantime.wbangapp.ui.activity.PlatformListActivity
import com.wantime.wbangapp.ui.activity.RecordActivity
import com.wantime.wbangapp.ui.activity.UserUpgradeActivity
import com.wantime.wbangapp.ui.adapter.AuthorityAdapterRecycler
import com.wantime.wbangapp.ui.adapter.AuthorityPagerAdapter
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.AccountTypeDialog
import com.wantime.wbangapp.ui.dialog.CreateChildDialog
import com.wantime.wbangapp.ui.dialog.EditDialog
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.widget.MenuPopupWindow
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.AuthorityViewModel
import kotlinx.android.synthetic.main.fragment_authority.*
import kotlinx.android.synthetic.main.new_ui_main_adapter_head.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.Delegates


class AuthorityFragment :
    BaseFragment<AuthorityViewModel, FragmentAuthorityBinding>() {
    private var mAdapter: AuthorityAdapterRecycler by Delegates.notNull()
    private var mPageAdapter: AuthorityPagerAdapter<NewsBean.BannerBean> by Delegates.notNull()
    private var iMessageDialog: MessageDialog by Delegates.notNull()
    private var mMenuPopupWindow: MenuPopupWindow by Delegates.notNull()
    private var mEditDialog: EditDialog by Delegates.notNull()
    private var mAccountTypeDialog: AccountTypeDialog by Delegates.notNull()
    private var iCreateChildDialog: CreateChildDialog by Delegates.notNull()
    private var mKeyChargeDialog: EditDialog by Delegates.notNull()


    override val layoutResource: Int
        get() = R.layout.fragment_authority

    override fun afterInitView() {
//        initToolbar()
//        changeTitle()
        initRecyclerView()
        onInitRefresh()
    }

    private fun onInitRefresh() {
//        refreshLayout.setOnRefreshListener {
//            mViewModel?.onGetAuthHomeList()
//                ?.observe(this, Observer {
//                    if (it.article != null)
//                        onRefreshRecycler(it)
//                })
//        }
//        refreshLayout.setEnableLoadMore(false)
    }

    override fun onRefresh() {
        binding?.mainAdapterHead?.xUserBean = Constants.onGetUserBaseInfoWithNoToken()
        onProcessLogic()
    }

    override fun onProcessLogic() {
        mViewModel?.onGetAuthHomeList()
            ?.observe(this, Observer {
                if (it.project != null)
                    onRefreshRecycler(it)
            })
    }

//    private fun initToolbar() {
//        iMessageDialog = MessageDialog(activity!!)
//        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
//        setHasOptionsMenu(true)
//    }

//    private fun changeTitle() {
//        val mTitle = mToolbar.getChildAt(0) as TextView
//        mTitle.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;//填充父类
//        mTitle.gravity = Gravity.CENTER_HORIZONTAL;//水平居中，CENTER，即水平也垂直，自选
//        mTitle.text = resources.getStringArray(R.array.app_nav)[0]
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        menu.clear()
//        inflater.inflate(R.menu.authority_menu, menu)
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_share -> {
//                val agentcyId = Constants.onGetUserBaseInfoWithToken().agencyId
//                Constants.openWebView(
//                    activity!!,
//                    "客服",
//                    Constants.baseAPIUrl + "authPage?agencyId=" + agentcyId
//                )
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun initRecyclerView() {
        //添加自定义分割线
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.custom_divider)!!)
        mRecyclerView.addItemDecoration(divider)
        //设置适配器
        mAdapter = AuthorityAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AuthorityFragment.onItemClick(itemView, iBean)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AuthorityFragment.onPagerItemClick(itemView, iBean)
            }
        })
        mPageAdapter = AuthorityPagerAdapter(object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AuthorityFragment.onPagerItemClick(itemView, iBean)
            }
        })

        mRecyclerView.adapter = mAdapter
        mEditDialog = EditDialog(activity!!)
            .setTitle(getString(R.string.new_ui_new_nick_name))
            .addConfirmListener(View.OnClickListener {
                if (TextUtils.isEmpty(mEditDialog.getMessage())) {
                    ToastUtil.show(activity!!, getString(R.string.new_ui_please_enter_nickName))
                    return@OnClickListener
                }
                mEditDialog.dismiss()
                showProgress()
                mViewModel?.onUpdateNickname(mEditDialog.getMessage())!!
                    .observe(activity!!, Observer {
                        hiddenProgress()
                        if (it.code == Constants.NET_OK)
                            Constants.sendRefreshUserBaseInfo()
                        ToastUtil.show(activity!!, it.message)
                    })
            })
        mKeyChargeDialog = EditDialog(activity!!)
            .setTitle(getString(R.string.new_ui_input_key_charge))
            .addConfirmListener(View.OnClickListener {
                if (TextUtils.isEmpty(mKeyChargeDialog.getMessage())) {
                    ToastUtil.show(activity!!, getString(R.string.new_ui_input_key_charge_empty))
                    return@OnClickListener
                }
                mKeyChargeDialog.dismiss()
                showProgress()
                mViewModel?.onKeyCharge(mKeyChargeDialog.getMessage())!!
                    .observe(activity!!, Observer {
                        hiddenProgress()
                        if (it.code == Constants.NET_OK)
                            Constants.sendRefreshUserBaseInfo()
                        ToastUtil.show(activity!!, it.message)
                    })
            })
        iMessageDialog = MessageDialog(activity!!)
            .setTitle(getString(R.string.ui_system_title))
            .setContent(getString(R.string.ui_exist_login_title_desc))
        iCreateChildDialog = CreateChildDialog(activity!!).addConfirmListener(View.OnClickListener {
            val tempNumber = Constants.getRealInt(iCreateChildDialog.getMessage())
            if (tempNumber <= 0) {
                ToastUtil.show(activity!!, "请输入正确的数量!")
                return@OnClickListener
            }
            if (tempNumber > 200) {
                ToastUtil.show(activity!!, "数量不能超过200!")
                return@OnClickListener
            }
            iCreateChildDialog.dismiss()
            showProgress()
            mViewModel?.onRegisterBatchUser(tempNumber)!!.observe(activity!!, Observer {
                hiddenProgress()
                if (it.code == Constants.NET_OK) {
                    val mArray = JSONArray(it.message)
                    var message = ""
                    for (k in 0 until mArray.length()) message += "" + mArray[k] + ","
                    GlobalScope.launch(Dispatchers.Main) {
                        val mClipboardManager = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                        if (mClipboardManager != null)mClipboardManager.text = message
                        iMessageDialog.setTitle(getString(R.string.ui_system_title)).setContent(getString(R.string.ui_auto_copy_accounts_tips)).show()
                    }

                } else ToastUtil.show(activity!!, it.message)
            })
        })
        mMenuPopupWindow = MenuPopupWindow(activity!!)
        mMenuPopupWindow.onChangeMenuType(MenuPopupBean.MenuType.MAIN_AllUser)
        mMenuPopupWindow.addRecyclerItemClick(object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AuthorityFragment.onMenuItemClick(itemView, iBean)
            }
        })
        mAccountTypeDialog = AccountTypeDialog(activity!!)
        initUserInfo()
    }

    private fun initUserInfo() {
        binding?.mainAdapterHead?.xItemClick = object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@AuthorityFragment.onItemClick(itemView, iBean)
            }
        }
        val xUserBean = Constants.onGetUserBaseInfoWithNoToken()
        binding?.mainAdapterHead?.xUserBean = xUserBean
        mViewPager.adapter = mPageAdapter
        if (TextUtils.isEmpty(xUserBean.inviteUrl)) {
            copyButton.visibility = View.GONE
            copyLinkText.visibility = View.GONE
        }
        copyButton.setOnClickListener {
            val clipManager = getSystemService(activity!!, ClipboardManager::class.java)
            clipManager?.text = xUserBean.inviteUrl + "\n" + xUserBean.inviteCode
            ToastUtil.show(activity!!, getString(R.string.new_ui_copy_finished))
        }
        when (xUserBean.level) {
            "2" -> {
                new_ui_agent_manager.visibility = View.GONE
                new_ui_user_manager.visibility = View.VISIBLE
            }
            "0" -> {
                setNavigationDrawableForTop(new_ui_agent_manager, R.mipmap.dailixiangguan)
                new_ui_user_manager.visibility = View.GONE
                new_ui_agent_manager.text = getString(R.string.new_ui_agent_manager_xg)
            }
        }
    }


    private fun onRefreshRecycler(itBean: NewsBean) {
        mRecyclerView.post {
            mAdapter.onClear()
            if (itBean.project != null)
                mAdapter.pushDataList(itBean.project as ArrayList<NewsBean.ProjectBean>)
            if (itBean.banner != null)
                mPageAdapter.pushDataList(itBean.banner as ArrayList<NewsBean.BannerBean>)
            mAdapter.notifyDataSetChanged()
            mPageAdapter.notifyDataSetChanged()
        }
    }

    private fun onPagerItemClick(itemView: View, iBean: Any) {
        val iItemBean = iBean as NewsBean.BannerBean
        Constants.openWebView(activity!!, "", iItemBean.webLink)
    }

    private fun onItemClick(itemView: View, iBean: Any) {
        if (iBean is NewsBean.ProjectBean) {
            if (!TextUtils.isEmpty(iBean.webLink))
                Constants.openWebView(activity!!, iBean.title, iBean.webLink)
        } else if (iBean is Int) {
            when (iBean) {
                0 -> mMenuPopupWindow.showAsDropDown(itemView)
                2 -> startActivity(Intent(activity, PlatformListActivity::class.java))
                3 -> startActivity(Intent(activity!!, RecordActivity::class.java))
                4 -> {
                    val xUserBean = Constants.onGetUserBaseInfoWithNoToken()
                    if (xUserBean.level == "0") {//普通用户联系自己的代理
                        startActivity(Intent(activity!!, UserUpgradeActivity::class.java))
                    } else {
                        val mIntent = Intent(activity!!, AgentManagerActivity::class.java)
                        val mDataJson = JSONObject()
                        mDataJson.put("viewType", 0)
                        mIntent.putExtra("data", mDataJson.toString())
                        startActivity(mIntent)
                    }
                }
                5 -> {
                    val mIntent = Intent(activity!!, AgentManagerActivity::class.java)
                    val mDataJson = JSONObject()
                    mDataJson.put("viewType", 1)
                    mIntent.putExtra("data", mDataJson.toString())
                    startActivity(mIntent)
                }
            }
        }
    }

    private fun onMenuItemClick(itemView: View, iBean: Any) {
        mMenuPopupWindow.dismiss()
        iBean as MenuPopupBean
        when (iBean.option) {
            0 -> mEditDialog.show()
            1 -> Constants.openWebView(activity!!, "", Constants.commonProblemUrl)
            2 -> mAccountTypeDialog.show()
            3 -> iCreateChildDialog.show()
            4 -> mKeyChargeDialog.show()
            5 -> {
                iMessageDialog.setTitle(getString(R.string.ui_system_title))
                    .setContent(getString(R.string.ui_exist_login_title_desc)).addConfirmListener(View.OnClickListener {
                        mViewModel?.onLoginOut()?.observe(this, Observer {
                            if (it.ok == Constants.NET_OK) {
                            } else ToastUtil.show(activity!!, it.message)
                        })
                        GlobalScope.launch(Dispatchers.Main) {
                            Constants.clearLogin(activity!!)
                            notifyExist()
                            Constants.openLogin(activity!!)
                        }
                        iMessageDialog.dismiss()
                    }).show()
            }
        }
    }

//
//    private fun onInitMenusData(): ArrayList<MenuPopupBean> {
//        val menuList = ArrayList<MenuPopupBean>()
//        val menuArray = resources.getStringArray(R.array.new_ui_main_menus)
//        for (i in menuArray.indices) {
//            val menuModel = MenuPopupBean()
//            menuModel.option = i
//            menuModel.name = menuArray[i]
//            menuList.add(menuModel)
//        }
//        return menuList
//    }

    private fun notifyExist() {
        Constants.sendRefreshCmd()
    }

    override fun onDestroy() {
        iMessageDialog.dismiss()
        mEditDialog.dismiss()
        mAccountTypeDialog.dismiss()
        super.onDestroy()
    }

}
