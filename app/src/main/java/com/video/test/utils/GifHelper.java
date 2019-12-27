package com.video.test.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.shuyu.gsyvideoplayer.listener.GSYVideoGifSaveListener;
import com.shuyu.gsyvideoplayer.utils.AnimatedGifEncoder;
import com.shuyu.gsyvideoplayer.utils.GifCreateHelper;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * java类作用描述
 *
 * @author : AhhhhDong
 * @date : 2019/5/14 10:12
 */
public class GifHelper extends GifCreateHelper {

    public GifHelper(StandardGSYVideoPlayer standardGSYVideoPlayer, GifSaveListener gsyVideoGifSaveListener) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener);
    }

    public GifHelper(StandardGSYVideoPlayer standardGSYVideoPlayer, GifSaveListener gsyVideoGifSaveListener, int delay, int inSampleSize, int scaleSize, int frequencyCount) {
        super(standardGSYVideoPlayer, gsyVideoGifSaveListener, delay, inSampleSize, scaleSize, frequencyCount);
    }

    @Override
    public void createGif(File file, List<String> pics, int delay, int inSampleSize, int scaleSize, GSYVideoGifSaveListener gsyVideoGifSaveListener) {

        if (gsyVideoGifSaveListener instanceof GifSaveListener) {
            File coverFile = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AnimatedGifEncoder localAnimatedGifEncoder = new AnimatedGifEncoder();
            //start
            localAnimatedGifEncoder.start(baos);
            //设置生成gif的开始播放时间。0为立即开始播放
            localAnimatedGifEncoder.setRepeat(0);
            localAnimatedGifEncoder.setDelay(delay);
            for (int i = 0; i < pics.size(); i++) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = inSampleSize;
                // 先获取原大小
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pics.get(i), options);
                double w = (double) options.outWidth / scaleSize;
                double h = (double) options.outHeight / scaleSize;
                // 获取新的大小
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(pics.get(i), options);
                Bitmap pic = ThumbnailUtils.extractThumbnail(bitmap, (int) w, (int) h);
                localAnimatedGifEncoder.addFrame(pic);
                if (coverFile == null) {
                    coverFile = createCover(pic);
                }
                bitmap.recycle();
                pic.recycle();

                gsyVideoGifSaveListener.process(i + 1, pics.size());
            }
            //finish
            localAnimatedGifEncoder.finish();
            try {
                FileOutputStream fos = new FileOutputStream(file.getPath());
                baos.writeTo(fos);
                baos.flush();
                fos.flush();
                baos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                ((GifSaveListener) gsyVideoGifSaveListener).result(false, file, coverFile);
                return;
            }
            ((GifSaveListener) gsyVideoGifSaveListener).result(true, file, coverFile);
        } else {
            super.createGif(file, pics, delay, inSampleSize, scaleSize, gsyVideoGifSaveListener);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createCover(Bitmap bitmap) {
        File coverFile = new File(DownloadUtil.getCacheDirFile(), "gif_cover_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = null;
        try {
            if (!coverFile.exists()) {
                coverFile.createNewFile();
            }
            fos = new FileOutputStream(coverFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return coverFile;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public abstract static class GifSaveListener implements GSYVideoGifSaveListener {

        @Override
        public void result(boolean success, File file) {

        }

        @Override
        public void process(int curPosition, int total) {

        }

        /**
         * 对原方法进行修改增加了封面文件
         *
         * @param success 是否成功创建
         * @param gif     gif文件
         * @param cover   封面文件
         */
        public abstract void result(boolean success, File gif, File cover);
    }

}
