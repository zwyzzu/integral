package com.zhangwy.integral.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.zhangwy.integral.R;
import com.zhangwy.integral.entity.CouponsBindEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Logger;
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

    public abstract void addCoupons(CouponsBindEntity entity);

    public abstract void useCoupons(Context context, CouponsBindEntity entity);

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
                        verifyCoupons(coupons);
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

        private boolean verifyCoupons(CouponsBindEntity coupons) {
            if (coupons == null) {
                return false;
            }
            if (coupons.used()) {
                this.used.put(coupons.getBind(), coupons);
            } else if (coupons.overdue()){
                this.overDue.put(coupons.getBind(), coupons);
            } else {
                this.useable.put(coupons.getBind(), coupons);
                if (coupons.nearOverDue()) {
                    this.nearOverDue.put(coupons.getBind(), coupons);
                }
            }
            return true;
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
                case NEAROVERDUE:
                    if (TextUtils.isEmpty(memberId)) {
                        return this.getAllNearOverDue();
                    }
                    return this.getNearOverDue(memberId);
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

        @Override
        public void addCoupons(CouponsBindEntity entity) {
            if (this.verifyCoupons(entity)) {
                this.notifyObserver();
            }
        }

        @Override
        public void useCoupons(Context context, CouponsBindEntity entity) {
            if (entity == null) {
                return;
            }

            try {
                IDataManager.getInstance().useCoupons(entity.getBind(), entity.getId());
                this.removeItem(entity, this.useable.get(entity.getBind()));
                this.removeItem(entity, this.nearOverDue.get(entity.getBind()));
                this.used.put(entity.getBind(), entity);
                this.notifyObserver();
            } catch (CodeException e) {
                Logger.d("useCoupons", e);
                int res;
                switch (e.code) {
                    case IDataCode.MEMBER_NOFOUND:
                        res = R.string.error_nofound_member;
                        break;
                    case IDataCode.COUPONS_NOFOUND:
                        res = R.string.error_nofound_coupons;
                        break;
                    case IDataCode.COUPONS_UNAVAILABLE:
                        res = R.string.error_unavailable_coupons;
                        break;
                    default:
                        res = R.string.error_use_coupons;
                        break;
                }
                if (context != null) {
                    Toast.makeText(context, res, Toast.LENGTH_LONG).show();
                }
            }
        }

        private void removeItem(CouponsBindEntity entity, List<CouponsBindEntity> array) {
            if (Util.isEmpty(array)) {
                return;
            }
            if (array.contains(entity)) {
                array.remove(entity);
                return;
            }
            for (CouponsBindEntity coupons : array) {
                if (coupons == null) {
                    continue;
                }
                if (TextUtils.equals(coupons.getId(), entity.getId())) {
                    array.remove(coupons);
                    break;
                }
            }
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
        OVERDUE("overDue", "过期的"), NEAROVERDUE("nearOverDue", "临近过期的"),
        UNKNOWN("unknown", "未知的");
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
