package com.wantime.wbangapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pools
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.PagerAdapter
import com.wantime.wbangapp.R
import com.wantime.wbangapp.databinding.UiAdapterAdvoicePagerItemBinding
import com.wantime.wbangapp.model.AdItemBean

//自己造一个池，可以提高加载效率，与复用率，
class AdvoicePagerAdapter<D>(_mItemClick: RecyclerItemClick) : PagerAdapter() {

    private val pool: Pools.Pool<View> = Pools.SimplePool(4)
    private val mItemClick: RecyclerItemClick = _mItemClick
    private val dataList = ArrayList<AdItemBean.ImgListBean>()

    fun pushDataList(dataList: ArrayList<AdItemBean.ImgListBean>) {
        this.dataList.addAll(dataList)
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = pool.acquire()
        if (view == null) {
            view = DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(container.context),
                R.layout.ui_adapter_advoice_pager_item,
                container,
                false
            ).root
        }
        val binding =
            DataBindingUtil.bind<ViewDataBinding>(view) as UiAdapterAdvoicePagerItemBinding
        binding.iPagerItem = dataList[position]
        binding.xItemClick = mItemClick
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
        pool.release(view)
    }

}