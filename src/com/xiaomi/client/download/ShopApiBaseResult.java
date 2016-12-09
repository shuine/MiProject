
package com.xiaomi.client.download;

/**
 */
public class ShopApiBaseResult {

    public ShopApiBaseResult(Builder builder) {
        mInfo = builder.mInfo;
        mCode = builder.mCode;
        mDescription = builder.mDescription;
        mCloseLink = builder.mCloseLink;
        mJsonData = builder.mJsonData;
    }

    /**
     * 接口状态码 0 -> success
     */
    public long mCode;

    /**
     * 栗子 "success"
     */
    public String mInfo;

    /**
     * 接口返回结果概述 栗子 "获取成功"
     */
    public String mDescription;

    /**
     * 关站链接
     */
    public String mCloseLink;

    /**
     * 具体接口数据
     */
    public StringBuilder mJsonData;

    public boolean isApiOk(boolean isNotUseData) {
        return mCode == 0 && (isNotUseData || (mJsonData != null && mJsonData.length() > 0));
    }

    public static class Builder {
        private long mCode;
        private String mInfo;
        private String mDescription;
        private String mCloseLink;
        private StringBuilder mJsonData;

        public Builder setCode(long code) {
            mCode = code;
            return this;
        }

        public Builder setInfo(String info) {
            mInfo = info;
            return this;
        }

        public Builder setDescription(String description) {
            mDescription = description;
            return this;
        }

        public Builder setCloseLink(String closeLink) {
            mCloseLink = closeLink;
            return this;
        }

        public Builder setJsonData(StringBuilder jsonData) {
            mJsonData = jsonData;
            return this;
        }

        public ShopApiBaseResult build() {
            return new ShopApiBaseResult(this);
        }
    }

    @Override
    public String toString() {
        return "ShopApiBaseResult{" +
                "mCloseLink='" + mCloseLink + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mInfo='" + mInfo + '\'' +
                ", mCode=" + mCode +
                ",mJsonData=" + mJsonData +
                '}';
    }
}
