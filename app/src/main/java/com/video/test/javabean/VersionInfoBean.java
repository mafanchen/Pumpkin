package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Enoch Created on 2018/8/8.
 */
public class VersionInfoBean {

    private InfoBean android;
    private InfoBean iOS;
    private InfoBean PC;

    public InfoBean getAndroid() {
        return android;
    }

    public void setAndroid(InfoBean android) {
        this.android = android;
    }

    public InfoBean getIOS() {
        return iOS;
    }

    public void setIOS(InfoBean iOS) {
        this.iOS = iOS;
    }

    public InfoBean getPC() {
        return PC;
    }

    public void setPC(InfoBean PC) {
        this.PC = PC;
    }

    public class InfoBean {
        private String update_details;
        private String size;
        @SerializedName("versions_check")
        private String md5;
        private String versions;
        private String versions_name;
        private String download;
        private String type;
        private String add_time;
        private boolean is_update;

        public String getUpdate_details() {
            return update_details;
        }

        public void setUpdate_details(String update_details) {
            this.update_details = update_details;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getVersions() {
            return versions;
        }

        public void setVersions(String versions) {
            this.versions = versions;
        }

        public String getVersions_name() {
            return versions_name;
        }

        public void setVersions_name(String versions_name) {
            this.versions_name = versions_name;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public boolean isIs_update() {
            return is_update;
        }

        public void setIs_update(boolean is_update) {
            this.is_update = is_update;
        }

        @Override
        public String toString() {
            return "InfoBean{" +
                    "update_details='" + update_details + '\'' +
                    ", size='" + size + '\'' +
                    ", md5='" + md5 + '\'' +
                    ", versions='" + versions + '\'' +
                    ", versions_name='" + versions_name + '\'' +
                    ", download='" + download + '\'' +
                    ", type='" + type + '\'' +
                    ", add_time='" + add_time + '\'' +
                    ", is_update=" + is_update +
                    '}';
        }
    }
}