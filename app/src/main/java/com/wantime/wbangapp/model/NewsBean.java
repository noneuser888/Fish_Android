package com.wantime.wbangapp.model;

import java.util.List;

public class NewsBean {



    private List<BannerBean> banner;
    private List<ProjectBean> project;

    public List<BannerBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
    }

    public List<ProjectBean> getProject() {
        return project;
    }

    public void setProject(List<ProjectBean> project) {
        this.project = project;
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

    public static class ProjectBean {
        /**
         * id : 1
         * title : ssd
         * subTitle : asd
         * imageUrl : asd
         * webLink : asd
         */

        private int id;
        private String title;
        private String subTitle;
        private String imageUrl;
        private String webLink;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getWebLink() {
            return webLink;
        }

        public void setWebLink(String webLink) {
            this.webLink = webLink;
        }
    }
}
