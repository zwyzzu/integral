package com.zhangwy.integral.data;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangwy.integral.R;
import com.zhangwy.integral.entity.BookingBindEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2020-03-29.
 * Updated by zhangwy on 2020-04-12.
 * Description
 */
@SuppressWarnings({"unused"})
public abstract class IBookingManager {
    private static IBookingManager instance;

    public static IBookingManager getInstance() {
        if (instance == null) {
            synchronized (IBookingManager.class) {
                if (instance == null) {
                    instance = new IBookingManager.IBookingManagerImpl();
                }
            }
        }
        return instance;
    }

    public abstract void register(OnBookingDataListener listener);

    public abstract void unRegister(OnBookingDataListener listener);

    public abstract List<BookingBindEntity> getOrdered(String memberId);

    public abstract List<BookingBindEntity> getUnOrder(String memberId);

    public abstract List<BookingBindEntity> getInvalid(String memberId);

    public abstract void addBooking(BookingBindEntity entity);

    public abstract void order(Context context, BookingBindEntity entity);

    public abstract void invalid(Context context, BookingBindEntity entity);

    private static final class IBookingManagerImpl extends IBookingManager {
        private boolean loadDataEnd = false;
        private ViewGroup root = null;
        private List<OnBookingDataListener> listeners = new ArrayList<>();
        private BookingCollection unOrderBookings = new BookingCollection();
        private BookingCollection orderedBookings = new BookingCollection();
        private BookingCollection invalidBookings = new BookingCollection();

        private IBookingManagerImpl() {
            this.loadData();
        }

        @SuppressLint("StaticFieldLeak")
        private void loadData() {
            AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
                @Override
                protected Boolean doInBackground(String... strings) {
                    List<BookingBindEntity> all = IDataManager.getInstance().getMemberBookings();
                    for (BookingBindEntity coupons : all) {
                        verifyBooking(coupons);
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
                    notifyObserver("");
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }
            };
            asyncTask.execute();
        }

        private boolean verifyBooking(BookingBindEntity booking) {
            if (booking == null) {
                return false;
            }
            if (booking.isInvalid()) {
                this.invalidBookings.put(booking.getBind(), booking);
            } else if (booking.isOrdered()) {
                this.orderedBookings.put(booking.getBind(), booking);
            } else {
                this.unOrderBookings.put(booking.getBind(), booking);
            }
            return true;
        }

        @Override
        public void register(OnBookingDataListener listener) {
            if (this.listeners.contains(listener)) {
                return;
            }
            if (this.loadDataEnd) {
                listener.onBookingDataChanged("");
            }
            this.listeners.add(listener);
        }

        @Override
        public void unRegister(OnBookingDataListener listener) {
            if (listener == null || !this.listeners.contains(listener)) {
                return;
            }
            this.listeners.remove(listener);
        }

        @Override
        public List<BookingBindEntity> getOrdered(String memberId) {
            return this.getBookingsByMember(this.orderedBookings, memberId);
        }

        @Override
        public List<BookingBindEntity> getUnOrder(String memberId) {
            return this.getBookingsByMember(this.unOrderBookings, memberId);
        }

        @Override
        public List<BookingBindEntity> getInvalid(String memberId) {
            return this.getBookingsByMember(this.invalidBookings, memberId);
        }

        private List<BookingBindEntity> getBookingsByMember(BookingCollection collection, String memberId) {
            if (!this.loadDataEnd || collection == null) {
                return new ArrayList<>();
            }
            if (!TextUtils.isEmpty(memberId) && collection.containsKey(memberId)) {
                return collection.get(memberId);
            }
            return collection.getAll();
        }

        @Override
        public void addBooking(BookingBindEntity entity) {
            if (this.verifyBooking(entity)) {
                this.notifyObserver(entity.getBind());
            }
        }

        @Override
        public void order(Context context, BookingBindEntity entity) {
            if (entity == null || context == null) {
                return;
            }

            View root = LayoutInflater.from(context).inflate(R.layout.dialog_booking_order, this.root);
            TextView bookers = root.findViewById(R.id.bookingOrderBookersValue);
            TextView created = root.findViewById(R.id.bookingOrderCreateTimeValue);
            TextView address = root.findViewById(R.id.bookingOrderAddressValue);
            TextView description = root.findViewById(R.id.bookingOrderDescriptionValue);
            View unAgreed = root.findViewById(R.id.bookingOrderUnAgreed);
            View agreed = root.findViewById(R.id.bookingOrderAgreed);
            bookers.setText(entity.getBindName());
            created.setText(TimeUtil.dateMilliSecond2String(entity.getCreateTime(), TimeUtil.PATTERN_DATE));
            address.setText(entity.getAddressText());
            String descContent = entity.getText() + "  " + entity.getDesc();
            description.setText(descContent);
            Dialog dialog = WindowUtil.createAlertDialog(context, 0, root, null, null);
            if (dialog == null) {
                return;
            }
            unAgreed.setOnClickListener(v -> dialog.dismiss());
            agreed.setOnClickListener(v -> {
                dialog.dismiss();
                orderImpl(context, entity);
            });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        private void orderImpl(Context context, BookingBindEntity entity) {
            try {
                if (IDataManager.getInstance().orderBooking(entity.getBind(), entity.getId())) {
                    entity.setOrderTime(System.currentTimeMillis());
                    this.removeItem(entity, this.unOrderBookings.get(entity.getBind()));
                    this.orderedBookings.put(entity.getBind(), entity);
                    this.notifyObserver(entity.getBind());
                    String text = entity.getBookingString();
                    if (!TextUtils.isEmpty(text)) {
                        Util.copy2Clipboard(context, text);
                        Toast.makeText(context, R.string.member_address_export_over, Toast.LENGTH_LONG).show();
                    }
                }
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

        @Override
        public void invalid(Context context, BookingBindEntity entity) {
            if (entity == null || context == null) {
                return;
            }

            View root = LayoutInflater.from(context).inflate(R.layout.dialog_booking_invalid, this.root);
            TextView bookers = root.findViewById(R.id.bookingInvalidBookersValue);
            TextView created = root.findViewById(R.id.bookingInvalidCreateTimeValue);
            TextView address = root.findViewById(R.id.bookingInvalidAddressValue);
            TextView description = root.findViewById(R.id.bookingInvalidDescriptionValue);
            View unAgreed = root.findViewById(R.id.bookingInvalidUnAgreed);
            View agreed = root.findViewById(R.id.bookingInvalidAgreed);
            bookers.setText(entity.getBindName());
            created.setText(TimeUtil.dateMilliSecond2String(entity.getCreateTime(), TimeUtil.PATTERN_DATE));
            address.setText(entity.getAddressText());
            String descContent = entity.getText() + "  " + entity.getDesc();
            description.setText(descContent);
            Dialog dialog = WindowUtil.createAlertDialog(context, 0, root, null, null);
            if (dialog == null) {
                return;
            }
            unAgreed.setOnClickListener(v -> dialog.dismiss());
            agreed.setOnClickListener(v -> {
                dialog.dismiss();
                invalidImpl(context, entity);
            });
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        private void invalidImpl(Context context, BookingBindEntity entity) {
            try {
                if (IDataManager.getInstance().invalidBooking(entity.getBind(), entity.getId())) {
                    entity.setInvalidTime(System.currentTimeMillis());
                    this.removeItem(entity, this.unOrderBookings.get(entity.getBind()));
                    this.invalidBookings.put(entity.getBind(), entity);
                    this.notifyObserver(entity.getBind());
                }
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

        private void notifyObserver(final String memberId) {
            List<OnBookingDataListener> array = new ArrayList<>(this.listeners);
            for (OnBookingDataListener listener : array) {
                listener.onBookingDataChanged(memberId);
            }
        }

        private void removeItem(BookingBindEntity entity, List<BookingBindEntity> array) {
            if (Util.isEmpty(array)) {
                return;
            }
            if (array.contains(entity)) {
                array.remove(entity);
                return;
            }
            for (BookingBindEntity booking : array) {
                if (booking == null) {
                    continue;
                }
                if (TextUtils.equals(booking.getId(), entity.getId())) {
                    array.remove(booking);
                    break;
                }
            }
        }
    }

    public interface OnBookingDataListener {
        void onBookingDataChanged(String memberId);
    }

    public static class BookingCollection extends HashMap<String, List<BookingBindEntity>> {
        private static final long serialVersionUID = -4475954540859586969L;

        public void put(String key, BookingBindEntity coupons) {
            List<BookingBindEntity> array;
            if (!this.containsKey(key) || (array = this.get(key)) == null) {
                array = new ArrayList<>();
                this.put(key, array);
            }
            if (array.contains(coupons)) {
                return;
            }
            array.add(coupons);
        }

        public List<BookingBindEntity> getAll() {
            List<BookingBindEntity> entities = new ArrayList<>();
            for (List<BookingBindEntity> array : this.values()) {
                if (Util.isEmpty(array)) {
                    continue;
                }
                entities.addAll(array);
            }
            return entities;
        }
    }
}
