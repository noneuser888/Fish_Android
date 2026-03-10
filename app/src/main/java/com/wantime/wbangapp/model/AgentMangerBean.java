package com.wantime.wbangapp.model;

import android.text.TextUtils;
import android.view.View;

import java.util.List;

public class AgentMangerBean {

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
        /**
         * phone : 12345678990
         * nickname :
         * remark :
         * id : 1280065023666028123
         * status : 0
         */

        private String phone;
        private String nickname;
        private String remark;
        private String id;
        private int status;
        private double balance;
        private int isVisiable= View.GONE;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            if(TextUtils.isEmpty(nickname)){
                return "昵称 (暂未设置)";
            }
            return "昵称  "+ nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getRemark() {
            if(TextUtils.isEmpty(remark))
                return "暂无备注";
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public int getIsVisiable() {
            return isVisiable;
        }

        public void setIsVisiable(int isVisiable) {
            this.isVisiable = isVisiable;
        }
    }
}
