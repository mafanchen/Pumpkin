package com.video.test.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.RequiresPermission;
import android.text.format.Formatter;

import com.video.test.AppConstant;
import com.video.test.TestApp;
import com.video.test.sp.SpUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;

/**
 * Created by Enoch on 2017/5/8.
 */

public class NetworkUtils {

    private static final String TAG = "NetworkUtils";


    private NetworkUtils() {
        throw new UnsupportedOperationException("UtilClazz can't initialize");
    }

    /*判断网络是否可用*/
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /*判断wifi网络是否可用*/
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /*判断移动网络是否可用*/
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /*检测网络状态*/
    public static void checkNetConnectChange() {
        if (isNetWorkAvailable(TestApp.getContext())) {
            LogUtils.i(TAG, "目前网络可用");
            if (isWifiConnected(TestApp.getContext())) {
                LogUtils.i(TAG, "目前WIFI网络可用");
                onNetworkStatusChange(true, false, true);
            } else if (isMobileConnected(TestApp.getContext())) {
                LogUtils.i(TAG, "目前移动网络可用");
                onNetworkStatusChange(true, true, false);
            }
        } else {
            LogUtils.i(TAG, "目前网络不可用");
            onNetworkStatusChange(false, false, false);
        }
    }

    /*根据软件设置,更改P2P网络状态*/
    private static void onNetworkStatusChange(boolean isConnect, boolean isMobile, boolean isWifi) {
        boolean switchButton = SpUtils.getBoolean(TestApp.getContext(), "netSwitchButton", true);
        LogUtils.i(TAG, "目前SwitchButton按钮的状态是" + switchButton);
        if (isConnect) {
            //网络连接正常
            if (isMobile) {
                // 移动流量开关开启状态
                if (switchButton) {
                    // 移动流量开关 状态开
                    LogUtils.d(TAG, "onNetworkStatusChange 移动网络可用");
                    SpUtils.putInt(TestApp.getContext(), "networkStatus", AppConstant.MOBILE_NETWORK_CAN_USE);

                } else {
                    // 移动流量开关 状态关
                    LogUtils.d(TAG, "onNetworkStatusChange 移动网络不可用");
                    SpUtils.putInt(TestApp.getContext(), "networkStatus", AppConstant.MOBILE_NETWORK_CAN_NOT_USE);
                }
            }

            if (isWifi) {
                LogUtils.d(TAG, "onNetworkStatusChange Wifi网络可用");
                SpUtils.putInt(TestApp.getContext(), "networkStatus", AppConstant.WIFI_NETWORK_CAN_USE);
            }

        } else {
            // 网络连接关闭
            ToastUtils.showToast(TestApp.getContext(), "网络已断开，请您检查网络连接！");
        }
    }

    /**
     * Return the ip address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    public static String getIPAddress(final boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0, size = ias.size(); i < size; i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Return the domain address.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @RequiresPermission(INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) TestApp.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) TestApp.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) TestApp.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi() {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) TestApp.getContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) return "";
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }
}
