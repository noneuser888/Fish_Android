package com.wantime.wbangapp.ui.fragment

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentPersonBinding
import com.wantime.wbangapp.model.PersonalBean
import com.wantime.wbangapp.ui.activity.AppealRecordActivity
import com.wantime.wbangapp.ui.activity.ITaskActivity
import com.wantime.wbangapp.ui.activity.RecordActivity
import com.wantime.wbangapp.ui.adapter.PersonAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.ui.dialog.EditDialog
import com.wantime.wbangapp.ui.dialog.MessageDialog
import com.wantime.wbangapp.ui.event.MessageEvent
import com.wantime.wbangapp.utils.Constants
import com.wantime.wbangapp.viewmodel.PersonViewModel
import kotlinx.android.synthetic.main.fragment_person.mRecyclerView
import kotlinx.android.synthetic.main.fragment_person.mToolbar
import org.greenrobot.eventbus.EventBus

class PersonFragment : BaseFragment<PersonViewModel, FragmentPersonBinding>() {

    private var mAdapter: PersonAdapterRecycler? = null
    private var existDialog: MessageDialog? = null
    private var editDialog: EditDialog? = null

    companion object {
        private var mPersonFragment: PersonFragment? = null
        fun newInstance(): PersonFragment {
            if (mPersonFragment == null) {
                synchronized(AuthorityFragment::class.java) {
                    mPersonFragment =
                        PersonFragment()
                }
            }
            return mPersonFragment!!
        }
    }

    override val layoutResource: Int
        get() = R.layout.fragment_person


    override fun afterInitView() {
        initToolbar()
        changeTitle()
        initMessageDialog()
        initRecyclerView()
    }

    override fun onResume() {
        initBindingData()
        super.onResume()
    }

    private fun initBindingData() {
        binding?.iUserBean = Constants.onGetUserBaseInfoWithNoToken()
    }


    private fun initRecyclerView() {
        mAdapter = PersonAdapterRecycler(activity!!.applicationContext, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@PersonFragment.onItemClick(itemView, iBean)
            }
        })
        mRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        mRecyclerView.adapter = mAdapter
    }


    private fun initMessageDialog() {
        existDialog = MessageDialog(activity!!)
        existDialog!!.setTitle(getString(R.string.ui_exist_login_title))
            .setContent(getString(R.string.ui_exist_login_title_desc))

        editDialog = EditDialog(activity!!)
        editDialog!!.setTitle(getString(R.string.ui_my_email))
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)
        setHasOptionsMenu(true)
    }


    private fun changeTitle() {
        val mTitle = mToolbar.getChildAt(0) as TextView
        mTitle.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;//填充父类
        mTitle.gravity = Gravity.CENTER_HORIZONTAL;//水平居中，CENTER，即水平也垂直，自选
        mTitle.text = resources.getStringArray(R.array.app_nav)[2]
    }


    override fun onRefresh() {
        onProcessLogic()
    }

    //https://blog.csdn.net/qq_33210042/article/details/103472388
    //处理数据
    override fun onProcessLogic() {
        if (mViewModel != null)
            mViewModel?.onGetPersonOptions()!!.observe(this, Observer {
                mRecyclerView.post {
                    mAdapter?.onClear()
                    mAdapter?.pushData(it as ArrayList<PersonalBean>)
                    mAdapter?.notifyDataSetChanged()
                    isFinishRefresh = true
                }
            })
    }

    private fun onItemClick(itemView: View, iBean: Any) {
        if (Constants.isLogin(activity!!)) {
            val iDataBean = iBean as PersonalBean
            when (iDataBean.iOption) {
                //授权记录
                PersonalBean.Options.authorityList.ordinal -> startActivity(
                    Intent(activity!!, RecordActivity::class.java)
                )
                //我的任务
                PersonalBean.Options.myTask.ordinal -> startActivity(
                    Intent(
                        activity!!,
                        ITaskActivity::class.java
                    )
                )
                //我的申述
                PersonalBean.Options.myShenSu.ordinal -> startActivity(
                    Intent(
                        activity!!,
                        AppealRecordActivity::class.java
                    )
                )

                PersonalBean.Options.existLogin.ordinal -> {
                    existDialog!!.addConfirmListener(View.OnClickListener {
                        notifyExist()
                        Constants.clearLogin(activity!!)
                        notifyExist()
                        EventBus.getDefault().post(MessageEvent())
                        existDialog!!.dismiss()
                    }).show()
                }

            }
        }
    }

    override fun onDestroy() {
        existDialog?.dismiss()
        editDialog?.dismiss()
        super.onDestroy()
    }

    private fun notifyExist() {
        Constants.sendRefreshCmd()
    }
}