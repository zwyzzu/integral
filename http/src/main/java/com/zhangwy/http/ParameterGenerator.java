package com.zhangwy.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import yixia.lib.core.cipher.MD5;
import yixia.lib.core.util.AppUtils;
import yixia.lib.core.util.Device;
import yixia.lib.core.util.UUID;

public class ParameterGenerator {

    private static Map<String, String> common() {
        Map<String, String> params = new HashMap<>();
        params.put("udid", UUID.getInstance().getUUID());
        params.put("versionCode", String.valueOf(Device.App.getVersionCode(AppUtils.getContext()))); //客户端应用版本号
        params.put("versionName", Device.App.getVersionName(AppUtils.getContext()));  // 客户端应用版本名
        params.put("pkgName", Device.App.getPackageName(AppUtils.getContext()));  // 包名
        params.put("versionOS", Device.OS.getVersion()); // OS版本号
        params.put("lang", Device.OS.getOSLanguage(AppUtils.getContext()));// 系统语言代码(使用ISO-639标准)
        params.put("model", Device.OS.getModel());// 设备类型
        params.put("channel", "debug");// 渠道
        params.put("time", String.valueOf(System.currentTimeMillis()));
        params.put("network", String.valueOf(Device.NetWork.netWorkType(AppUtils.getContext()).getCode()));   // 网络类型
        return params;
    }

    public static String generate(FormBody formBody) throws JSONException {
        JSONObject params = new JSONObject();
        JSONObject jo = new JSONObject();
        if (formBody != null && formBody.size() > 0) {
            for (int i = 0; i < formBody.size(); i++) {
                params.put(formBody.name(i), formBody.value(i));
            }
        }
        jo.put("common", new JSONObject(common()));
        jo.put("params", params);
        return jo.toString();
    }

    public static String sign(String params) {
        return MD5.md5Encode(params).substring(2, 22).toLowerCase(Locale.US);
    }
}
