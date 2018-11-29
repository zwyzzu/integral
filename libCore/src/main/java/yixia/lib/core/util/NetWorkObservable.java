package yixia.lib.core.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.SoftReference;
import java.util.Observable;
import java.util.Observer;

import yixia.lib.core.task.Task;

/**
 * Author: zhangwy(张维亚)
 * Email:  zhangweiya@yixia.com
 * 创建时间:2017/4/28 下午3:52
 * 修改时间:2017/4/28 下午3:52
 * Description:监听网络变化
 */
@SuppressWarnings("unused")
public class NetWorkObservable extends Observable implements Handler.Callback {
    private static NetWorkObservable instance;

    public static NetWorkObservable getInstance() {
        if (instance == null) {
            synchronized (NetWorkObservable.class) {
                if (instance == null) {
                    instance = new NetWorkObservable();
                }
            }
        }
        return instance;
    }

    public static NetWorkObservable initialize(Context context) {
        return getInstance().init(context.getApplicationContext());
    }

    /******************************************************************************/
    private boolean initialized = false;
    private Handler handler = new Handler(Looper.getMainLooper(), this);
    private static final int WHAT_NETWORK_CHANGE = 100;
    private Device.NetType currentNetType;

    private NetWorkObservable() {
    }

    private void change(Context context) {
        if (context == null)
            return;
        new Task<Context>() {
            @Override
            protected void doInBackground(Context... params) {
                if (Util.isEmpty(params))
                    return;
                sendMsg(Device.NetWork.netWorkType(params[0]));
            }
        }.execute(context.getApplicationContext());
    }

    public NetWorkObservable init(Context context) {
        if (initialized)
            return this;
        initialized = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.getApplicationContext().registerReceiver(new NetWorkReceiver(this), filter);
        this.change(context);
        return this;
    }

    public void register(NetWorkObserver observer) {
        if (observer == null)
            return;

        if (this.currentNetType != null)
            observer.update(this, this.currentNetType);

        this.addObserver(observer);
    }

    public void unRegister(NetWorkObserver observer) {
        this.deleteObserver(observer);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == WHAT_NETWORK_CHANGE) {
            this.notifyAll((Device.NetType) msg.obj);
            return true;
        }
        return false;
    }

    private void notifyAll(Device.NetType netType) {
        this.currentNetType = netType;
        this.setChanged();
        this.notifyObservers(netType);
    }

    private final class NetWorkReceiver extends BroadcastReceiver {

        private SoftReference<NetWorkObservable> reference;

        NetWorkReceiver(NetWorkObservable netChange) {
            this.reference = new SoftReference<>(netChange);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            NetWorkObservable netChange;
            if (this.reference == null || (netChange = reference.get()) == null) {
                try {
                    context.unregisterReceiver(this);
                } catch (Exception e) {
                    Logger.e("NetWorkReceiver.unregisterReceiver", e);
                }
                return;
            }
            netChange.change(context);
        }
    }

    private void sendMsg(Device.NetType netType) {
        if (this.handler != null) {
            this.handler.sendMessage(this.handler.obtainMessage(WHAT_NETWORK_CHANGE, netType));
        }
    }

    public abstract static class NetWorkObserver implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            this.updateNetWork((Device.NetType) arg);
        }

        public abstract void updateNetWork(Device.NetType netType);
    }
}
