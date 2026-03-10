package com.wantime.wbangapp.ui.activity

import androidx.fragment.app.Fragment
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseActivity
import com.wantime.wbangapp.ui.fragment.ArticleListFragment

class ArticleListActivity :BaseActivity(){
    private var mArticleListFragment: ArticleListFragment?=null

    override val layoutId: Int
        get() = R.layout.activity_fragment

    override fun afterInitView() {
        initFragment()
    }

    private fun initFragment(){
        mArticleListFragment= ArticleListFragment.newInstance()
        initFragment(mArticleListFragment as Fragment, R.id.frameLayout)
    }

}