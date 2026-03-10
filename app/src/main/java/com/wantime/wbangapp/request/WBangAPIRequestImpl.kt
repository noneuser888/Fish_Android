package com.wantime.wbangapp.request


import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.wantime.wbangapp.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import org.json.JSONObject
import java.util.ArrayList

class WBangAPIRequestImpl private constructor() : WBangAPIRequest {

    fun String.pageSize(page: Int, size: Int): String = also {
        return it.replace("{page}", "$page").replace("{size}", "$size")
    }

    companion object {
        private var mWBangAPIRequest: WBangAPIRequestImpl? = null
        fun getInstance(): WBangAPIRequestImpl {
            synchronized(WBangAPIRequestImpl::class.java) {
                mWBangAPIRequest = WBangAPIRequestImpl()
            }
            return mWBangAPIRequest!!
        }
    }

    override fun onLogin(iBean: UserBean): Observable<String> {

        return Observable.create {
            val mFormJson = JSONObject(JSON.toJSONString(iBean))
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(APIRequestPath.login_url, mFormJson)
            it.onNext(resultJson.toString())
        }
    }

    override fun onAuthHome(agencyId: String?): Observable<String> {
        return Observable.create {
            val tempPath =
                if (!TextUtils.isEmpty(agencyId)) APIRequestPath.authHome_url + "?agencyId=" + agencyId else APIRequestPath.authHome_url
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun uploadPicMore(multipartBodyBuilder: MultipartBody.Builder): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestMultipartSync(APIRequestPath.upload_url, multipartBodyBuilder.build())
            it.onNext(resultJson.toString())
        }
    }

    override fun getAuthorityNews(): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(APIRequestPath.news_url, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onGetArticleList(page: Int, size: Int, iBean: FormPostBean): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.ariticle_list_url
                .replace("{page}", "$page")
                .replace("{size}", "$size")
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onGetLaunchAD(): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(APIRequestPath.launch_url, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onPopups(): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(APIRequestPath.popups_url, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onCancelOrder(id: String?): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.cancelOrder_url.replace("{id}", id!!)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onConfirmOrder(iBean: ConfirmOrderBean): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.confirmOrder_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onPlatform(nullStr: FormPostBean?): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.platform_url,
                    JSONObject(JSON.toJSONString(nullStr))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onAutoAuth(iBean: AutoAuthBean): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.autoAuth_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onScanCode(iBean: AutoAuthBean): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.scanCode_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onUserInfo(iBean: NullBean?): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(APIRequestPath.userInfo_url, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onPlatformApply(iBean: PlatformApplyBean): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.platformApply_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onPublicPlatformList(platformName: String?): Observable<String> {
        return Observable.just(JSONObject().toString())
    }

    override fun onPAQTaskList(page: Int, size: Int, iBean: NullBean): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.PAQTaskList_url
                .replace("{page}", "$page")
                .replace("{size}", "$size")
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onLoginauthRecord(page: Int, size: Int, iBean: FormPostBean): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.authRecord_url
                .replace("{page}", "$page")
                .replace("{size}", "$size")
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    tempPath,
                    if (!TextUtils.isEmpty(iBean.appId)) JSONObject(JSON.toJSONString(iBean)) else JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onZSauthRecord(page: Int, size: Int, iBean: FormPostBean?): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.authRecord_url
                .replace("{page}", "$page")
                .replace("{size}", "$size")
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject(JSON.toJSONString(iBean)))
            it.onNext(resultJson.toString())
        }
    }

    override fun onZSRepresent(iBean: ZSRepresentBean?): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.represent_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onRepresentRecord(page: Int, size: Int): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.representRecord_url
                .replace("{page}", "$page")
                .replace("{size}", "$size")
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onExclusivePlatformList(iPostBean: FormPostBean?): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.exclusivePlatformList_url,
                    JSONObject(JSON.toJSONString(iPostBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    //专属授权
    override fun onExclusiveAuth(iPostBean: ExclusivePostBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.exclusiveAuth_url,
                    JSONObject(JSON.toJSONString(iPostBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onCancelAuth(orderId: String?): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.cancelAuth_url.replace("{orderId}", orderId!!)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    tempPath,
                    JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }


    override fun onGetOnlineNumber(iPostBean: FormPostBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.getOnlineNumber_url,
                    JSONObject(JSON.toJSONString(iPostBean))
                )
            it.onNext(resultJson.toString())
        }
    }


    override fun onQRCodeAuth(iPostBean: FormPostBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.QRCodeAuth_url,
                    JSONObject(JSON.toJSONString(iPostBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun updateRecord(iposeBean: UpdateRecordBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.updateRecord_url,
                    JSONObject(JSON.toJSONString(iposeBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun checkUpdate(nullBean: NullBean): Observable<String> {

        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(
                    APIRequestPath.checkUpdate_url,
                    JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onSendEmail(iBean: FindPostBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.sendEmail_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onResetPassword(iBean: ForgetBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.resetPassword_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }


    override fun onAddMarkForOrder(iBean: OrderMarkInfo): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.setRemark_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onIdCardVerif(iBean: RealNameModel): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.idCardVerif_url,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onDisclaimer(): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(
                    APIRequestPath.disclaimerUrl,
                    JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onUserAgreement(): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(
                    APIRequestPath.userAgreementUrl,
                    JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }

    override fun onProtocol(): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(
                    APIRequestPath.protocolUrl,
                    JSONObject()
                )
            it.onNext(resultJson.toString())
        }
    }


    override fun onRegisterUser(iBean: RegisterBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.registerUrl,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }
    }


    override fun sendSms(iBean: SendMsgBean): Observable<String> {
        return Observable.create {
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(
                    APIRequestPath.sendSmsUrl,
                    JSONObject(JSON.toJSONString(iBean))
                )
            it.onNext(resultJson.toString())
        }

    }

    override fun onRechargeRecord(page: Int, size: Int): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.rechargeRecordUrl.pageSize(page, size)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetGetSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun onMyAgencyList(phone: String, page: Int, size: Int): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.myAgencyListUrl.pageSize(page, size)
            val iBean = SearchInfoBean()
            iBean.phone = phone
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject(JSON.toJSONString(iBean)))
            it.onNext(resultJson.toString())
        }
    }

    override fun onMyUserList(phone: String, page: Int, size: Int): Observable<String> {

        return Observable.create {
            val tempPath = APIRequestPath.myUserListUrl.pageSize(page, size)
            val iBean = SearchInfoBean()
            iBean.phone = phone
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject(JSON.toJSONString(iBean)))
            it.onNext(resultJson.toString())
        }
    }


    override fun addRemark(id: String, remark: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.addRemarkUrl
            val user = UserMarkModel()
            user.id = id
            user.remark = remark

            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject(JSON.toJSONString(user)))
            it.onNext(resultJson.toString())
        }
    }

    override fun bannedUser(id: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.bannedUserUrl.replace("{id}", id)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject(JSON.toJSONString(NullBean())))
            it.onNext(resultJson.toString())
        }
    }

    override fun keyRechargeUser(key: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.keyRechargeUrl
            val postJson=JSONObject()
            postJson.put("key",key)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, postJson)
            it.onNext(resultJson.toString())
        }
    }

    override fun rechargeUser(userId: String, amount: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.rechargeUrl
            val recharge = JSONObject()
            recharge.put("userId", userId)
            recharge.put("amount", amount)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, recharge)
            it.onNext(resultJson.toString())
        }
    }

    override fun userUpgrade(userId: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.userUpgradeUrl.replace("{id}", userId)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }

    override fun updateNicknameUrl(nickname: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.updateNicknameUrl
            val postJson=JSONObject()
            postJson.put("nickname",nickname)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, postJson)
            it.onNext(resultJson.toString())
        }
    }

    override fun afterSale(orderId: String): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.afterSale.replace("{id}", orderId)
            val postJson=JSONObject()
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, postJson)
            it.onNext(resultJson.toString())
        }
    }

    override fun userLogout(): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.userLogoutUrl
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, JSONObject())
            it.onNext(resultJson.toString())
        }
    }
    override fun bathchRegister(number: Int): Observable<String> {
        return Observable.create {
            val tempPath = APIRequestPath.bathchRegisterUrl
            val postJson=JSONObject()
            postJson.put("number",number)
            val resultJson = APIRequestServiceImpl.getInstance()
                .apiRequestNetPostSync(tempPath, postJson)
            it.onNext(resultJson.toString())
        }
    }
}