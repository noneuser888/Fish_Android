package com.wantime.wbangapp.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.block.dog.common.util.ToastUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentAppealBinding
import com.wantime.wbangapp.inter.IObserver
import com.wantime.wbangapp.model.bind.IPepresentBean
import com.wantime.wbangapp.request.RetrofitManager
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.utils.NetCode
import com.wantime.wbangapp.utils.ViewType
import com.wantime.wbangapp.viewmodel.AppealViewModel
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.utils.ContentUriUtils
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_appeal.*
import kotlinx.android.synthetic.main.ui_top_navigation.*
import kotlin.collections.ArrayList

//申诉提交
class AppealFragment : BaseFragment<AppealViewModel, FragmentAppealBinding>() {

    private var photoPaths = ArrayList<Uri>()
    private var photoSelectPaths = ArrayList<Uri>()
    private val maxCount = 3


    companion object {
        private var mAppealFragment: AppealFragment? = null
        fun newInstance(): AppealFragment {
            if (mAppealFragment == null) {
                mAppealFragment = AppealFragment()
            }
            return mAppealFragment!!
        }
    }


    override val layoutResource: Int
        get() = R.layout.fragment_appeal

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navTitle.text = getString(R.string.ui_shensu)
        navBack.setOnClickListener { activity!!.finish() }
        val iItemBean = IPepresentBean()
        if (mParamJson != null) {
            iItemBean.authOrderId.set(mParamJson!!.optString("taskId"))
        }
        binding?.iItemBean = iItemBean
        addButton.setOnClickListener {
            if (photoSelectPaths.size < maxCount)
                Constants.openMoreFilePicker(this, photoPaths, maxCount)
        }
        submitButton.setOnClickListener {
            if (TextUtils.isEmpty(iItemBean.description.get()!!) || iItemBean.description.get()!!.length < 10) {
                ToastUtil.show(activity!!, getString(R.string.ui_appeal_reason_tips))
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(iItemBean.authOrderId.get()!!)) {
                ToastUtil.show(activity!!, getString(R.string.ui_appeal_task_tips))
                return@setOnClickListener
            }
            uploadPics(
                iItemBean.authOrderId.get()!!,
                iItemBean.description.get()!!,
                "1",
                photoSelectPaths
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && Constants.CUSTOM_REQUEST_CODE == requestCode) {
            if (data != null) {
                val dataList =
                    data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    photoSelectPaths.addAll(dataList)
                    onRefreshPicture()
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onProcessLogic() {


    }

    private fun onRefreshPicture() {
        activity!!.runOnUiThread {
            for (k in 0 until appealImgs.childCount - 1) {
                appealImgs.removeViewAt(k)
            }
            val viewWidth = Constants.dip2px(activity!!, 70f)
            val margin = Constants.dip2px(activity!!, 10f)
            val layoutParam = LinearLayout.LayoutParams(viewWidth, viewWidth)
            layoutParam.rightMargin = margin

            for (imagePath in photoSelectPaths) {
                val imageView =
                    layoutInflater.inflate(R.layout.ui_appeal_form_post_imgs, null) as ImageView
                imageView.layoutParams = layoutParam
                appealImgs.addView(imageView, 0)
                Glide.with(activity!!).load(imagePath)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .override(viewWidth, viewWidth)
                            .placeholder(droidninja.filepicker.R.drawable.image_placeholder)
                    )
                    .thumbnail(0.5f)
                    .into(imageView)
            }
        }
    }



    //上传图片优先
    private fun uploadPics(
        taskID: String,
        taskReason: String,
        taskType: String,
        photoSelectPaths: ArrayList<Uri>
    ) {
        showProgress()
        mViewModel?.onAppealFormPost(activity!!, taskID, taskReason, taskType, photoSelectPaths)
            ?.observe(this,
                Observer {
                    hiddenProgress()
                    ToastUtil.show(activity!!, it.message)
                    if (it.ok == NetCode.NET_SUCCESS) activity!!.finish()

                })
    }
}
