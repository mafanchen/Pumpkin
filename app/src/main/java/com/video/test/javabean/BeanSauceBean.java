package com.video.test.javabean;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class BeanSauceBean {

    public static class AddPlayUrl {
        private int err;
        @SerializedName("file_size")
        private double fileSize;
        private String name;
        private String result;
        private String sh1;
        private String task;

        public int getErr() {
            return err;
        }

        public void setErr(int err) {
            this.err = err;
        }

        public double getFileSize() {
            return fileSize;
        }

        public void setFileSize(double fileSize) {
            this.fileSize = fileSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getResule() {
            return result;
        }

        public void setResule(String resule) {
            this.result = resule;
        }

        public String getSh1() {
            return sh1;
        }

        public void setSh1(String sh1) {
            this.sh1 = sh1;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }
    }

    public static class EnableP2PNet {
        private String result;
        private String task;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }
    }


    public static class GetTaskState {
        private String result;
        private String task;
        private InfoBean info;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }
    }

    public static class GetP2PTasks {

        private String result;
        private String task;
        private List<InfoBean> nodes;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public List<InfoBean> getNodes() {
            return nodes;
        }

        public void setNodes(List<InfoBean> nodes) {
            this.nodes = nodes;
        }

    }

    public static class ControlTask {
        private String result;
        private String task;

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTask() {
            return task;
        }

        public void setTask(String task) {
            this.task = task;
        }

    }

    public static class InfoBean {
        private double dlpercent;
        private int dlspeed;
        private double filesize;
        private String name;
        private String sha1;
        private int taskerr;
        private String tasktp;
        private int upspeed;

        public double getDlpercent() {
            return dlpercent;
        }

        public void setDlpercent(double dlpercent) {
            this.dlpercent = dlpercent;
        }

        public int getDlspeed() {
            return dlspeed;
        }

        public void setDlspeed(int dlspeed) {
            this.dlspeed = dlspeed;
        }

        public double getFilesize() {
            return filesize;
        }

        public void setFilesize(double filesize) {
            this.filesize = filesize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSha1() {
            return sha1;
        }

        public void setSha1(String sha1) {
            this.sha1 = sha1;
        }

        public int getTaskerr() {
            return taskerr;
        }

        public void setTaskerr(int taskerr) {
            this.taskerr = taskerr;
        }

        public String getTasktp() {
            return tasktp;
        }

        public void setTasktp(String tasktp) {
            this.tasktp = tasktp;
        }

        public int getUpspeed() {
            return upspeed;
        }

        public void setUpspeed(int upspeed) {
            this.upspeed = upspeed;
        }
    }
}
