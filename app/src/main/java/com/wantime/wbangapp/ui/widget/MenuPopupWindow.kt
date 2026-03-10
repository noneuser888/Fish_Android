package com.wantime.wbangapp.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.wantime.wbangapp.R
import com.wantime.wbangapp.model.MenuPopupBean
import com.wantime.wbangapp.ui.adapter.MenuPopupRecycler
import com.wantime.wbangapp.ui.adapter.RecyclerItemClick
import com.wantime.wbangapp.utils.Constants
import kotlin.properties.Delegates

class MenuPopupWindow(val context: Context) : PopupWindow(context, null, -1) {

    private var mAdapter: MenuPopupRecycler by Delegates.notNull()
    private var mRecyclerItemClick: RecyclerItemClick by Delegates.notNull()

    init {
        initPopupWindow(context)
    }


    private fun initPopupWindow(context: Context) {
        val mLayout = LayoutInflater.from(context).inflate(R.layout.new_ui_menu_popup_window, null)
        this.contentView = mLayout
        val mRecyclerView = mLayout.findViewById<RecyclerView>(R.id.mRecyclerView)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.custom_divider)!!)
        isOutsideTouchable = false
        isFocusable = true
        isTouchable = true
        mRecyclerView.addItemDecoration(divider)
        mAdapter = MenuPopupRecycler(context, object : RecyclerItemClick {
            override fun onItemClick(itemView: View, iBean: Any) {
                mRecyclerItemClick.onItemClick(itemView, iBean)
            }
        })
        mRecyclerView.adapter = mAdapter
        width = Constants.dip2px(context, 120f)
        height = Constants.dip2px(context, 120f)
    }

    fun onChangeMenuType(menuType: MenuPopupBean.MenuType): MenuPopupWindow {
        val menuList = ArrayList<MenuPopupBean>()
        var menuArray: Array<String> by Delegates.notNull()
        menuArray = when (menuType) {
            MenuPopupBean.MenuType.MAIN_AllUser -> context.resources.getStringArray(R.array.new_ui_main_menus)
            MenuPopupBean.MenuType.Agent_PrimaryAgent -> context.resources.getStringArray(R.array.new_ui_main_menus_agent)
            MenuPopupBean.MenuType.User_PrimaryAgent -> context.resources.getStringArray(R.array.new_ui_main_menus_user)
            MenuPopupBean.MenuType.User_SecondaryAgent -> context.resources.getStringArray(R.array.new_ui_main_menus_sec_user)
            else -> emptyArray()
        }
        val mHeightNumber=if(menuArray.size in 1..1) 2 else  menuArray.size
        height = Constants.dip2px(context, (40 * mHeightNumber).toFloat())

        for (i in menuArray.indices) {
            val menuModel = MenuPopupBean()
            menuModel.option = i
            menuModel.name = menuArray[i]
            menuModel.type = menuType
            menuList.add(menuModel)
        }
        onRefreshMenus(menuList)
        return this
    }

    private fun onRefreshMenus(dataList: ArrayList<MenuPopupBean>) {
        mAdapter.onClear()
        mAdapter.pushDataList(dataList)
        mAdapter.notifyDataSetChanged()
    }

    fun addRecyclerItemClick(mRecyclerItemClick: RecyclerItemClick) {
        this.mRecyclerItemClick = mRecyclerItemClick
    }


}