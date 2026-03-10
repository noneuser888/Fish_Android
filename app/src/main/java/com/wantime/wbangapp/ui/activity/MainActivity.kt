package com.wantime.wbangapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.open.hule.library.entity.AppUpdate
import com.open.hule.library.utils.UpdateManager
import com.wantime.wbangapp.BuildConfig
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.database.SQLHelper
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.inter.ITabLayoutListener
import com.wantime.wbangapp.model.NullBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.services.DeskOverService
import com.wantime.wbangapp.ui.dialog.AdMessageDialog
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.fragment.AuthorityFragment
import com.wantime.wbangapp.ui.fragment.PersonFragment
import com.wantime.wbangapp.ui.fragment.PlatformFragment
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.LoginViewModel
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import kotlin.properties.Delegates

class MainActivity : BaseActivity() {

//    private var mTabsTitle: Array<String>? = null
//    private var currentTab: TabLayout.Tab? = null

    private var mAuthorityFragment: AuthorityFragment? = null

    //    private var mPlatformFragment: PlatformFragment? = null
//    private var mPersonFragment: PersonFragment? = null
    private var mAdMessageDialog: AdMessageDialog? = null
    private var messageDialog: MessageDialog by Delegates.notNull()

    //登录动作
    private var mLoginModel: LoginViewModel? = null
    private var mPosition: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onStartAutoLogin()
        EventBus.getDefault().register(this)
    }


    override val layoutId: Int
        get() = R.layout.activity_main

    override fun afterInitView() {
        mLoginModel = LoginViewModel(application)
        requestPermission()
        initFragment()
        initAdAlert()
    }

//    private fun initNavTab() {
//        mTabsTitle = resources.getStringArray(R.array.app_nav)
//        for (i in mTabsTitle!!.indices) {
//            currentTab = mBottomTabLayout.getTabAt(i)
//            if (currentTab == null) {
//                currentTab = mBottomTabLayout.newTab()
//                mBottomTabLayout.addTab(currentTab!!)
//            }
//            currentTab?.customView = getTabView(i)
//        }
//        mBottomTabLayout.selectTab(mBottomTabLayout.getTabAt(1))
//        mBottomTabLayout.addOnTabSelectedListener(object : ITabLayoutListener() {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                updateFragmentData(tab!!.position);
//            }
//        })
//
//    }

    private fun initAdAlert() {
        mAdMessageDialog = AdMessageDialog(this)
//        startOverWindow()
    }

    private fun initFragment() {
        mAuthorityFragment = AuthorityFragment()
        initFragment(mAuthorityFragment as Fragment, R.id.frameLayout)
        messageDialog = MessageDialog(this).setTitle(getString(R.string.ui_system_title))
            .setContent(
                getString(R.string.new_ui_login_exception)
            )
//        mPersonFragment = PersonFragment.newInstance()
//        mPlatformFragment = PlatformFragment.newInstance()
//        frameLayout.postDelayed({
//            initFragment(mAuthorityFragment as Fragment, R.id.frameLayout)
//        }, 1000)

    }


//    /**
//     * 刷新fragment
//     *
//     * @param position
//     */
//    fun updateFragmentData(position: Int) {
//        when (position) {
//            0 -> {
//                changeFragment(mAuthorityFragment as Fragment)
//            }
//            1 -> {
//                changeFragment(mPlatformFragment as Fragment)
//            }
//            2 -> {
//                if (Constants.isLogin(this)) {
//                    changeFragment(mPersonFragment as Fragment)
//                } else {
//                    mBottomTabLayout.selectTab(mBottomTabLayout.getTabAt(mPosition))
//                    return
//                }
//            }
//        }
//        mPosition = position
//    }

//
//    private fun getTabView(position: Int): View {
//        val view: View =
//            LayoutInflater.from(this@MainActivity).inflate(R.layout.ui_main_bottom_nav_item, null)
//        val title: TextView = view.findViewById(R.id.mNavText)
//        when (position) {
//            0 -> view.findViewById<ImageView>(R.id.navIcon)
//                .setImageResource(R.drawable.ui_bottom_menu_authority_drawable)
//            1 -> view.findViewById<ImageView>(R.id.navIcon)
//                .setImageResource(R.drawable.ui_bottom_menu_platform_drawable)
//            2 -> view.findViewById<ImageView>(R.id.navIcon)
//                .setImageResource(R.drawable.ui_bottom_menu_personal_drawable)
//        }
//        title.text = mTabsTitle!![position]
//        return view
//    }


    override fun onDestroy() {
        SQLHelper.onDestroy()
        EventBus.getDefault().unregister(this)
        mAdMessageDialog?.dismiss()
        messageDialog.dismiss()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.mType) {
            MessageEvent.EventType.refreshFragment.ordinal -> {
                mAuthorityFragment?.onRefresh()
            }
            MessageEvent.EventType.requestAgainBaseInfo.ordinal -> {
                onGetUserBaseInfo()
            }
            MessageEvent.EventType.loginException.ordinal -> {
                Constants.onAutoLogin(this)
//                Constants.clearLogin()
//                messageDialog.setCancelable(false)
//                messageDialog.addCancelListener(View.OnClickListener {
//                    Constants.isLogin(this@MainActivity)
//                    messageDialog.dismiss()
//                }).addConfirmListener(View.OnClickListener {
//                    Constants.isLogin(this@MainActivity)
//                    messageDialog.dismiss()
//                }).show()
            }
            MessageEvent.EventType.closeMain.ordinal->{
               //moveTaskToBack(true)
               finish()
            }
        }

    }

    //启动自动登录到系统中
    private fun onStartAutoLogin() {
        Constants.onGetUserInfo(this)
        onGetAppOtherInfo()
    }

    //获取用户基本信息
    private fun onGetUserBaseInfo() {
        RetrofitManager.getInstance().apiRequest.onUserInfo(NullBean()).subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    Constants.onGetInfo(t).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                Constants.sendRefreshCmd()
                                Constants.onSaveUserBaseInfoWithNoToken(getJSONObject("data"))
                            }
                            else -> Constants.sendLoginException()
                        }
                    }
                }
            })
    }

    //获取用户的基本新
    private fun onGetAppOtherInfo() {
        onGetUserBaseInfo()
        RetrofitManager.getInstance().apiRequest.onPopups().subscribeOn(Schedulers.io())
            .subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    Constants.onGetInfo(t).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                val dataJson = getJSONObject("data")
                                runOnUiThread {
                                    dataJson.put("webUrl", dataJson.getString("webLink"))
                                    mAdMessageDialog?.setAdPicture(dataJson.getString("imgUrl"))!!
                                        .addDetailListener(View.OnClickListener {
                                            val mIntent =
                                                Intent(
                                                    this@MainActivity,
                                                    IWebActivity::class.java
                                                )
                                            mIntent.putExtra("data", dataJson.toString())
                                            startActivity(mIntent)
                                        }).show()
                                }
                            }
                        }
                    }
                }
            })

        //检查更新
        RetrofitManager.getInstance().apiRequest.checkUpdate(NullBean())
            .subscribeOn(Schedulers.io()).subscribe(object : IObserver<String>() {
                override fun onNext(t: String) {
                    Constants.onGetInfo(t).apply {
                        when (optInt("code")) {
                            Constants.NET_OK -> {
                                updateApp(getJSONObject("data"))
                            }
                        }
                    }
                }
            })

    }


    private fun updateApp(dataJson: JSONObject) {
        runOnUiThread {
            dataJson.apply {
                Log.e("dataJson", dataJson.toString())
                val appUrl = optString("appUrl_android")//非root 下载地址
                val appVersion = optInt("lastVersion_android")//非root
                if (appVersion > BuildConfig.VERSION_CODE) {//网络版本更高
                    val appDesc = optString("versionDesc_android")
                    val appName = optString("versionName_android")
                    val appUpdate = AppUpdate.Builder() //更新地址（必须）
                        .newVersionUrl(appUrl) // 版本号（非必须）
                        .newVersionCode("v $appName") // 文件大小（非必须）
                        .updateInfo(appDesc)
                        .forceUpdate(if ((optString("forceUpdate", "0") != "0")) 1 else 0)
                        .build()
                    UpdateManager().startUpdate(this@MainActivity, appUpdate)
                }
            }
        }
    }
}
