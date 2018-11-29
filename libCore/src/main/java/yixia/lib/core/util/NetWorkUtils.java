package yixia.lib.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetWorkUtils {

    /**
     * network is HSPA+
     * <p/>
     * 因该参数为android api13 新增的参数。固此处使用常量表示
     */
    public static final int NETWORK_TYPE_HSPAP = 15;

    /**
     * value:
     * null: no network
     * others: exist network
     */
    public enum NetworkStatus {
        OFF, MOBILE_3G, MOBILE_2G, WIFI, OTHER
    }

    public static boolean isThirdGeneration() {
        TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp()
                .getSystemService(Context.TELEPHONY_SERVICE);
        int netWorkType = telephonyManager.getNetworkType();
        switch (netWorkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false;
            default:
                return true;
        }
    }

    public static NetworkInfo getAvailableNetWorkInfo() {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) Utils.getApp()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return null;
            }
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.isAvailable()) {
                return activeNetInfo;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNetWorkType() {
        String netWorkType = "UNKNOWN";
        NetworkInfo netWorkInfo = getAvailableNetWorkInfo();
        if (netWorkInfo != null) {
            if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                netWorkType = "WIFI";
            } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager == null) {
                    return netWorkType;
                }
                switch (telephonyManager.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        netWorkType = "GPRS";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        netWorkType = "EDGE";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                        netWorkType = "UMTS";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        netWorkType = "HSDPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                        netWorkType = "HSUPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                        netWorkType = "HSPA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        netWorkType = "CDMA";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        netWorkType = "EVDO_0";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        netWorkType = "EVDO_A";
                        break;
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                        netWorkType = "1xRTT";
                        break;
                    case NETWORK_TYPE_HSPAP:
                        netWorkType = "HSPAP";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netWorkType = "LTE";
                        break;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        netWorkType = "EHRPD";
                        break;
                    default:
                        netWorkType = "UNKNOW";
                }
            }
        }
        return netWorkType;
    }

    public static NetworkStatus getNetworkStatus() {
        NetworkInfo networkInfo = getAvailableNetWorkInfo();
        if (null == networkInfo) {
            return NetworkStatus.OFF;
        }
        int type = networkInfo.getType();
        if (ConnectivityManager.TYPE_WIFI == type) {
            return NetworkStatus.WIFI;
        }
        TelephonyManager telephonyManager = (TelephonyManager) Utils.getApp()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return NetworkStatus.OFF;
        }
        type = telephonyManager.getNetworkType();
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return NetworkStatus.MOBILE_2G;
            default:
                return NetworkStatus.MOBILE_3G;
        }
    }

    public static String getNetWorkApnType() {
        String mApnName = "";
        try {
            ConnectivityManager cm = (ConnectivityManager) Utils.getApp()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return "";
            }
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.getTypeName() != null) {
                mApnName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
                if ("wifi".equalsIgnoreCase(mApnName)) {
                    mApnName = "wifi";
                } else {
                    // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
                    mApnName = info.getExtraInfo().toLowerCase();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mApnName;
    }

    public static boolean isWapApnType() {
        return "3gwap".equals(getNetWorkApnType());
    }

    /**
     * 网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean judgeNetworkConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) Utils.getApp()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否wifi网络
     *
     * @param context
     * @return
     */
    public static boolean judgeWifi() {
        ConnectivityManager cm = (ConnectivityManager) Utils.getApp()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return info.getType() == ConnectivityManager.TYPE_WIFI;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断有无网络
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable() {
        return getNetworkStatus() != NetworkStatus.OFF;
    }
}
