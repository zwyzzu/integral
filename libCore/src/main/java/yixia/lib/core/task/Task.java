package yixia.lib.core.task;

import java.util.concurrent.Future;

import yixia.lib.core.util.Logger;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/7 下午3:17
 * 修改时间：2017/4/7 下午3:17
 * Description: 异步任务task
 */
@SuppressWarnings("unused")
public abstract class Task<Params> implements Runnable {

    private Future<?> future;
    private Params[] params;

    @Override
    public void run() {
        try {
            this.doInBackground(params);
        } catch (Throwable throwable) {
            Logger.e("Task.run", throwable);
        }
    }

    protected abstract void doInBackground(Params... params);

    public final Task<Params> execute(Params... params) {
        try {
            this.params = params;
            XExecutor executor = XExecutor.getInstance();
            future = executor.submit(this);
            return this;
        } catch (Throwable throwable) {
            Logger.e("Task.execute", throwable);
        }
        return this;
    }

    public final void cancel() {
        try {
            if (!this.isCancelled()) {
                future.cancel(true);
            }
        } catch (Throwable throwable) {
            Logger.e("Task.cancel", throwable);
        }
    }

    private boolean isCancelled() {
        return future == null || future.isCancelled();
    }
}
