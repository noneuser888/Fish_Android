package com.wantime.wbangapp.ui.fragment

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseFragment
import com.wantime.wbangapp.databinding.FragmentRecylerBinding
import com.wantime.wbangapp.model.NewsBean
import com.wantime.wbangapp.ui.activity.ArticleListActivity
import com.wantime.wbangapp.ui.activity.IWebActivity
import com.wantime.wbangapp.ui.adapter.AuthorityAdapterRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.viewmodel.AuthorityViewModel
import kotlinx.android.synthetic.main.fragment_authority.*
import kotlinx.android.synthetic.main.ui_top_navigation.*
import org.json.JSONObject

class ArticleListFragment : BaseFragment<AuthorityViewModel, FragmentRecylerBinding>() {

    private var mAdapter: AuthorityAdapterRecycler? = null

    companion object {
        private var mArticleListFragment: ArticleListFragment? = null
        fun newInstance(): ArticleListFragment {
            if (mArticleListFragment == null) {
                mArticleListFragment = ArticleListFragment()
            }
            return mArticleListFragment!!
        }
    }


    override val layoutResource: Int
        get() = R.layout.fragment_recyler

    override fun afterInitView() {
        navBack.visibility = View.VISIBLE
        navBack.setOnClickListener { activity!!.finish() }
        navTitle.text = getString(R.string.todayTitle)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        //添加自定义分割线
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.custom_divider)!!)
        mRecyclerView.addItemDecoration(divider)
        //设置适配器
        //设置适配器
        mAdapter = AuthorityAdapterRecycler(activity!!, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                this@ArticleListFragment.onItemClick(itemView, iBean)
            }
        }, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {

            }
        })
        mAdapter?.isShowHeadView(false)
        mRecyclerView.adapter = mAdapter
        onInitRefresh()
    }


    private fun onInitRefresh() {
//        refreshLayout.setOnRefreshListener {
//            mViewModel?.onRefreshNews()
//                ?.observe(this, Observer { onRefreshRecycler(it) })
//        }
//        refreshLayout.setOnLoadMoreListener {
//            mViewModel?.onLoadMore()
//                ?.observe(this, Observer { onLoadMoreRecycler(it) })
//        }
    }

    override fun onProcessLogic() {
        mViewModel?.onRefreshNews()
            ?.observe(this, Observer { onRefreshRecycler(it) })
    }

    private fun onRefreshRecycler(itemBean: NewsBean) {
//        mRecyclerView.post {
//            mAdapter?.onClear()
//            if (itemBean.list != null) {
//                mAdapter?.pushDataList(itemBean.list as ArrayList<NewsBean.ArticleBean>)
//                mAdapter?.notifyDataSetChanged()
//            }
//        }
    }


    private fun onLoadMoreRecycler(itemBean: NewsBean) {
//        mRecyclerView.post {
//            if (itemBean.list != null) {
//                mAdapter?.pushDataList(itemBean.list as ArrayList<NewsBean.ArticleBean>)
//                mAdapter?.notifyDataSetChanged()
//            }
////            refreshLayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
//        }
    }


    private fun onItemClick(itemView: View, iBean: Any) {
//        val iNewsBean = iBean as NewsBean.ArticleBean
//        val dataJson = JSONObject()
//        dataJson.put("webUrl", iNewsBean.contentUrl)
//        dataJson.put("title", iNewsBean.tittle)
//        val mIntent = Intent(activity, IWebActivity::class.java)
//        mIntent.putExtra("data", dataJson.toString())
//        startActivity(mIntent)
    }
}