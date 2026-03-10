package com.wantime.wbangapp.request;

import android.text.TextUtils;

import com.wantime.wbangapp.BuildConfig;
import com.wantime.wbangapp.model.UserBean;
import com.wantime.wbangapp.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private OkHttpClient okHttpClient = null;
    private Retrofit retrofit = null;
    private RetrofitApiService retrofitApiService = null;
    private static final String accessTokenFront = "Bearer"; //accessToken前面的部分
    private static RetrofitManager retrofitManager = null;

    private WBangAPIRequest mWBangAPIRequest = null;

    public RetrofitManager() {
        initOkHttpClient();
        initRetrofit();
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseAPIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        retrofitApiService = retrofit.create(RetrofitApiService.class);
        mWBangAPIRequest = WBangAPIRequestImpl.Companion.getInstance();
    }

    private void initOkHttpClient() {
        okHttpClient = new OkHttpClient.Builder() //设置缓存文件路径，和文件大小
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder1 = request.newBuilder();
                        builder1.addHeader("type", "android")
                                .addHeader("version_Name", BuildConfig.VERSION_NAME)
                                .addHeader("version_Code", BuildConfig.VERSION_CODE + "");
                        UserBean mUserBean = Constants.INSTANCE.onGetUserBaseInfoWithToken();
                        if (TextUtils.isEmpty(mUserBean.getToken())) {
                            builder1.addHeader("Authorization", accessTokenFront + mUserBean.getToken());
                        }
                        Request build = builder1.build();
                        return chain.proceed(build);

                    }
                }).build();

    }

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            synchronized (RetrofitManager.class) {
                if (retrofitManager == null) {
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }


    public RetrofitApiService getApiService() {
        return retrofitManager.retrofitApiService;
    }

    public WBangAPIRequest getApiRequest() {
        return retrofitManager.mWBangAPIRequest;
    }
}
