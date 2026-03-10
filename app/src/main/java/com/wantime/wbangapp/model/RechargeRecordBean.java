package com.wantime.wbangapp.model;

import java.util.List;

public class RechargeRecordBean {
    /**
     * records : [{"id":"1280768329157459968","userId":"1280065023666028544","amount":100,"afterBalance":9499,"time":"2020-07-08 15:39:00","type":2},{"id":"1280768076987514880","userId":"1280065023666028544","amount":100,"afterBalance":9599,"time":"2020-07-08 15:38:00","type":2},{"id":"1280767822858829824","userId":"1280065023666028544","amount":100,"afterBalance":9699,"time":"2020-07-08 15:36:59","type":2},{"id":"1280761807497084928","userId":"1280065023666028544","amount":100,"afterBalance":9799,"time":"2020-07-08 15:13:05","type":2},{"id":"1280760246851092480","userId":"1280065023666028544","amount":100,"afterBalance":9899,"time":"2020-07-08 15:06:53","type":2}]
     * total : 5
     * size : 20
     * current : 1
     * orders : []
     * searchCount : true
     * pages : 1
     */

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
         * id : 1280768329157459968
         * userId : 1280065023666028544
         * amount : 100
         * afterBalance : 9499
         * time : 2020-07-08 15:39:00
         * type : 2
         */

        private String id;
        private String userId;
        private int amount;
        private int afterBalance;
        private String time;
        private String username;
        private int type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getAfterBalance() {
            return afterBalance;
        }

        public void setAfterBalance(int afterBalance) {
            this.afterBalance = afterBalance;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
