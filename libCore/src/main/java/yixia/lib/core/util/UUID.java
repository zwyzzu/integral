package yixia.lib.core.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.Random;

import yixia.lib.core.cipher.MD5;
import yixia.lib.core.sharePreferences.PreferencesHelper;

@SuppressWarnings("unused")
public class UUID {

    private static UUID instance = new UUID();
    public static UUID getInstance() {
        return instance;
    }

    private String uuid;
    public void init(Context context){
        PreferencesHelper helper = PreferencesHelper.defaultInstance();
        helper.init(context.getApplicationContext());
        final String UUID_KEY = "user_private_uuid";
        if (helper.contains(UUID_KEY)) {
            this.uuid = helper.getString(UUID_KEY, "");
        }

        if (!TextUtils.isEmpty(this.uuid)) {
            return;
        }

        this.uuid = this.createUUID(context.getApplicationContext());
        helper.commitString(UUID_KEY, this.uuid);
    }

    private String createUUID(Context context) {
        StringBuilder buffer = new StringBuilder();
        //获取IMEI
        String imei = Device.Dev.getDeviceID(context);
        imei = this.fillUpString(imei);
        buffer.append(imei);
        //获取AndroidID
        String androidId = Device.Dev.getAndroidId(context);
        androidId = this.fillUpString(androidId);
        buffer.append(androidId);
        //获取Mac地址
        String macAddress = Device.NetWork.getMacAddress(context);
        macAddress = this.fillUpString(macAddress);
        buffer.append(macAddress);
        //获取手机型号
        String model = Device.OS.getModel();
        model = this.fillUpString(model);
        buffer.append(model);
        String manufacturer = Device.OS.getManufacturer();
        manufacturer = this.fillUpString(manufacturer);
        buffer.append(manufacturer);
        String text = buffer.toString();
        text = text.trim();
        if (TextUtils.isEmpty(text)) {
            text = this.randomString();
        }
        return MD5.md5Encode(text);
    }

    public String getUUID(Context context) {
        return this.uuid;
    }

    private String fillUpString(String text) {
        return !TextUtils.isEmpty(text) ? text : this.randomString();
    }

    private String randomString() {
        String random;
        String buffer = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        int range = buffer.length();
        final int RANDOM_LENGTH = 32;
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        random = sb.toString();
        return random;
    }
}
