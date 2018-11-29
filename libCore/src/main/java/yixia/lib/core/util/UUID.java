package yixia.lib.core.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Random;

/**
 * Created by liutao on 14/06/2018.
 */
public class UUID {

    // 获取UUID,其实用Application context也可以
    public static String getUUID() {
        // 从本地文件读取UUID
        // 如果从本地文件读取UUID为null，重新生成UUID
        String uuid;
        SharedPreferences sp = Utils.getApp()
                .getSharedPreferences("plugin_sp", Context.MODE_PRIVATE);
        uuid = sp.getString("uuid", "");
        if (!TextUtils.isEmpty(uuid)) {
            return uuid;
        }
        String imei = "";
        /*
         * 获取ANDROID_ID,注意：在Android<=2.1 or
         * Android>=2.3的版本是可靠，稳定的，但在2.2的版本并不是100%可靠的
         */
        String androidId = DeviceUtils.getAndroidID();
        // 获取MAC地址，注意：没有wifi，或者wifi没有打开获取不到
        String mac = DeviceUtils.getMacAddress();
        // 获取手机型号
        String model = DeviceUtils.getModel();
        // 获取手机厂商
        String manufacturer = DeviceUtils.getManufacturer();
        uuid = imei + androidId + mac + model + manufacturer;
        uuid = uuid.trim();
        if (uuid.equals("")) {
            uuid = getRandomString(Utils.getApp(), 16);
        }
        uuid = SignUtils.md5(uuid);
        sp.edit().putString("uuid", uuid).apply();
        return uuid;
    }

    // 获取16位随即数
    public static String getRandomString(Context context, int length) {
        String random;
        String buffer = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        random = sb.toString();
        return random;
    }
}
