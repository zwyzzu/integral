package com.zhangwy.address;

import android.app.Activity;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import yixia.lib.core.util.Logger;

public class AddressActivity {

    public AddressActivity(Activity activity) {
        Gson gson = new Gson();
        List<AddressBean> addressBeans = gson.fromJson(getCityJson(activity), new TypeToken<List<AddressBean>>() {}.getType());
        AreaPickerView areaPickerView = new AreaPickerView(activity, R.style.Dialog, addressBeans);
        areaPickerView.setAreaPickerViewCallback(value -> {
            if (value.length == 3) {
                Logger.d(addressBeans.get(value[0]).getLabel() + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getLabel() + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getChildren().get(value[2]).getLabel());
            } else {
                Logger.d(addressBeans.get(value[0]).getLabel() + "-" + addressBeans.get(value[0]).getChildren().get(value[1]).getLabel());
            }
        });
        areaPickerView.setSelect();
        areaPickerView.show();
    }

    private String getCityJson(Activity activity) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = activity.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("region.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}