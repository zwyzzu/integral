package yixia.lib.core.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import yixia.lib.core.cipher.MD5;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/6 下午3:26
 * 修改时间：2017/4/6 下午3:26
 * Description: 设备信息
 */
@SuppressWarnings("unused")
public class Device {
    private static final String TAG = "Device";

    public static final class Dev {
        public static String getAndroidId(Context context) {
            if (context == null)
                return "";

            try {
                return Settings.System.getString(context.getContentResolver(), Secure.ANDROID_ID);
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * 获取IMEI
         */
        public static String getDeviceID(Context context) {
            if (context == null)
                return "";

            try {
                TelephonyManager manager = getTelephonyManager(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED) {
                    @SuppressLint("HardwareIds")
                    String deviceId = (manager == null) ? "" : manager.getDeviceId();
                    return TextUtils.isEmpty(deviceId) ? "" : deviceId;
                }
            } catch (Exception e) {
                Logger.e("getDeviceID", e);
            }
            return "";
        }

        public static String getSimSerialNumber(Context context) {
            if (context == null)
                return "";

            try {
                TelephonyManager manager = getTelephonyManager(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED) {
                    @SuppressLint("HardwareIds")
                    String number = (manager == null) ? "" : manager.getSimSerialNumber();
                    return TextUtils.isEmpty(number) ? "" : number;
                }
            } catch (Exception e) {
                Logger.e("getSimSerialNumber", e);
            }
            return "";
        }

        public static Operators getOperators(Context context) {
            if (context == null)
                return Operators.UNKNOWN;

            TelephonyManager manager = getTelephonyManager(context);
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED) {
                    @SuppressLint("HardwareIds")
                    String subscriberId = (manager == null) ? "" : manager.getSubscriberId();
                    if (subscriberId == null || subscriberId.length() < 5)
                        return Operators.UNKNOWN;
                    switch (subscriberId.substring(0, 5)) {
                        case "46000":
                        case "46002":
                        case "46007":
                            return Operators.CMCC;
                        case "46001":
                        case "46006":
                            return Operators.UNICOM;
                        case "46003":
                        case "46005":
                            return Operators.TELECOM;
                        default:
                            return Operators.UNKNOWN;
                    }
                }
            } catch (Exception e) {
                Logger.e("getOperators", e);
            }
            return Operators.UNKNOWN;
        }

        private static TelephonyManager getTelephonyManager(Context context) {
            if (context != null) {
                return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            }
            return null;
        }

        /**
         * judge whether it's locked screen status
         *
         * @return true indicates that it's locked screen; otherwise is false
         */
        public static boolean isLockScreen(Context context) {
            if (context == null)
                return true;

            try {
                KeyguardManager keyguardManager = (KeyguardManager) context
                        .getSystemService(Context.KEYGUARD_SERVICE);
                return (null != keyguardManager && keyguardManager.inKeyguardRestrictedInputMode());
            } catch (Exception e) {
                Logger.e(TAG, "isLockScreen", e);
            }
            return true;
        }
    }

    public enum Operators {
        UNKNOWN(0, "未知运营商"), CMCC(1, "中国移动"), UNICOM(2, "中国联通"), TELECOM(3, "中国电信");
        private int code;
        private String name;

        Operators(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return this.code;
        }

        public String getName() {
            return this.name;
        }
    }

    public static final class Locate {

        public static Pair<Double, Double> getCoordinate(Context context) {
            Location location = getLocation(context);
            if (location == null)
                return null;
            return Pair.create(location.getLongitude(), location.getLatitude());
        }

        public static Location getLocation(Context context) {
            LocationManager manager = getLocationManager(context);
            if (manager == null)
                return null;
            List<String> providers = manager.getProviders(true);
            if (Util.isEmpty(providers))
                return null;
            Location location;
            try {
                if (providers.contains(LocationManager.GPS_PROVIDER) && (location = manager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)) != null)
                    return location;
                if (providers.contains(LocationManager.NETWORK_PROVIDER) && (location = manager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)) != null)
                    return location;
                for (String provider : providers) {
                    if (TextUtils.isEmpty(provider)
                            || (location = manager.getLastKnownLocation(provider)) == null)
                        continue;
                    return location;
                }
            } catch (SecurityException e) {
                Logger.e(TAG, "Locate.getLocation", e);
            }
            return null;
        }

        private static LocationManager getLocationManager(Context context) {
            if (context != null) {
                return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }
            return null;
        }
    }

    public static final class OS {

        /**
         * 产品名称
         */
        public static String getProduct() {
            return Build.PRODUCT;
        }

        /**
         * 序列号
         */
        @SuppressLint("HardwareIds")
        public static String getSerial() {
            return Build.SERIAL;
        }

        /**
         * 获取手机型号
         */
        public static String getModel() {
            String model = Build.MODEL;
            return TextUtils.isEmpty(model) ? "" : model;
        }

        public static String getCpuType() {
            try {
                return Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0];
            } catch (Exception e) {
                return "";
            }
        }

        /**
         * 获取手机系统版本号
         */
        public static int getAndroidOSVersion() {
            return Build.VERSION.SDK_INT;
        }

        /**
         * 获取当前手机系统版本号
         *
         * @return 系统版本号
         */
        public static String getVersion() {
            return Build.VERSION.RELEASE;
        }

        /**
         * 获取手机厂商
         *
         * @return 手机厂商
         */
        public static String getDeviceBrand() {
            return Build.BRAND;
        }

        /**
         * 获取手机制造商
         *
         * @return 手机制造商
         */
        public static String getManufacturer() {
            return Build.MANUFACTURER;
        }

        public static String getDevice() {
            return Build.DEVICE;
        }

        public static String getHardWare() {
            return Build.HARDWARE;
        }

        public static String getOSLanguage(Context context) {
            if (context == null)
                return "";
            try {
                return context.getApplicationContext().getResources().getConfiguration()
                        .locale.getLanguage();
            } catch (Throwable throwable) {
                Logger.e("getOSLanguage", throwable);
            }
            return "";
        }
    }

    public static final class App {

        public static int getLabelId(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager
                        .getPackageInfo(context.getPackageName(), 0);
                return packageInfo.applicationInfo.labelRes;
            } catch (Exception e) {
                Logger.e("getLabelId", e);
            }
            return 0;
        }

        public static String getAppName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo =
                        packageManager.getPackageInfo(context.getPackageName(), 0);
                return packageInfo == null
                        ? null : packageInfo.applicationInfo.loadLabel(packageManager).toString();
            } catch (Exception e) {
                Logger.e("getAppName", e);
            }
            return "";
        }

        public static String getVersionName(Context context) {
            try {
                PackageInfo info = getPackageInfo(context);
                if (info != null)
                    return info.versionName;
            } catch (Exception e) {
                Logger.e("getVersionName", e);
            }
            return "";
        }

        public static int getVersionCode(Context context) {
            try {
                PackageInfo info = getPackageInfo(context);
                if (info != null)
                    return info.versionCode;
            } catch (Exception e) {
                Logger.e("getVersionCode", e);
            }
            return 0;
        }

        private static PackageInfo getPackageInfo(Context context)
                throws PackageManager.NameNotFoundException {
            return context == null ? null : context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        }

        public static boolean isPreInstall(Context context) {
            return context != null && context.getApplicationInfo() != null
                    && context.getApplicationInfo().flags == ApplicationInfo.FLAG_PERSISTENT;
        }

        public static String getPackageName(Context context) {
            try {
                return context == null ? "" : context.getPackageName();
            } catch (Exception e) {
                Logger.e("getAppPackageName", e);
                return "";
            }
        }
    }

    public static final class NetWork {

        /**
         * 获取手机网络类型
         */
        public static String isWifi(Context context) {
            String netType = "1";
            ConnectivityManager manager = getConnectivityManager(context);
            NetworkInfo networkInfo = manager == null ? null : manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    netType = "1";
                } else {
                    netType = "10";
                }
            }
            return netType;
        }

        public static String getWifiSSID(Context context) {
            WifiManager wifiManager = getWifiManager(context);
            WifiInfo wifiInfo;
            return wifiManager == null || (wifiInfo = wifiManager.getConnectionInfo()) == null
                    ? "" : wifiInfo.getBSSID();
        }

        /**
         * 去除分隔符”:”的大写MAC地址取MD5摘要
         */
        public static String getMac6(Context context) {
            String mac6 = getMacAddress(context);
            mac6 = TextUtils.isEmpty(mac6) ? "null" : mac6.replace(":", "");
            return MD5.md5Encode(mac6.toUpperCase());
        }

        /**
         * 获取手机MAC地址
         */
        public static String getMacAddress(Context context) {
            String mac = getMacAddressByWifi(context);
            if (TextUtils.isEmpty(mac) || TextUtils.equals("02:00:00:00:00:00", mac)) {
                mac = getMacAddressByIp(context);
            }
            return mac;
        }

        /**
         * 获取Mac通过WIFI
         *
         * @return Mac Address
         */
        @SuppressLint("HardwareIds")
        private static String getMacAddressByWifi(Context context) {
            WifiManager wifiManager = getWifiManager(context);
            WifiInfo wifiInfo;
            return wifiManager == null || (wifiInfo = wifiManager.getConnectionInfo()) == null
                    ? "" : wifiInfo.getMacAddress();
        }

        /**
         * 获取Mac通过IP
         *
         * @return Mac Address
         */
        public static String getMacAddressByIp(Context context) {
            String ip = getIpAddress(context);
            if (TextUtils.isEmpty(ip))
                return "";
            try {
                NetworkInterface network = NetworkInterface
                        .getByInetAddress(InetAddress.getByName(ip));
                return Util.byte2hex(network.getHardwareAddress(), ":");
            } catch (SocketException e) {
                Logger.e("getMacAddressByIp.SocketException", e);
            } catch (UnknownHostException e) {
                Logger.e("getMacAddressByIp.UnknownHostException", e);
            } catch (Exception e) {
                Logger.e("getMacAddressByIp.Exception", e);
            }
            return "";
        }

        public static String getIpAddress(Context context) {
            switch (netWorkType(context)) {
                case WIFI:
                    String ipAddress = getWifiIpAddress(context);
                    if (!TextUtils.isEmpty(ipAddress))
                        return ipAddress;
                default:
                    return getIpAddress();
            }
        }

        private static String getWifiIpAddress(Context context) {
            WifiManager manager = getWifiManager(context);
            WifiInfo info;
            if (manager == null || !manager.isWifiEnabled()
                    || (info = manager.getConnectionInfo()) == null)
                return "";
            int inAddress = info.getIpAddress();
            return (inAddress & 0xFF) + "." + ((inAddress >> 8) & 0xFF) + "."
                    + ((inAddress >> 16) & 0xFF) + "." + (inAddress >> 24 & 0xFF);
        }

        /**
         * 非WIFI下获取IP
         *
         * @return ip address
         */
        private static String getIpAddress() {
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                if (en == null)
                    return "";
                for (; en.hasMoreElements();) {
                    if (en.nextElement() == null)
                        continue;
                    Enumeration<InetAddress> enumIpAddress = en.nextElement().getInetAddresses();
                    if (enumIpAddress == null)
                        continue;
                    for (; enumIpAddress.hasMoreElements();) {
                        InetAddress address = enumIpAddress.nextElement();
                        if (address != null && !address.isLoopbackAddress() && !address
                                .isLinkLocalAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e("getOtherIpAddress", e);
            }
            return "";
        }

        public static int getWifiSize(Context context) {
            List<WifiConfiguration> array = getWifiArray(context);
            return array == null ? 0 : array.size();
        }

        public static List<WifiConfiguration> getWifiArray(Context context) {
            WifiManager manager = getWifiManager(context);
            return manager == null ? null : manager.getConfiguredNetworks();
        }

        public static NetType netWorkType(Context context) {
            ConnectivityManager connManager = getConnectivityManager(context);
            if (connManager == null)
                return NetType.NONE;

            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable())
                return NetType.NONE;
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    return mobileNetType(networkInfo);
                case ConnectivityManager.TYPE_BLUETOOTH:
                    return NetType.BLUETOOTH;
                case ConnectivityManager.TYPE_WIMAX:
                    return NetType.WIMAX;
                case ConnectivityManager.TYPE_ETHERNET:
                    return NetType.ETHERNET;
                default:
                    return NetType.UNKNOWN;
            }
        }

        private static NetType mobileNetType(NetworkInfo networkInfo) {
            switch (networkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS://联通2G
                case TelephonyManager.NETWORK_TYPE_EDGE://移动2G
                case TelephonyManager.NETWORK_TYPE_CDMA://电信2G
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN://api < 8 : replace by 11
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return NetType.MOBILE2;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A://电信3G
                case TelephonyManager.NETWORK_TYPE_EVDO_B://api < 9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EHRPD://api < 11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    return NetType.MOBILE3;
                case TelephonyManager.NETWORK_TYPE_LTE:
                case TelephonyManager.NETWORK_TYPE_HSPAP://api < 13 : replace by 15
                case 19://TelephonyManager.NETWORK_TYPE_LTE_CA
                    return NetType.MOBILE4;
                default:
                    String subtypeName = networkInfo.getSubtypeName();
                    if (TextUtils.isEmpty(subtypeName))
                        return NetType.MOBILE;

                    String mobile4 = "LTE-Advanced&WirelessMAN-Advanced&WIMAX&HSPA+&LTE&FDD-LTE"
                            + "&TDD-LTE";
                    if (mobile4.contains(subtypeName))
                        return NetType.MOBILE4;

                    String mobile3 = "TD-SCDMA&WCDMA&CDMA2000";
                    if (mobile3.contains(subtypeName))
                        return NetType.MOBILE3;
                    return NetType.MOBILE;
            }
        }

        /**
         * 检测网络状态
         */
        public static boolean isAvailable(Context context) {
            ConnectivityManager connManager = getConnectivityManager(context);
            if (connManager == null)
                return false;

            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();
        }

        public static boolean isWifiAvailable(Context context) {
            ConnectivityManager connManager = getConnectivityManager(context);
            if (connManager == null)
                return false;
            NetworkInfo wifiNetInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetInfo != null && wifiNetInfo.isAvailable();
        }

        private static ConnectivityManager getConnectivityManager(Context context) {
            return context == null ? null : (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        private static WifiManager getWifiManager(Context context) {
            return context == null ? null : (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
        }
    }

    public enum NetType {
        NONE(-1, "unAvailable", "网络不可用的"),
        UNKNOWN(0, "unKnown", "未知的"),
        WIFI(1, "wifi", "WIFI网络"),
        MOBILE(-2, "mobile", "移动网络-具体未知"),
        MOBILE2(2, "2G", "2G网络"),
        MOBILE3(3, "3G", "3G网络"),
        MOBILE4(4, "4G", "4G网络"),
        ETHERNET(11, "ethernet", "以太网"),
        BLUETOOTH(12, "BLUETOOTH", "蓝牙连接"),
        WIMAX(13, "wimax", "全球微波互联接入");
        private int code;
        private String name;
        private String desc;

        NetType(int code, String name, String desc) {
            this.code = code;
            this.name = name;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public static NetType find(int code) {
            for (NetType netType : NetType.values()) {
                if (code == netType.code)
                    return netType;
            }
            return UNKNOWN;
        }

        public boolean isAvailable() {
            return this != NONE;
        }
    }
}