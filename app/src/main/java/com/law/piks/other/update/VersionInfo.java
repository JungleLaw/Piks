package com.law.piks.other.update;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Law on 2016/10/8.
 */

public class VersionInfo {
    @JSONField(name = "name")
    private String appName;
    @JSONField(name = "version")
    private String versionCode;
    @JSONField(name = "changelog")
    private String changelog;
    @JSONField(name = "updated_at")
    private long updatedDate;
    @JSONField(name = "versionShort")
    private String versionName;
    @JSONField(name = "build")
    private String build;
    @JSONField(name = "installUrl")
    private String installUrl;
    @JSONField(name = "install_url")
    private String install_url;
    @JSONField(name = "direct_install_url")
    private String direct_install_url;
    @JSONField(name = "update_url")
    private String update_url;
    @JSONField(name = "binary")
    private Binary binary;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getDirect_install_url() {
        return direct_install_url;
    }

    public void setDirect_install_url(String direct_install_url) {
        this.direct_install_url = direct_install_url;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public Binary getBinary() {
        return binary;
    }

    public void setBinary(Binary binary) {
        this.binary = binary;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "appName='" + appName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", changelog='" + changelog + '\'' +
                ", updatedDate=" + updatedDate +
                ", versionName='" + versionName + '\'' +
                ", build='" + build + '\'' +
                ", installUrl='" + installUrl + '\'' +
                ", install_url='" + install_url + '\'' +
                ", direct_install_url='" + direct_install_url + '\'' +
                ", update_url='" + update_url + '\'' +
                ", binary=" + binary +
                '}';
    }

    public class Binary {
        private long fsize;

        public long getFsize() {
            return this.fsize;
        }

        public void setFsize(long fsize) {
            this.fsize = fsize;
        }
    }
}
