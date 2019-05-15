package com.zhangwy.upgrade;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by zhangwy on 2018/1/22 下午4:41.
 * Updated by zhangwy on 2018/1/22 下午4:41.
 * Description
 */
@SuppressWarnings("unused")
public class AppCheckEntity extends AppBaseEntity {
    private static final long serialVersionUID = -5258594310681065694L;

    @JSONField(name = "data")
    public AppVersionEntity detail;

    public AppVersionEntity getDetail() {
        return detail;
    }

    public void setDetail(AppVersionEntity detail) {
        this.detail = detail;
    }

    public static class AppVersionEntity extends AppBaseEntity {
        private static final long serialVersionUID = 4900615382971288833L;
        public String buildKey;
        public String buildType;
        @JSONField(name = "buildIsFirst")
        public String isFirst;
        @JSONField(name = "buildIsLastest")
        public String isLast;
        @JSONField(name = "buildFileSize")
        public String size;
        @JSONField(name = "buildName")
        public String name;
        @JSONField(name = "buildIcon")
        public String icon;
        @JSONField(name ="buildBuildVersion")
        public String buildVersion;
        @JSONField(name ="downloadURL")
        public String downloadUrl;
        @JSONField(name ="buildVersionNo")
        public String versionNo;
        @JSONField(name ="buildVersion")
        public String version;
        @JSONField(name ="buildShortcutUrl")
        public String shortcutUrl;
        @JSONField(name ="buildDescription")
        public String description;
        @JSONField(name ="buildUpdateDescription")
        public String updateDescription;

        public String getBuildKey() {
            return buildKey;
        }

        public void setBuildKey(String buildKey) {
            this.buildKey = buildKey;
        }

        public String getBuildType() {
            return buildType;
        }

        public void setBuildType(String buildType) {
            this.buildType = buildType;
        }

        public String getIsFirst() {
            return isFirst;
        }

        public void setIsFirst(String isFirst) {
            this.isFirst = isFirst;
        }

        public String getIsLast() {
            return isLast;
        }

        public void setIsLast(String isLast) {
            this.isLast = isLast;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getBuildVersion() {
            return buildVersion;
        }

        public void setBuildVersion(String buildVersion) {
            this.buildVersion = buildVersion;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(String versionNo) {
            this.versionNo = versionNo;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getShortcutUrl() {
            return shortcutUrl;
        }

        public void setShortcutUrl(String shortcutUrl) {
            this.shortcutUrl = shortcutUrl;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUpdateDescription() {
            return updateDescription;
        }

        public void setUpdateDescription(String updateDescription) {
            this.updateDescription = updateDescription;
        }
    }
}
