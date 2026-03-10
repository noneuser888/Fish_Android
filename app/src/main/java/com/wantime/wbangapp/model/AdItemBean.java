package com.wantime.wbangapp.model;

import java.util.List;

public class AdItemBean {
    /**
     * id : 1
     * position : startBanner
     * tittle : 启动广告
     */

    private String id;
    private String position;
    private String tittle;
    private List<ImgListBean> imgList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public List<ImgListBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImgListBean> imgList) {
        this.imgList = imgList;
    }

    public static class ImgListBean {


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
