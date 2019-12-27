package com.video.test.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static float KB = 1024;
    private static float MB = 1048576;
    private static float GB = 1073741824;

    private StringUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        return "".equals(str.trim());
    }

    public static boolean isEmpty(EditText et) {
        return isEmpty(getText(et));
    }

    public static boolean isEmpty(TextView tv) {
        return isEmpty(getText(tv));
    }

    public static String getText(EditText editText) {
        if (editText == null) {
            return "";
        }
        return editText.getText().toString();
    }


    public static String getText(TextView textView) {
        if (textView == null) {
            return null;
        }
        return textView.getText().toString();
    }

    public static String getSecurityMobile(String mobile) {
        if (isEmpty(mobile)) {
            return null;
        }
        int length = mobile.length();
        if (length == 11) {
            String begin = mobile.substring(0, 3);
            String end = mobile.substring(8);
            return begin + "*****" + end;

        } else {
            return mobile;
        }
    }

    public static String groupMobile(String mobile) {
        if (isEmpty(mobile)) {
            return null;
        }
        char[] arr = mobile.toCharArray();
        StringBuilder sb_mobile = new StringBuilder();
        if (arr.length == 11) {
            sb_mobile.append(arr[0]).append(arr[1]).append(arr[2]);
            sb_mobile.append(" ").append(arr[3]).append(arr[4]).append(arr[5]).append(arr[6]);
            sb_mobile.append(" ").append(arr[7]).append(arr[8]).append(arr[9]).append(arr[10]);
            return sb_mobile.toString();
        }
        return mobile;
    }

    public static String getValue(String string) {
        return getValue(string, "");
    }

    public static String getValue(String string, String defaultVal) {
        return isEmpty(string) ? defaultVal : string;
    }

    public static String getMoney(String string) {
        return formatPrice(getValue(string, "0.00"));
    }

    public static double getFloatVal(String string) {
        return Double.parseDouble(getValue(string, "0"));
    }

    public static String getInt(String string) {
        return getValue(string, "0");
    }

    public static int getIntVal(String string) {
        return Integer.parseInt(getValue(string, "0"));
    }

    public static String formatPrice(String price) {
        return formatPrice(Double.parseDouble(price), 2);
    }

    public static String formatPrice(double price) {
        return formatPrice(price, 2);
    }

    public static String formatPrice(double price, int scale) {
        BigDecimal bigDecimal = new BigDecimal(price).setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.toPlainString();

    }


    /**
     * 手机号是否合法
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
     * 联通：130、131、132、152、155、156、185、186
     * 电信：133、153、180、189、（1349卫通）
     * 其他: 177,178,179
     * 总结起来就是第一位必定为1，第二位必定为3或5或7或8，其他位置的可以为0-9
     */
    public static boolean isMobile(String mobile) {
        //"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][3578]\\d{9}";
        if (isEmpty(mobile)) {
            return false;
        } else {
            return mobile.matches(telRegex);
        }
    }

    /**
     * 邮箱是否合法
     */
    public static boolean isEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判断是数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    public static void copy(Context context, String content) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager) {
            ClipData clipData = ClipData.newPlainText("text", content);
            clipboardManager.setPrimaryClip(clipData);
        }
    }


    public static double sum(String... floats) {
        BigDecimal decimal = new BigDecimal(0);
        for (String f : floats) {
            decimal = decimal.add(new BigDecimal(String.valueOf(f)));
        }
        return decimal.doubleValue();
    }

    /**
     * @param size size of download file
     * @return
     */
    public static String formatDownloadSpeed(int size) {
        long fileSize = (long) size;
        String showSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            showSize = fileSize + " Kb/s";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            showSize = Long.toString(fileSize / 1024) + " KB/s";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            DecimalFormat df = new DecimalFormat("0.00");
            double speed = (double) fileSize / (1024 * 1024);
            showSize = df.format(speed) + " MB/s";
        }
        return showSize;
    }

    /**
     * 格式化文件大小
     */
    public static String formatFileSize(long size) {
        if (size >= GB) {
            return String.format("%.1f GB", size / GB);
        } else if (size >= MB) {
            float value = size / MB;
            return String.format(value > 100 ? "%.0f MB" : "%.1f MB", value);
        } else if (size >= KB) {
            float value = size / KB;
            return String.format(value > 100 ? "%.0f KB" : "%.1f KB", value);
        } else {
            return String.format("%d B", size);
        }
    }

    /**
     * @param size 视频的大小 原始单位 转换为MB 小数点2位
     * @return
     */
    public static String formatVideoFileSize(double size) {
        double fileSize = size;
        DecimalFormat df = new DecimalFormat("0.00");
        String showSize = "";
        if (size >= 0 && size < 1024) {
            showSize = df.format(fileSize) + " M";
        } else if (fileSize >= 1024) {
            double sizeDouble = fileSize / 1024;
            showSize = df.format(sizeDouble) + " G";
        }
        return showSize;
    }

    /**
     * @param size 原始文件大侠
     * @return 转换为 MB 大小
     */
    public static String formatByte2Mb(double size) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format((size / 1024D / 1024D));
    }


    /**
     * @param timeSecond 单位秒
     * @return
     */
    public static String stringForTime(int timeSecond) {
        if (timeSecond <= 0 || timeSecond >= 24 * 60 * 60) {
            return "00:00";
        }
        int totalSeconds = timeSecond;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
