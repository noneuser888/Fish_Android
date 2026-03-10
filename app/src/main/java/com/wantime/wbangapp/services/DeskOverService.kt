package com.wantime.wbangapp.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.tencent.mm.plugin.view.IWxPluginView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.app.WBangApp
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.ui.widget.LoadingView
import com.wantime.wbangapp.utils.Constants
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.util.ArrayList

/**
 *  on 2019/6/19.
 */
class DeskOverService : Service() {


    private var isAdded = false // 是否已增加悬浮窗
    private var wm: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var float_show: ConstraintLayout? = null
//    private var leftView: ImageView? = null
//    private var rightView: ImageView? = null
//    private var centerView: ImageView? = null

    private var homeList: List<String>? = null // 桌面应用程序包名列表
    private var mActivityManager: ActivityManager? = null
    private var screen_width: Int = 0
    private var screen_height: Int = 0
    private var default_right: Int = 0
    private var default_left: Int = 0
    private var show_anim: Animation? = null
    private var hide_anim: Animation? = null

    // 是否有移动事件
    private var has_move = false
    private var mPlugin: IWxPluginView? = null
    protected var mParamJson: JSONObject? = null


    private var wxWebview: WebView? = null
    private var navTitle: TextView? = null
    private var navRightIcon: TextView? = null
    private var navRightIcon1: TextView? = null
    private var showQrText: TextView? = null
    private var wxZsText: TextView? = null
    private var wxKeyText: TextView? = null
    private var iWebCover: View? = null
    private var zsLayout: ConstraintLayout? = null
    private var mXLoadingView: LoadingView? = null
    private var wxVerifiText: TextView? = null
    private var navBack: TextView? = null
    private var uiWxOnLine: TextView? = null


    companion object {
        val OPERATION = "operation"
        val OPERATION_SHOW = 100
        val OPERATION_HIDE = 101

        private val HANDLE_CHECK_ACTIVITY = 200
        private val HANDLE_HIDE_FLOAT = 201
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm!!.defaultDisplay.getMetrics(outMetrics)
        screen_width = outMetrics.widthPixels
        screen_height = outMetrics.heightPixels
        homeList = getHomes()
        show_anim = AnimationUtils.loadAnimation(this@DeskOverService, R.anim.show_anim)
        hide_anim = AnimationUtils.loadAnimation(this@DeskOverService, R.anim.hide_anim)
        createFloatView()
        onCreatePlugin()
    }



    private fun onCreatePlugin() {
        if (float_show != null) {
            wxWebview = float_show!!.findViewById(R.id.wxWebview)
            navBack = float_show!!.findViewById(R.id.navBack)
            navTitle = float_show!!.findViewById(R.id.navTitle)
            navRightIcon = float_show!!.findViewById(R.id.navRightIcon)
            navRightIcon1 = float_show!!.findViewById(R.id.navRightIcon1)
            showQrText = float_show!!.findViewById(R.id.showQrText)
            wxZsText = float_show!!.findViewById(R.id.wxZsText)
            wxKeyText = float_show!!.findViewById(R.id.wxKeyText)
            iWebCover = float_show!!.findViewById(R.id.iWebCover)
            zsLayout = float_show!!.findViewById(R.id.zsLayout)
            mXLoadingView = float_show!!.findViewById(R.id.mXLoadingView)
            wxVerifiText = float_show!!.findViewById(R.id.wxVerifiText)
            uiWxOnLine = float_show!!.findViewById(R.id.uiWxOnLine)

//            mPlugin = IWxPluginView(
//                this,
//                application,
//                null,
//                if (mParamJson == null) JSONObject() else mParamJson!!,
//                wxWebview!!,
//                navBack!!,
//                navTitle!!,
//                navRightIcon!!,
//                navRightIcon1!!,
//                showQrText!!,
//                wxZsText!!,
//                wxKeyText!!,
//                iWebCover!!,
//                zsLayout!!,
//                mXLoadingView!!,
//                wxVerifiText!!,
//                uiWxOnLine!!,
//                null
//            )
//            mPlugin?.initView()
        }
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)

        var operation = OPERATION_SHOW
        if (intent != null)
            operation = if (intent.hasExtra(OPERATION))
                intent.getIntExtra(OPERATION, OPERATION_SHOW)
            else
                OPERATION_SHOW
        when (operation) {
            OPERATION_SHOW -> {
                mHandler.removeMessages(HANDLE_CHECK_ACTIVITY)
                mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY)
            }
            OPERATION_HIDE -> {
                mHandler.removeMessages(HANDLE_CHECK_ACTIVITY)
                mHandler.sendEmptyMessage(HANDLE_HIDE_FLOAT)
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == HANDLE_CHECK_ACTIVITY) {

            }
            when (msg.what) {
                HANDLE_CHECK_ACTIVITY -> {
                    if (isHome()) {
                        if (!isAdded) {
                            wm!!.addView(float_show, params)
                            float_show!!.startAnimation(show_anim)
                            isAdded = true
                        } else {
                            float_show!!.startAnimation(show_anim)
                        }
                    } else {
                        if (isAdded) {
                            wm!!.removeView(float_show)
                            isAdded = false
                        }
                    }
                    this.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000)
                }
                HANDLE_HIDE_FLOAT -> if (isAdded) {
                    wm!!.removeView(float_show)
                    isAdded = false
                }
            }
        }
    }

    // // 设置悬浮窗的长宽
    fun updateDefaultLeftRight() {
        default_right = (screen_width - params!!.width) / 2
        default_left = -1 * default_right
    }

    /**
     * 创建悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun createFloatView() {

        float_show = LayoutInflater.from(applicationContext)
            .inflate(R.layout.service_desk_overflow, null) as ConstraintLayout
        float_show!!.setOnClickListener {
            // TODO Auto-generated method stub
            if (!has_move) {
                Toast.makeText(this@DeskOverService, "单击事件", Toast.LENGTH_LONG).show()
            }
        }
//        leftView = float_show!!.findViewById<View>(R.id.left_view) as ImageView
//        rightView = float_show!!.findViewById<View>(R.id.right_view) as ImageView
//        centerView = float_show!!.findViewById<View>(R.id.center_view) as ImageView

        wm = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams()
        params?.width=screen_width
        params?.height=Constants.dip2px(this,240f)

        // 设置window type
        //		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            }
        } else {
            params!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        params!!.format = PixelFormat.RGBA_8888 // 设置图片格式，效果为背景透明

        // 设置Window flag
        params!!.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

        updateDefaultLeftRight()
        params!!.x = default_left
//        showLeftView()
        // 设置悬浮窗的Touch监听
        float_show!!.setOnTouchListener(object : View.OnTouchListener {
            var lastX: Int = 0
            var lastY: Int = 0
            var paramX: Int = 0
            var paramY: Int = 0
            var dx: Int = 0
            var dy: Int = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        paramX = params!!.x
                        paramY = params!!.y
                        has_move = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        dx = event.rawX.toInt() - lastX
                        dy = event.rawY.toInt() - lastY
                        params!!.x = paramX + dx
                        params!!.y = paramY + dy
//                        showCenterView()
                        has_move = true
                        // 更新悬浮窗位置
                        wm!!.updateViewLayout(float_show, params)
                    }
                    MotionEvent.ACTION_UP -> if (has_move) {
                        // 如果发生了移动操作,才进行松开的处理,避免和单击事件冲突
//                        val dx1 = event.rawX.toInt()
//                        if (dx1 > 240) {
//                            showRightView()
//                            updateDefaultLeftRight()
//                            params!!.x = default_right
//                        } else {
//                            showLeftView()
//                            updateDefaultLeftRight()
//                            params!!.x = default_left
//                        }
//                        params!!.y = paramY + dy
//                        wm!!.updateViewLayout(float_show, params)
//
//                        if (event.rawY > screen_height - 100)
//                            Toast.makeText(this@DeskOverService, "开始清理...", Toast.LENGTH_LONG)
//                                .show()
                    }
                }
                return false
            }
        })

        wm!!.addView(float_show, params)
        isAdded = true
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private fun getHomes(): List<String> {
        val names = ArrayList<String>()
        val packageManager = this.packageManager
        // 属性
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.queryIntentActivities(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        )
        for (ri in resolveInfo) {
            names.add(ri.activityInfo.packageName)
        }
        return names
    }

    /**
     * 判断当前界面是否是桌面
     */
    fun isHome(): Boolean {
        if (mActivityManager == null) {
            mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        }
        val rti = mActivityManager!!.getRunningTasks(1)
        return homeList!!.contains(rti[0].topActivity?.packageName)
    }

    // 显示桌面左边的图标
    private fun showLeftView() {
        params!!.width = 42
        params!!.height = 67
//        leftView!!.visibility = View.VISIBLE
//        rightView!!.visibility = View.GONE
//        centerView!!.visibility = View.GONE
    }

    // 显示桌面右边的图标
    private fun showRightView() {
        params!!.width = 42
        params!!.height = 67
//        leftView!!.visibility = View.GONE
//        rightView!!.visibility = View.VISIBLE
//        centerView!!.visibility = View.GONE
    }

    // 显示移动时的图标
    fun showCenterView() {
        params!!.width = 103
        params!!.height = 145
//        leftView!!.visibility = View.GONE
//        rightView!!.visibility = View.GONE
//        centerView!!.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        mPlugin?.onMessageEvent(event)
    }

    override fun onDestroy() {
        mPlugin?.showProgress()
        super.onDestroy()
    }
}