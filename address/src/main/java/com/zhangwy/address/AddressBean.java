package com.zhangwy.address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddressBean {

    @SerializedName("name")
    private String label;
    @SerializedName("code")
    private String value;
    private boolean status;
    @SerializedName("city")
    private List<CityBean> children;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<CityBean> getChildren() {
        return children;
    }

    public void setChildren(List<CityBean> children) {
        this.children = children;
    }

    public class CityBean {
        @SerializedName("name")
        private String label;
        @SerializedName("code")
        private String value;
        private boolean status;
        @SerializedName("area")
        private List<AreaBean> children;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public List<AreaBean> getChildren() {
            return children;
        }

        public void setChildren(List<AreaBean> children) {
            this.children = children;
        }

    }

    public class AreaBean {
        @SerializedName("name")
        private String label;
        @SerializedName("code")
        private String value;
        private boolean status;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

}
