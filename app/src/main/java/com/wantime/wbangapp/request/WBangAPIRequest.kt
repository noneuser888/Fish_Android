package com.wantime.wbangapp.request

import com.wantime.wbangapp.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody

interface WBangAPIRequest {

    //    @POST("login")
    fun onLogin(iBean: UserBean): Observable<String>

    fun onAuthHome(agencyId: String?): Observable<String>

//    @POST("upload") //上传图片的公共接口 单个图片
//    @Multipart
//    fun uploadPic(@Part file: MultipartBody.Part?): Observable<String>

    // @POST("upload") //上传图片的公共接口
    fun uploadPicMore(multipartBodyBuilder: MultipartBody.Builder): Observable<String>


    // @GET("xiandu/category/wow")
    fun getAuthorityNews(): Observable<String>

    // @GET("articleList/{page}/{size}")
    fun onGetArticleList(page: Int, size: Int, iBean: FormPostBean): Observable<String>

    //广告
    //  @POST("launchAd")
    fun onGetLaunchAD(): Observable<String>

    //是否开弹窗
    //  @GET("popups")
    fun onPopups(): Observable<String>

    //取消公众号代绑任务
    //  @POST("/cancelOrder/{id}")
    fun onCancelOrder(id: String?): Observable<String>

    //授权成功回调地址
    //  @POST("confirmOrder")
    fun onConfirmOrder(iBean: ConfirmOrderBean): Observable<String>

    //支持的平台列表
    //  @POST("platform")
    fun onPlatform(nullStr: FormPostBean?): Observable<String>

    //一键授权
    //  @POST("autoAuth")
    fun onAutoAuth(iBean: AutoAuthBean): Observable<String>

    //网页授权
    //  @POST("scanCode")
    fun onScanCode(iBean: AutoAuthBean): Observable<String>

    //个人信息
    // @POST("userInfo")
    fun onUserInfo(iBean: NullBean?): Observable<String>

    //申请添加新平台
    fun onPlatformApply(iBean: PlatformApplyBean): Observable<String>

    //公众号平台列表
    //  @POST("publicPlatformList")
    fun onPublicPlatformList(platformName: String?): Observable<String>

    //查询公众号绑定任务的状态
    //  @POST("PAQTaskList/{page}/{size}")
    fun onPAQTaskList(page: Int, size: Int, iBean: NullBean): Observable<String>

    /***授权记录查询 */
    // @POST("/authRecord/{page}/{size}")
    fun  //登录授权记录
            onLoginauthRecord(
        page: Int,
        size: Int,
        iBean: FormPostBean
    ): Observable<String>

    // @POST("/authRecord/{page}/{size}")
    fun  //专属授权记录
            onZSauthRecord(
        page: Int,
        size: Int,
        iBean: FormPostBean?
    ): Observable<String>

    /***********专属对应的接口 */ //专属授权申诉
//    @POST("represent")
    fun onZSRepresent(iBean: ZSRepresentBean?): Observable<String>

    //申述记录
//    @GET("representRecord/{page}/{size}")
    fun onRepresentRecord(
        page: Int,
        size: Int
    ): Observable<String>

    //专属对接平台列表
//    @POST("exclusivePlatformList")
    fun onExclusivePlatformList(iPostBean: FormPostBean?): Observable<String>

    //专属授权
//    @POST("exclusiveAuth")
    fun onExclusiveAuth(iPostBean: ExclusivePostBean): Observable<String>

    //取消专属对接任务
//    @POST("/cancelAuth/{orderId}")
    fun onCancelAuth(orderId: String?): Observable<String>

    //专属对接获取在线数量
    fun onGetOnlineNumber(iPostBean: FormPostBean): Observable<String>

    //显示二维码后扣费接口
    fun onQRCodeAuth(iPostBean: FormPostBean): Observable<String>

    //更新授权记录
    fun updateRecord(iposeBean: UpdateRecordBean): Observable<String>

    //更新APP
    fun checkUpdate(nullBean: NullBean): Observable<String>

    //根据账号发送邮箱
    fun onSendEmail(iBean: FindPostBean): Observable<String>

    //修改密码
    fun onResetPassword(iBean: ForgetBean): Observable<String>

    //订单记录 添加备注信息
    fun onAddMarkForOrder(iBean: OrderMarkInfo): Observable<String>

    //实名校验
    fun onIdCardVerif(iBean: RealNameModel): Observable<String>

    //用户服务协议
    fun onUserAgreement(): Observable<String>

    //免责声明
    fun onDisclaimer(): Observable<String>

    //用户服务协议
    fun onProtocol(): Observable<String>

    //用户注册
    fun onRegisterUser(iBean: RegisterBean): Observable<String>

    //手机号 发送验证码
    fun sendSms(iBean: SendMsgBean): Observable<String>

    //充值记录
    fun onRechargeRecord(page: Int, size: Int): Observable<String>

    //用户的代理
    fun onMyAgencyList(phone:String,page: Int, size: Int): Observable<String>

    //用户下面的普通用户
    fun onMyUserList(phone:String,page: Int, size: Int): Observable<String>
    //为用户添加备注
    fun addRemark(id:String,remark:String): Observable<String>
    //封禁用户
    fun bannedUser(id:String): Observable<String>

    //卡密充值
    fun keyRechargeUser(key:String): Observable<String>
    //给用户充值
    fun rechargeUser(userId:String,amount:String): Observable<String>
    //普通用户升级成二级用户
    fun userUpgrade(userId:String): Observable<String>

    //修改用户的昵称
    fun updateNicknameUrl(nickname:String): Observable<String>
    //申请售后
    fun afterSale(orderId: String): Observable<String>
    //退出APP 主动调用
    fun userLogout():Observable<String>
    //批量注册子账户
    fun bathchRegister(number:Int):Observable<String>
}