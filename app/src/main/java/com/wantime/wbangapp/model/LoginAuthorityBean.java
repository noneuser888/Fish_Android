package com.wantime.wbangapp.model;

import android.text.TextUtils;
import android.view.View;

import java.util.List;

public class LoginAuthorityBean {

    private long currentTime;
    private ListBean list;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public static class ListBean {
 
        private int total;
        private int size;
        private int current;
        private boolean searchCount;
        private int pages;
        private List<RecordsBean> records;
        private List<?> orders;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public List<?> getOrders() {
            return orders;
        }

        public void setOrders(List<?> orders) {
            this.orders = orders;
        }

        public static class RecordsBean {

            private String id;
            private String producerId;
            private String consumerId;
            private String agencyId;
            private Object tid;
            private String taskId;
            private String nickname;
            private String platformName;
            private String appId;
            private double price;
            private double inviteReward;
            private double consumerIncome;
            private Object agencyIncome;
            private String redirectUrl;
            private String uuid;
            private String wxUserNickname;
            private int type;
            private String imageUrl;
            private String limitTime;
            private String createTime;
            private String acceptTime;
            private long submitTime;
            private Object updateTime;
            private int status;
            private int representStatus;
            private int leaseTime;
            private int version;
            private int isUploadImg;
            private String description;
            private int settleTime;
            private String info;
            private String authUrl;
            private List<String> imgList;
            private String typeName = "";
            private String taskName = "";
            private String nickName = "";

            private int infoVisible = View.VISIBLE;
            private int stateVisible = View.VISIBLE;
            private int ssVisible = View.GONE;//专属申诉是否显示
            private int ssFinishVisible = View.GONE;//已经申诉


            public int getStateVisible() {
                return stateVisible;
            }

            public void setStateVisible(int stateVisible) {
                this.stateVisible = stateVisible;
            }

            public String getTaskName() {
                if (TextUtils.isEmpty(taskId)) return "";
                return taskId;
            }

            public void setTaskName(String taskName) {
                this.taskName = taskName;
            }

            public String getNickName() {
                if (TextUtils.isEmpty(wxUserNickname)) return "";
                return wxUserNickname;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getProducerId() {
                return producerId;
            }

            public void setProducerId(String producerId) {
                this.producerId = producerId;
            }

            public String getConsumerId() {
                return consumerId;
            }

            public void setConsumerId(String consumerId) {
                this.consumerId = consumerId;
            }

            public String getAgencyId() {
                return agencyId;
            }

            public void setAgencyId(String agencyId) {
                this.agencyId = agencyId;
            }

            public Object getTid() {
                return tid;
            }

            public void setTid(Object tid) {
                this.tid = tid;
            }

            public String getTaskId() {
                return taskId;
            }

            public void setTaskId(String taskId) {
                this.taskId = taskId;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getPlatformName() {
                return platformName;
            }

            public void setPlatformName(String platformName) {
                this.platformName = platformName;
            }

            public String getAppId() {
                return appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public double getPrice() {
                return price;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public double getInviteReward() {
                return inviteReward;
            }

            public void setInviteReward(double inviteReward) {
                this.inviteReward = inviteReward;
            }

            public double getConsumerIncome() {
                return consumerIncome;
            }

            public void setConsumerIncome(double consumerIncome) {
                this.consumerIncome = consumerIncome;
            }

            public Object getAgencyIncome() {
                return agencyIncome;
            }

            public void setAgencyIncome(Object agencyIncome) {
                this.agencyIncome = agencyIncome;
            }

            public String getRedirectUrl() {
                return redirectUrl;
            }

            public void setRedirectUrl(String redirectUrl) {
                this.redirectUrl = redirectUrl;
            }

            public String getUuid() {
                return uuid;
            }

            public void setUuid(String uuid) {
                this.uuid = uuid;
            }

            public String getWxUserNickname() {
                return wxUserNickname;
            }

            public void setWxUserNickname(String wxUserNickname) {
                this.wxUserNickname = wxUserNickname;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public String getLimitTime() {
                return limitTime;
            }

            public void setLimitTime(String limitTime) {
                this.limitTime = limitTime;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getAcceptTime() {
                return acceptTime;
            }

            public void setAcceptTime(String acceptTime) {
                this.acceptTime = acceptTime;
            }

            public long getSubmitTime() {
                return submitTime;
            }

            public void setSubmitTime(long submitTime) {
                this.submitTime = submitTime;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getRepresentStatus() {
                return representStatus;
            }

            public void setRepresentStatus(int representStatus) {
                this.representStatus = representStatus;
            }

            public int getLeaseTime() {
                return leaseTime;
            }

            public void setLeaseTime(int leaseTime) {
                this.leaseTime = leaseTime;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public int getIsUploadImg() {
                return isUploadImg;
            }

            public void setIsUploadImg(int isUploadImg) {
                this.isUploadImg = isUploadImg;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public int getSettleTime() {
                return settleTime;
            }

            public void setSettleTime(int settleTime) {
                this.settleTime = settleTime;
            }

            public String getInfo() {
                if(TextUtils.isEmpty(info))return "暂无";
                return info;
            }

            public void setInfo(String info) {
                this.info = info;
            }

            public String getAuthUrl() {
                return authUrl;
            }

            public void setAuthUrl(String authUrl) {
                this.authUrl = authUrl;
            }

            public List<String> getImgList() {
                return imgList;
            }

            public void setImgList(List<String> imgList) {
                this.imgList = imgList;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public int getInfoVisible() {
                if (TextUtils.isEmpty(info)) setInfoVisible(View.GONE);
                else setInfoVisible(View.VISIBLE);
                return infoVisible;
            }

            public void setInfoVisible(int infoVisible) {
                this.infoVisible = infoVisible;
            }

            public String getTypeName() {
                switch (getType()) {
                    case 0:
                        if (getPrice() < 1)
                            setTypeName("二维码");
                        else setTypeName("一键授权");
                        break;
                    case 4:
                        setTypeName("二维码");
                        break;
                    case 5:
                        setTypeName("专属授权");
                        break;
                    default:
                        setTypeName("一键授权");
                        break;
                }
                return typeName;
            }

            public int getSsVisible() {
                if (representStatus == 0)//representStatus是否申述
                    if (isUploadImg == 1) { //不需要提交图片那么默认时间就是10分钟
                        if (System.currentTimeMillis() - submitTime > settleTime * 60 * 1000)
                            setSsVisible(View.GONE);
                        else setSsVisible(View.VISIBLE);
                    } else { //需要上传图片 那么根据设置的时间
                        if (System.currentTimeMillis() - submitTime > 10 * 1000 * 60)
                            setSsVisible(View.GONE);
                        else setSsVisible(View.VISIBLE);
                    }
                else setSsVisible(View.GONE);

                return ssVisible;
            }

            public void setSsVisible(int ssVisible) {
                this.ssVisible = ssVisible;
            }

            public int getSsFinishVisible() {
                if (representStatus == 1) setSsFinishVisible(View.VISIBLE);
                else setSsFinishVisible(View.GONE);
                return ssFinishVisible;
            }

            public void setSsFinishVisible(int ssFinishVisible) {
                this.ssFinishVisible = ssFinishVisible;
            }
        }
    }
}
