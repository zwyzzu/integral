package com.zhangwy.integral.data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.zhangwy.integral.entity.CouponsBindEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2019/6/6.
 * Updated by zhangwy on 2019/6/6.
 * Description
 */
@SuppressWarnings("unused")
public abstract class ICouponsManager {
    private static ICouponsManager instance;

    public static ICouponsManager getInstance() {
        if (instance == null) {
            synchronized (ICouponsManager.class) {
                if (instance == null) {
                    instance = new ICouponsManagerImpl();
                }
            }
        }
        return instance;
    }

    public abstract void register(OnCouponsDataListener listener);

    public abstract void unRegister(OnCouponsDataListener listener);

    public abstract List<CouponsBindEntity> getUseable(String memberId);

    public abstract List<CouponsBindEntity> getUsed(String memberId);

    public abstract List<CouponsBindEntity> getOverDue(String memberId);

    public abstract List<CouponsBindEntity> getNearOverDue(String memberId);

    public abstract List<CouponsBindEntity> getCoupons(String memberId, CouponsMold mold);

    public abstract List<CouponsBindEntity> getAllNearOverDue();

    public abstract boolean hasNearOverDue();
    /*------------------------------------------------------------------------------*/
    private static class ICouponsManagerImpl extends ICouponsManager {
        private boolean loadDataEnd = false;
        private List<OnCouponsDataListener> listeners = new ArrayList<>();
        private CouponsCollection useable = new CouponsCollection();
        private CouponsCollection used = new CouponsCollection();
        private CouponsCollection overDue = new CouponsCollection();
        private CouponsCollection nearOverDue = new CouponsCollection();

        private ICouponsManagerImpl() {
            this.loadData();
        }

        @SuppressLint("StaticFieldLeak")
        private void loadData() {
            AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
                @Override
                protected Boolean doInBackground(String... strings) {
                    List<CouponsBindEntity> all = IDataManager.getInstance().getMemberCoupons("");
                    for (CouponsBindEntity coupons : all) {
                        if (coupons == null) {
                            continue;
                        }
                        if (coupons.used()) {
                            used.put(coupons.getBind(), coupons);
                        } else if (coupons.overdue()){
                            overDue.put(coupons.getBind(), coupons);
                        } else {
                            useable.put(coupons.getBind(), coupons);
                            if (coupons.nearOverDue()) {
                                nearOverDue.put(coupons.getBind(), coupons);
                            }
                        }
                    }
                    return true;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loadDataEnd = false;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    loadDataEnd = true;
                    notifyObserver();
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }
            };
            asyncTask.execute();
        }

        @Override
        public void register(OnCouponsDataListener listener) {
            if (this.listeners.contains(listener)) {
                return;
            }
            if (this.loadDataEnd) {
                listener.onLoadCouponsSuccess();
            }
            this.listeners.add(listener);
        }

        @Override
        public void unRegister(OnCouponsDataListener listener) {
            if (listener == null || !this.listeners.contains(listener)) {
                return;
            }
            this.listeners.remove(listener);
        }

        private void notifyObserver() {
            List<OnCouponsDataListener> array = new ArrayList<>(this.listeners);
            for (OnCouponsDataListener listener: array) {
                listener.onLoadCouponsSuccess();
            }
        }

        @Override
        public List<CouponsBindEntity> getUseable(String memberId) {
            if (TextUtils.isEmpty(memberId) || !this.loadDataEnd || !this.useable.containsKey(memberId)) {
                return new ArrayList<>();
            }
            return this.useable.get(memberId);
        }

        @Override
        public List<CouponsBindEntity> getUsed(String memberId) {
            if (TextUtils.isEmpty(memberId) || !this.loadDataEnd || !this.used.containsKey(memberId)) {
                return new ArrayList<>();
            }
            return this.used.get(memberId);
        }

        @Override
        public List<CouponsBindEntity> getOverDue(String memberId) {
            if (TextUtils.isEmpty(memberId) || !this.loadDataEnd || !this.overDue.containsKey(memberId)) {
                return new ArrayList<>();
            }
            return this.overDue.get(memberId);
        }

        @Override
        public List<CouponsBindEntity> getNearOverDue(String memberId) {
            if (TextUtils.isEmpty(memberId) || !this.loadDataEnd || !this.nearOverDue.containsKey(memberId)) {
                return new ArrayList<>();
            }
            return this.nearOverDue.get(memberId);
        }

        @Override
        public List<CouponsBindEntity> getCoupons(String memberId, CouponsMold mold) {
            switch (mold) {
                case USEABLE:
                    return this.getUseable(memberId);
                case USED:
                    return this.getUsed(memberId);
                case OVERDUE:
                    return this.getOverDue(memberId);
                case UNKNOWN:
            }
            return new ArrayList<>();
        }

        @Override
        public List<CouponsBindEntity> getAllNearOverDue() {
            if (!this.loadDataEnd) {
                return new ArrayList<>();
            }
            List<CouponsBindEntity> array = new ArrayList<>();
            for (List<CouponsBindEntity> coupons : this.nearOverDue.values()) {
                if (Util.isEmpty(coupons)) {
                    continue;
                }
                array.addAll(coupons);
            }
            return array;
        }

        @Override
        public boolean hasNearOverDue() {
            if (!this.loadDataEnd) {
                return false;
            }
            if (Util.isEmpty(this.nearOverDue)) {
                return false;
            }
            for (List<CouponsBindEntity> coupons : this.nearOverDue.values()) {
                if (!Util.isEmpty(coupons)) {
                    return true;
                }
            }
            return false;
        }
    }

    public interface OnCouponsDataListener {
        void onLoadCouponsSuccess();
    }

    private static class CouponsCollection extends HashMap<String, List<CouponsBindEntity>> {

        private static final long serialVersionUID = -4475954540859586969L;

        public void put(String key, CouponsBindEntity coupons) {
            List<CouponsBindEntity> array;
            if (!this.containsKey(key) || (array = this.get(key)) == null) {
                array = new ArrayList<>();
                this.put(key, array);
            }
            if (array.contains(coupons)) {
                return;
            }
            array.add(coupons);
        }
    }

    public enum CouponsMold {
        USEABLE("useable", "可用的"), USED("used", "已使用的"),
        OVERDUE("overDue", "过期的"), UNKNOWN("unknown", "未知的");
        public String code;
        public String name;

        CouponsMold(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static CouponsMold find(String code) {
            for (CouponsMold mold : CouponsMold.values()) {
                if (TextUtils.equals(code, mold.code))
                    return mold;
            }
            return UNKNOWN;
        }
    }
}
