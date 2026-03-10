package com.wantime.wbangapp.common

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wantime.wbangapp.ui.dialog.IProgressLoading
import com.wantime.wbangapp.utils.ViewType
import org.json.JSONObject
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


//https://juejin.im/post/5da6c0acf265da5bbb1e4df7

abstract class BaseFragment<VM : BaseViewModel, VDB : ViewDataBinding> : Fragment() {
    protected var mViewModel: VM? = null
    protected var binding: VDB? = null

    private var rootView: View? = null
    protected var mActivity: Activity? = null
    protected var mParamJson: JSONObject? = null
    private var mProgressDialog: IProgressLoading? = null
    protected var isFinishRefresh = false
    private var viewType = ViewType.MultipleRecord.ordinal

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            createViewModel()
            binding = DataBindingUtil.inflate(inflater, layoutResource, container, false)
            binding!!.lifecycleOwner = this
            rootView = binding!!.root
            initProgress()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.mActivity = activity
        afterInitView()
        onProcessLogic()
    }

    open fun setViewType(viewType: Int) {
        this.viewType = viewType
    }

    open fun setParams(mParamJson: JSONObject) {
        this.mParamJson = mParamJson
    }

    //获取布局文件
    protected abstract val layoutResource: Int

    //初始化view
    protected abstract fun afterInitView()

    //执行请求
    protected abstract fun onProcessLogic()

    open fun onRefresh() {}


    override fun onDestroyView() {
        hiddenProgress()
        super.onDestroyView()
        rootView = null
    }

    open fun createViewModel() {
        if (mViewModel == null) {
            val modelClass: Class<ViewModel>
            val type: Type? = javaClass.genericSuperclass
            modelClass = (if (type is ParameterizedType) {
                (type as ParameterizedType?)!!.actualTypeArguments[0]
            } else {   //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java
            }) as Class<ViewModel>
            mViewModel = ViewModelProvider.AndroidViewModelFactory(activity!!.application)
                .create(modelClass) as VM
        }
    }

    private fun initProgress() {
        mProgressDialog = IProgressLoading(activity!!)
    }

    fun showProgress() {
        mProgressDialog?.run { mProgressDialog?.show() }
    }

    fun hiddenProgress() {
        mProgressDialog?.run { mProgressDialog?.dismiss() }
    }

    fun setNavigationDrawableForLeft(navText:TextView,resID:Int ){
        val drawableLeft = resources.getDrawable(resID)
        navText.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null)
    }

    fun setNavigationDrawableForTop(navText:TextView,resID:Int ){
        val drawableTop = resources.getDrawable(resID)
        navText.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
    }

    open fun closeSoftKeybord(mEditText: EditText, mContext: Context) {
        val imm =
            mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }
}