package com.wantime.wbangapp.app

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import com.wantime.wbangapp.database.SQLHelper



class WBangApp : MultiDexApplication() {

    private val buglyAppID = "d67fb5e8c7"
    private val TAG="WBangApp"

    companion object {
        private var mWBangApp: WBangApp? = null
        fun getInstance(): WBangApp? {

            return mWBangApp
        }
    }

    override fun onCreate() {
        mWBangApp = this
        SQLHelper.onInitHelper(this)
        ZXingLibrary.initDisplayOpinion(this)
//        Bugly.init(applicationContext, buglyAppID, true)
//        initUpgrade()
        super.onCreate()
    }

    override fun attachBaseContext(base: Context?) {
        MultiDex.install(this)
        super.attachBaseContext(base)
    }

//
//    private fun initUpgrade() {
//        Beta.autoInit = true
//        Beta.autoCheckUpgrade = true
//        Beta.initDelay = 500 //500 hm检测
//        Beta.largeIconId = R.mipmap.ic_launcher
//        Beta.defaultBannerId = R.mipmap.ic_launcher
//        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        Beta.showInterruptedStrategy = true
//        Beta.canShowUpgradeActs.add(MainActivity::class.java)
//        Beta.upgradeListener = UpgradeListener { var1, strategy, var2, var3 ->
//            Log.e("var1","$var1,$var2,var3")
//            Log.e("UpgradeListener",">>>>>>>>>>>>>>>>>UpgradeListener")
//            if (strategy != null) {
//                val mIntent = Intent()
//                mIntent.setClass(applicationContext, UpgradeActivity::class.java)
//                mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(mIntent)
//            }
//            else ToastUtil.show(applicationContext, "没有更新！")
//        }
//        //监听APP升级状态
//        Beta.upgradeStateListener = object : UpgradeStateListener {
//            override fun onUpgradeFailed(b: Boolean) {
//                Log.d(TAG, "upgradeStateListener upgrade fail")
//            }
//
//            override fun onUpgradeSuccess(b: Boolean) {
//                Log.d(TAG, "upgradeStateListener upgrade success")
//            }
//
//            override fun onUpgradeNoVersion(b: Boolean) {
//                Log.d(TAG, "upgradeStateListener upgrade has no new version")
//            }
//
//            override fun onUpgrading(b: Boolean) {
//                Log.d(TAG, "upgradeStateListener upgrading")
//            }
//
//            override fun onDownloadCompleted(b: Boolean) {
//                Log.d(TAG, "upgradeStateListener download apk file success")
//            }
//        }
//
//        Bugly.init(applicationContext, buglyAppID, true)
//    }
}