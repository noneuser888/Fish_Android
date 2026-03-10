package com.wantime.wbangapp.model;

import android.text.TextUtils;

import com.wantime.wbangapp.utils.Constants;
import java.util.List;

public class PlatformBean {

    private String advice;
    private List<PlatformListBean> platformList;
    private List<BannerBean> banner;

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public List<PlatformListBean> getPlatformList() {
        return platformList;
    }

    public void setPlatformList(List<PlatformListBean> platformList) {
        this.platformList = platformList;
    }

    public List<BannerBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
    }

    public static class PlatformListBean {


        private String appId;
        private String imageUrl;
        private String nickname;
        private int usableNum;
        private int id;
        private String platformName;
        private int tid;
        private String bundleId;
        private String scope="";
        private String bbyMoney = "";
        private String userMoney = "";
        private String qrPrice="";
        private String channel="";
        private String channel1="";
        private String channel2="";
        private String channel3="";
        private String price1="";
        private String price2="";
        private String price3="";

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        private String price = "单价(***)";
//        private String price1 = "单价(***)";

        public String getBbyMoney() {
            return bbyMoney;
        }

        public void setBbyMoney(String bbyMoney) {
            this.bbyMoney = bbyMoney;
        }

        public String getUserMoney() {
            return userMoney;
        }

        public void setUserMoney(String userMoney) {
            this.userMoney = userMoney;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getUsableNum() {
            return usableNum;
        }

        public void setUsableNum(int usableNum) {
            this.usableNum = usableNum;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPlatformName() {
            return platformName;
        }

        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getBundleId() {
            return bundleId;
        }

        public void setBundleId(String bundleId) {
            this.bundleId = bundleId;
        }

        public String getQrPrice() {
            return qrPrice;
        }

        public void setQrPrice(String qrPrice) {
            this.qrPrice = qrPrice;
        }

        public String getChannel() {
            switch (Constants.accountType){
                case 2:channel=channel2;break;
                case 3:channel=channel3;break;
                default:channel=channel1;
            }

            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getPrice() {
            switch (Constants.accountType){
                case 2:price=price2;break;
                case 3:price=price3;break;
                default:price=price1;
            }
            return "(单价："+price+")";
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getChannel1() {
            return channel1;
        }

        public void setChannel1(String channel1) {
            this.channel1 = channel1;
        }

        public String getChannel2() {
            return channel2;
        }

        public void setChannel2(String channel2) {
            this.channel2 = channel2;
        }

        public String getChannel3() {
            return channel3;
        }

        public void setChannel3(String channel3) {
            this.channel3 = channel3;
        }

        public String getPrice1() {
            return price1;
        }

        public void setPrice1(String price1) {
            this.price1 = price1;
        }

        public String getPrice2() {
            return price2;
        }

        public void setPrice2(String price2) {
            this.price2 = price2;
        }

        public String getPrice3() {
            return price3;
        }

        public void setPrice3(String price3) {
            this.price3 = price3;
        }

        //        public String getPrice1() {
//            if (!TextUtils.isEmpty(getUserMoney())) {
//                return "一键授权：" + getUserMoney();
//            }
//            return price1;
//        }

//        public void setPrice1(String price1) {
//            this.price1 = price1;
//        }
    }

    public static class BannerBean {

        private String imgUrl;
        private String webLink;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getWebLink() {
            return webLink;
        }

        public void setWebLink(String webLink) {
            this.webLink = webLink;
        }
    }
}
