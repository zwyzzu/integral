package yixia.lib.core.task;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/7 下午3:10
 * 修改时间：2017/4/7 下午3:10
 * Description: 线程池执行异步任务
 */
class XExecutor {
    private static XExecutor instance = null;

    public static XExecutor getInstance() {
        if (instance == null) {
            instance = new XExecutor();
        }
        return instance;
    }

    private ThreadPoolExecutor executor = null;

    private XExecutor() {
        this.initExecutor();
    }

    private void initExecutor() {
        if (this.executor != null)
            return;
        int maximumPoolSize = Math.max(2,
                Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
        this.executor = new ThreadPoolExecutor(0, maximumPoolSize, 30, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new MThreadFactory());
        this.executor.allowCoreThreadTimeOut(true);
        this.executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    }

    Future<?> submit(Runnable task) {
        if (this.executor == null)
            this.initExecutor();
        if (this.executor != null) {
            return this.executor.submit(task);
        }
        return null;
    }

    private class MThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "XExecutor #" + mCount.getAndIncrement());
        }
    }
}
