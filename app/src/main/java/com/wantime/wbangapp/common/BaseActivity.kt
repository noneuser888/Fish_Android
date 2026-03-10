package com.wantime.wbangapp.common

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.block.dog.common.util.ToastUtil
import com.wantime.wbangapp.R
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.ViewType
import org.json.JSONObject
import rebus.permissionutils.PermissionEnum
import rebus.permissionutils.PermissionManager


abstract class BaseActivity : AppCompatActivity() {

    private var containerViewId: Int = 0//记录替换ID
    private var fragmentManager: FragmentManager? = null
    private var currentFragment: Fragment? = null
    protected var mParamJson: JSONObject? = null
    protected var viewType = ViewType.MultipleRecord.ordinal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Constants.initUserInfo(this)
        if (intent.hasExtra("data")) {
            mParamJson = JSONObject(intent.getStringExtra("data")!!)
        }
        viewType = intent.getIntExtra("viewType", ViewType.MultipleRecord.ordinal)
        Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.flags
        setContentView(layoutId)
        afterInitView()
    }

    abstract val layoutId: Int
    abstract fun afterInitView()


    // 切換Fragment
    fun changeFragment(f: Fragment) {
        changeFragment(f, containerViewId, null)
    }

    //带参数的。
    fun changeFragment(f: Fragment, mBundle: Bundle) {
        changeFragment(f, containerViewId, mBundle)
    }

    // 初始化Fragment
    fun initFragment(f: Fragment, @IdRes containerViewId1: Int) {
        containerViewId = containerViewId1
        changeFragment(f, containerViewId, null)
    }

    private fun changeFragment(f: Fragment, @IdRes containerViewId: Int, mBundle: Bundle?) {

        if (fragmentManager == null) fragmentManager = supportFragmentManager
        val ft = fragmentManager?.beginTransaction()
        if (mBundle != null) f.arguments = mBundle
        if (currentFragment != null) ft?.hide(currentFragment!!)
        currentFragment = f
        if (!f.isAdded)
            ft?.add(containerViewId, f)
        else ft?.show(f)
        ft?.commitAllowingStateLoss()
    }

    fun requestPermission() {
        val permissionEnumArrayList = ArrayList<PermissionEnum>()
        permissionEnumArrayList.add(PermissionEnum.WRITE_EXTERNAL_STORAGE)
        permissionEnumArrayList.add(PermissionEnum.READ_EXTERNAL_STORAGE)
        permissionEnumArrayList.add(PermissionEnum.ACCESS_FINE_LOCATION)
        permissionEnumArrayList.add(PermissionEnum.READ_PHONE_STATE)
        permissionEnumArrayList.add(PermissionEnum.CAMERA)


        PermissionManager.Builder()
            .permissions(permissionEnumArrayList)
            .askAgain(true)
            .callback { _, permissionsDenied, _, _ ->
                if (permissionsDenied.size > 0) {
                    ToastUtil.show(applicationContext, "该应用正常运行，需要一些基本权限，请开启！")
                    finish()
                }
            }
            .askAgainCallback {}
            .ask(this)
    }


    open fun checkPermissionFinish() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var grantNumber = 0
        for (m in grantResults) {
            if (m == 0) grantNumber++
        }
        if (grantNumber >= permissions.size) checkPermissionFinish()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}