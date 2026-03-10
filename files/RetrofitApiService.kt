package com.wantime.wbangapp.request;

import com.wantime.wbangapp.model.*;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;


public interface RetrofitApiService {

//    @POST("login")
//    Observable<ResponseBody> onLogin(@Body UserBean iUser);
//
//
//    @POST("upload") //上传图片的公共接口 单个图片
//    @Multipart
//    Observable<ResponseBody> uploadPic(@Part MultipartBody.Part file);
//
//    @POST("upload") //上传图片的公共接口
//    @Multipart
//    Observable<ResponseBody> uploadPicMore(@Part ArrayList<MultipartBody.Part> files);
//
//
//    @GET("xiandu/category/wow")
//    Observable<ResponseBody> getAuthorityNews();
//
//    @GET("articleList/{page}/{size}")
//    Observable<ResponseBody> onGetArticleList(@Path("page") int page, @Path("size") int size);
//
//    //广告
//    @POST("launchAd")
//    Observable<ResponseBody> onGetLaunchAD();
//
//    //是否开弹窗
//    @GET("popups")
//    Observable<ResponseBody> onPopups();
//
//    //取消公众号代绑任务
//    @POST("/cancelOrder/{id}")
//    Observable<ResponseBody> onCancelOrder(@Path("id") String id);
//
//    //授权成功回调地址
//    @POST("confirmOrder")
//    Observable<ResponseBody> onConfirmOrder(@Body ConfirmOrderBean iBean);
//
//    //支持的平台列表
//    @POST("platform")
//    Observable<ResponseBody> onPlatform(@Body FormPostBean nullStr);
//
//    //一键授权
//    @POST("autoAuth")
//    Observable<ResponseBody> onAutoAuth(@Body AutoAuthBean iBean);
//
//    //个人信息
//    @POST("userInfo")
//    Observable<ResponseBody> onUserInfo(@Body NullBean iBean);
//
//    //申请添加新平台
//    @POST("platformApply")
//    Observable<ResponseBody> onPlatformApply(@Body PlatformApplyBean iBean);
//
//    //公众号平台列表
//    @POST("publicPlatformList")
//    Observable<ResponseBody> onPublicPlatformList(@Field("platformName") String platformName);
//
//    //查询公众号绑定任务的状态
//    @POST("PAQTaskList/{page}/{size}")
//    Observable<ResponseBody> onPAQTaskList(
//            @Path("page") int page,
//            @Path("size") int size,
//            @Body NullBean iBean
//    );
//
//    /***授权记录查询****/
//    @POST("/authRecord/{page}/{size}")
//    //登录授权记录
//    Observable<ResponseBody> onLoginauthRecord(
//            @Path("page") int page,
//            @Path("size") int size,
//            @Body NullBean iBean
//    );
//
//    @POST("/authRecord/{page}/{size}")
//        //专属授权记录
//    Observable<ResponseBody> onZSauthRecord(
//            @Path("page") int page,
//            @Path("size") int size,
//            @Body FormPostBean iBean
//    );
//
//    /***********专属对应的接口***********/
//    //专属授权申诉
//    @POST("represent")
//    Observable<ResponseBody> onZSRepresent(@Body ZSRepresentBean iBean);
//
//    //申述记录
//    @GET("representRecord/{page}/{size}")
//    Observable<ResponseBody> onRepresentRecord(
//            @Path("page") int page,
//            @Path("size") int size
//    );
//
//    //专属对接平台列表
//    @POST("exclusivePlatformList")
//    Observable<ResponseBody> onExclusivePlatformList(@Body FormPostBean iPostBean);
//
//    //专属授权
//    @POST("exclusiveAuth")
//    Observable<ResponseBody> onExclusiveAuth(
//            @Body String appId,
//            @Body String qrCode,
//            @Body String uuid
//    );
//
//    //取消专属对接任务
//    @POST("/cancelAuth/{orderId}")
//    Observable<ResponseBody> onCancelAuth(@Path("orderId") String orderId);


}