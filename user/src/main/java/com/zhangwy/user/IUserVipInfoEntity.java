package com.zhangwy.user;

import java.util.List;

/**
 * Author: 张维亚
 * 创建时间：2015年12月22日 上午11:24:12
 * 修改时间：2015年12月22日 上午11:24:12
 * Description: 用户vip信息
 **/
@SuppressWarnings("unused")
public class IUserVipInfoEntity extends IUserBaseEntity {

    private static final long serialVersionUID = 8027904934581193887L;

    private String retSign;
    private String retTime;
    private Data data;

    public String getRetSign() {
        return retSign;
    }

    public void setRetSign(String retSign) {
        this.retSign = retSign;
    }

    public String getRetTime() {
        return retTime;
    }

    public void setRetTime(String retTime) {
        this.retTime = retTime;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String user_id;
        private String is_vip;
        private String endTime;
        private String ticketNum;
        private List<Item> items;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getIs_vip() {
            return is_vip;
        }

        public void setIs_vip(String is_vip) {
            this.is_vip = is_vip;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getTicketNum() {
            return ticketNum;
        }

        public void setTicketNum(String ticketNum) {
            this.ticketNum = ticketNum;
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

    }

    public static class Item {
        private String id;
        private String name;
        private String endTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
