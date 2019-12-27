package com.video.test.module.download;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.db.DBManager;
import com.video.test.javabean.M3U8DownloadBean;
import com.video.test.network.RxExceptionHandler;
import com.video.test.sp.SpUtils;
import com.video.test.utils.FileUtils;
import com.video.test.utils.LogUtils;
import com.video.test.utils.RxSchedulers;
import com.video.test.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import jaygoo.library.m3u8downloader.M3U8Downloader;
import jaygoo.library.m3u8downloader.M3U8DownloaderConfig;
import jaygoo.library.m3u8downloader.OnM3U8DownloadListener;
import jaygoo.library.m3u8downloader.bean.M3U8;
import jaygoo.library.m3u8downloader.bean.M3U8Task;

/**
 * @author Enoch Created on 2018/6/27.
 */
public class DownloadPresenter extends DownloadContract.Presenter<DownloadModel> {
    private static final String TAG = "DownloadPresenter";
    private static String M3U8TAG = "DownM3U8Listener";
    private String mUserLevel;
    private boolean isRequestingTask = false;

    @Override
    public void subscribe() {

    }


    @Override
    void getSDCardFreeSize() {
        Disposable disposable = mModel.getSDCardFreeSize()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long freeSpace) {
                        LogUtils.d(TAG, "getSDCardFreeSize == " + freeSpace);
                        if (AppConstant.REMIND_SPACE_SIZE > freeSpace) {
                            mView.showCleanCacheDialog();
                        }

                    }
                }, new RxExceptionHandler<Throwable>(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        LogUtils.e(getClass(), "getSDCardFreeSize onError" + throwable.getMessage());

                    }
                }));
        addDisposable(disposable);
    }


    @Override
    void getM3U8Tasks(String userLevel) {
        if (isRequestingTask) {
            return;
        }
        isRequestingTask = true;
        Observable<List<M3U8DownloadBean>> observable1 = Observable.create(emitter -> {
            List<M3U8DownloadBean> m3U8DownloadedBeans = DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadedTasks();
            emitter.onNext(m3U8DownloadedBeans);
        });
        Observable<List<M3U8DownloadBean>> observable2 = Observable.create(emitter -> {
            List<M3U8DownloadBean> m3U8DownloadingBeans = DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadingTasks();
            emitter.onNext(m3U8DownloadingBeans);
        });
        Disposable subscribe = Observable
                .zip(Observable.just(userLevel),
                        observable1,
                        observable2,
                        (Function3<String, List<M3U8DownloadBean>, List<M3U8DownloadBean>, List<Object>>) (level, m3U8DownloadedBeans, m3U8DownloadingBeans) -> {
                            ArrayList<Object> result = new ArrayList<>();
                            if (!m3U8DownloadedBeans.isEmpty() || !m3U8DownloadingBeans.isEmpty()) {
                                List<Object> m3u8Items = getM3u8Items(userLevel, m3U8DownloadedBeans, m3U8DownloadingBeans);
                                result.addAll(m3u8Items);
                            }
                            return result;
                        }
                )
                .compose(RxSchedulers.io_main())
                .subscribe(list -> {
                    isRequestingTask = false;
                    if (list.isEmpty()) {
                        mView.showNoCacheBackground();
                        mView.hideEditBtn();
                    } else {
                        mView.showEditBtn();
                        mView.hideNoCacheBackground();
                        mView.setDownloadBeans(list);
                    }
                }, new RxExceptionHandler<>(throwable -> {
                    isRequestingTask = false;
                    LogUtils.e(TAG, "getM3U8Tasks Error : " + throwable.getMessage());
                }));
        addDisposable(subscribe);
//        List<M3U8DownloadBean> m3U8DownloadedBeans = DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadedTasks();
//        List<M3U8DownloadBean> m3U8DownloadingBeans = DBManager.getInstance(TestApp.getContext()).queryM3U8DownloadingTasks();
//
//        if (m3U8DownloadedBeans.isEmpty() && m3U8DownloadingBeans.isEmpty()) {
//            mView.showNoCacheBackground();
//            mView.hideEditBtn();
//        } else {
//            mView.showEditBtn();
//            mView.hideNoCacheBackground();
//            getM3u8Items(userLevel, m3U8DownloadedBeans, m3U8DownloadingBeans);
//        }
    }


    @Override
    void initM3U8Listener() {
        mUserLevel = SpUtils.getString(TestApp.getContext(), AppConstant.USER_TOKEN_LEVEL, AppConstant.USER_NORMAL);

        M3U8Downloader.getInstance().setOnM3U8DownloadListener(new OnM3U8DownloadListener() {
            @Override
            public void onDownloadItem(M3U8Task task, long itemFileSize, int totalTs, int curTs) {
                super.onDownloadItem(task, itemFileSize, totalTs, curTs);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + " itemFileSize : " + itemFileSize + " curTs : " + curTs + " totalTs : " + totalTs);
                updateM3U8StartTask(task, totalTs, curTs);
            }

            @Override
            public void onDownloadSuccess(M3U8Task task) {
                super.onDownloadSuccess(task);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "Success taskName : " + task.getVideoName());
                updateM3U8TaskSuccess(task);
                mView.showToast(task.getVideoName() + " 已完成缓存");
                getM3U8Tasks(mUserLevel);
            }

            @Override
            public void onDownloadPause(M3U8Task task) {
                super.onDownloadPause(task);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "Pause  taskName : " + task.getVideoName());
                mView.showToast(task.getVideoName() + " 已暂停");
                updateM3U8TaskStatus(task);
                getM3U8Tasks(mUserLevel);
            }

            @Override
            public void onDownloadPending(M3U8Task task) {
                super.onDownloadPending(task);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "Pending task status : " + task.getState());
                mView.showToast(task.getVideoName() + " 已添加缓存队列");
                insertM3U8Task(task);
            }

            @Override
            public void onDownloadPrepare(M3U8Task task) {
                super.onDownloadPrepare(task);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "Prepare task status : " + task.getState());
                updateM3U8TaskStatus(task);
            }

            @Override
            public void onDownloadProgress(M3U8Task task) {
                super.onDownloadProgress(task);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "progress task progress : " + task.getProgress());
                //mView.notifyDataChange(task.getFormatSpeed());
            }


            @Override
            public void onDownloadError(M3U8Task task, Throwable errorMsg) {
                super.onDownloadError(task, errorMsg);
                LogUtils.d(M3U8TAG, "thread : " + Thread.currentThread().getName() + "error task status : " + task.getState());
                updateM3U8TaskStatus(task);
                getM3U8Tasks(mUserLevel);
            }
        });
    }


    private List<Object> getM3u8Items(String userLevel, List<M3U8DownloadBean> downloadedBean, List<M3U8DownloadBean> downloadingBean) {
        ArrayList<Object> list = new ArrayList<>();
        LogUtils.d(TAG, "getMU38Items downloaded size" + downloadedBean.size());
        LogUtils.d(TAG, "getMU38Items downloading size" + downloadingBean.size());

        switch (userLevel) {
            case AppConstant.USER_NORMAL:
            case AppConstant.USER_VIP_EXPIRE:
                if (!downloadedBean.isEmpty()) {
                    list.add("已缓存");
                    list.addAll(downloadedBean);
                } else {
                    mView.showNoCacheBackground();
                }
                break;
            case AppConstant.USER_VIP:
            case AppConstant.USER_VIP_LASTDAY:
                if (!downloadingBean.isEmpty()) {
                    list.add("正在缓存");
                    list.addAll(downloadingBean);
                }
                if (!downloadedBean.isEmpty()) {
                    list.add("已缓存");
                    list.addAll(downloadedBean);
                }
                break;
            default:
                break;
        }
        return list;
    }

    private void insertM3U8Task(M3U8Task task) {
        M3U8DownloadBean downloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null == downloadBean) {
            M3U8DownloadBean m3U8DownloadBean = new M3U8DownloadBean();
            m3U8DownloadBean.setVideoUrl(task.getUrl());
            m3U8DownloadBean.setVideoId(task.getVideoId());
            m3U8DownloadBean.setVideoName(task.getVideoName());
            m3U8DownloadBean.setIsDownloaded(false);
            m3U8DownloadBean.setTaskStatus(task.getState());
            long rowID = DBManager.getInstance(TestApp.getContext()).insertM3U8Task(m3U8DownloadBean);
        } else {
            // 已经存在的情况下 ,更新该资源的状态
            LogUtils.d(TAG, "数据库已存在该数据");
            downloadBean.setTaskStatus(task.getState());
            downloadBean.setProgress(task.getProgress());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(downloadBean);
        }
    }

    private void updateM3U8StartTask(M3U8Task task, int totalTs, int curTs) {
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setCurTs(curTs);
            m3U8DownloadBean.setTotalTs(totalTs);
            m3U8DownloadBean.setTaskStatus(AppConstant.M3U8_TASK_DOWNLOADING);
            m3U8DownloadBean.setProgress(task.getProgress());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
    }


    private void updateM3U8TaskStatus(M3U8Task task) {
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setTaskStatus(task.getState());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
    }

    @Override
    void pauseAllTasks() {
        List<M3U8DownloadBean> list = DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks();
        for (M3U8DownloadBean m3u8 : list) {
            if (!m3u8.isDownloaded()) {
                m3u8.setTaskStatus(AppConstant.M3U8_TASK_PAUSE);
                DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3u8);
            }
        }
    }

    private void updateM3U8TaskSuccess(M3U8Task task) {
        M3U8 m3U8 = task.getM3U8();
        M3U8DownloadBean m3U8DownloadBean = DBManager.getInstance(TestApp.getContext()).queryM3U8BeanFromVideoUrl(task.getUrl());
        if (null != m3U8DownloadBean) {
            m3U8DownloadBean.setTaskStatus(task.getState());
            //下载完成的百分比应该都是 100%
            m3U8DownloadBean.setProgress(1.0f);
            m3U8DownloadBean.setIsDownloaded(true);
            m3U8DownloadBean.setDirFilePath(m3U8.getDirFilePath());
            m3U8DownloadBean.setM3u8FilePath(m3U8.getM3u8FilePath());
            m3U8DownloadBean.setTotalFileSize(m3U8.getFileSize());
            m3U8DownloadBean.setTotalTime(m3U8.getTotalTime());
            DBManager.getInstance(TestApp.getContext()).updateM3U8Task(m3U8DownloadBean);
        } else {
            LogUtils.d(TAG, "数据库未查询到数据");
        }
    }

    @Override
    public void deleteM3U8Task(List<Object> taskList, String... taskUrl) {
        Disposable disposable = Observable.just(Arrays.asList(taskUrl))
                .map(new Function<List<String>, Boolean>() {
                    @Override
                    public Boolean apply(List<String> taskUrl) throws Exception {
                        for (String task : taskUrl) {
                            M3U8Downloader.getInstance().cancel(task);
                        }
                        //批量删除数据库中的下载任务
                        List<M3U8DownloadBean> deleteTaskBeans = new ArrayList<>();
                        for (M3U8DownloadBean task : DBManager.getInstance(TestApp.getContext()).queryM3U8Tasks()) {
                            if (taskUrl.contains(task.getVideoUrl())) {
                                deleteTaskBeans.add(task);
                            }
                        }
                        DBManager.getInstance(TestApp.getContext()).deleteM3U8Task(deleteTaskBeans);
                        //这里遍历所有下载任务，排除要删除的任务，将要保留的任务添加到新的集合
                        List<String> pathList = new ArrayList<>();
                        for (Object o : taskList) {
                            if (o instanceof M3U8DownloadBean) {
                                String url = ((M3U8DownloadBean) o).getVideoUrl();
                                if (!taskUrl.contains(url)) {
                                    String m3U8Path = M3U8Downloader.getInstance().getM3U8Path(url);
                                    String m3u8DirPath = m3U8Path.substring(0, m3U8Path.lastIndexOf('/'));
                                    pathList.add(m3u8DirPath);
                                }
                            }
                        }
                        File saveDir = new File(M3U8DownloaderConfig.getSaveDir());
                        if (!saveDir.exists()) {
                            saveDir.mkdirs();
                        }
                        //这里遍历下载文件夹下的所有文件夹，如果子文件夹不是要保留的下载任务，则删除
                        File[] files = saveDir.listFiles();
                        if (files != null && files.length != 0) {
                            for (File file : files) {
                                if (file.isDirectory() && !pathList.contains(file.getAbsolutePath())) {
                                    FileUtils.deleteDir(file);
                                }
                            }
                        }
                        return true;
                    }
                }).compose(RxSchedulers.io_main())
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       ToastUtils.showLongToast(TestApp.getContext(), "操作成功");
                                       getM3U8Tasks(mUserLevel);
                                   } else {
                                       ToastUtils.showLongToast(TestApp.getContext(), "操作失败,请您稍后重试");
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   LogUtils.e(TAG, "deleteM3U8Task Error : " + throwable.getMessage());
                                   ToastUtils.showLongToast(TestApp.getContext(), "操作失败,请您稍后重试");
                               }
                           }
                );
        addDisposable(disposable);
    }


    @Override
    void startM3U8Task(M3U8DownloadBean downloadingBean) {
        M3U8Downloader.getInstance().download(downloadingBean.getVideoUrl(), downloadingBean.getVideoId(), downloadingBean.getVideoName());

    }

    @Override
    void pauseM3U8Task(M3U8DownloadBean downloadingBean) {
        M3U8Downloader.getInstance().pause(downloadingBean.getVideoUrl());
    }

}


