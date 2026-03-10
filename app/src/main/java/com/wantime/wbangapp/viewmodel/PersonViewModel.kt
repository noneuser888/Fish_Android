package com.wantime.wbangapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.wantime.wbangapp.R
import com.wantime.wbangapp.common.BaseViewModel
import com.wantime.wbangapp.model.PersonalBean

class PersonViewModel(application: Application) : BaseViewModel(application) {

    fun onGetPersonOptions() : MutableLiveData<List<PersonalBean>> {
        val liveData: MutableLiveData<List<PersonalBean>> = MutableLiveData<List<PersonalBean>>()
        val dataList=ArrayList<PersonalBean>()
        dataList.add(birthItem(0,"授权记录列表", R.mipmap.my_ic_authorize,PersonalBean.Options.authorityList.ordinal))
        dataList.add(birthItem(0,"我的申述", R.mipmap.my_ic_appeal,PersonalBean.Options.myShenSu.ordinal))
        dataList.add(birthItem(1,"", -1,-1))//空行
        dataList.add(birthItem(0,"退出登录", R.mipmap.my_ic_signout,PersonalBean.Options.existLogin.ordinal))
        liveData.postValue(dataList)
        return liveData
    }


    private fun birthItem(type:Int,title:String ,icon:Int,iOption :Int):PersonalBean{
        val  iBean=PersonalBean()
        iBean.type=type
        iBean.title=title
        iBean.icon=icon
        iBean.iOption=iOption

        return iBean
    }
}