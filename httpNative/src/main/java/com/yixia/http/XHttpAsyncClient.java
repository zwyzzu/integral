package com.yixia.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class XHttpAsyncClient implements XHttpClient {
    private final int DEFAULT_MAX_RETRY_COUNT = 1; //default max retry count
    private ThreadPoolExecutor executorService;
    private String userAgent;
    private HashMap<String, String> headers;

    XHttpAsyncClient(boolean single) {
        this.init(single);
    }

    XHttpAsyncClient(int maximumPoolSize) {
        if (maximumPoolSize <= 1) {
            this.init(true);
        } else {
            this.init(maximumPoolSize / 2, maximumPoolSize);
        }
    }

    private void init(boolean single) {
        int corePoolSize = 1;
        int maximumPoolSize = 1;
        if (!single) {
            final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
            corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
            maximumPoolSize = CPU_COUNT * 2 + 1;
        }
        this.init(corePoolSize, maximumPoolSize);
    }

    private void init(int corePoolSize, int maximumPoolSize) {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(512), new MThreadFactory());
            this.executorService.allowCoreThreadTimeOut(true);
        }
    }

    @Override
    public Future<?> get(String url, XHttpHandler handler) throws XHttpException {
        return this.get(url, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String hostPath, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.get(hostPath, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String host, String path, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.get(host, path, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String host, int port, String path, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.get(host, port, path, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String remoteFile, String localDir, XHttpHandler handler) throws XHttpException {
        return this.get(remoteFile, localDir, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String remoteFile, String localDir, String fileName, boolean append, XHttpHandler handler) throws XHttpException {
        return this.get(remoteFile, localDir, fileName, append, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> post(String url, XHttpHandler handler) throws XHttpException {
        return this.post(url, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> post(String hostPath, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.post(hostPath, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> post(String host, String path, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.post(host, path, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> post(String host, int port, String path, XHttpParams params, XHttpHandler handler) throws XHttpException {
        return this.post(host, port, path, params, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> post(String remotePath, String localFile, XHttpHandler handler) throws XHttpException {
        return this.post(remotePath, localFile, DEFAULT_MAX_RETRY_COUNT, handler);
    }

    @Override
    public Future<?> get(String url, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpGetTask(url, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String hostPath, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpGetTask(hostPath, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String host, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpGetTask(host, path, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String host, int port, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpGetTask(host, port, path, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String remoteFile, String localDir, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpFileGetTask(remoteFile, localDir, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String remoteFile, String localDir, String fileName, boolean append, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpFileGetTask(remoteFile, localDir, fileName, append, maxRetryCount, handler));
    }

    @Override
    public Future<?> get(String remoteFile, String localDir, String fileName, boolean append, boolean block, float blockSizeMb, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpFileGetTask(remoteFile, localDir, fileName, append, block, blockSizeMb, maxRetryCount, handler));
    }

    @Override
    public Future<?> post(String url, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpPostTask(url, maxRetryCount, handler));
    }

    @Override
    public Future<?> post(String hostPath, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpPostTask(hostPath, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> post(String host, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpPostTask(host, path, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> post(String host, int port, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpPostTask(host, port, path, params, maxRetryCount, handler));
    }

    @Override
    public Future<?> post(String remotePath, String localFile, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        return this.submit(new XHttpFilePostTask(remotePath, localFile, maxRetryCount, handler));
    }

    private Future<?> submit(XHttpTask task) throws XHttpException {
        if (task == null)
            throw new XHttpException("the task is null");
        {//设置headers
            if (!TextUtils.isEmpty(this.userAgent)) {
                task.setUserAgent(this.userAgent);
            }
            if (!Util.isEmpty(this.headers)) {
                task.putHeaders(headers);
            }
        }

        try {
            Future<?> future = this.executorService.submit(task);
            task.setFuture(future);
            return future;
        } catch (RejectedExecutionException e) {
            throw new XHttpException("the task cannot be accepted for execution");
        } catch (Exception e) {
            throw new XHttpException(e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (this.executorService != null) {
            this.executorService.shutdown();
            this.executorService = null;
        }
    }

    @Override
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void putHeaders(HashMap<String, String> headers) {
        if (Util.isEmpty(headers))
            return;
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.putAll(headers);
    }

    @Override
    public void putHeader(String key, String value) {
        if (TextUtils.isEmpty(key))
            return;

        if (this.headers == null) {
            this.headers = new HashMap<>();
        }

        this.headers.put(key, value);
    }

    private class MThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@Nullable Runnable runnable) {
            return new Thread(runnable, "HttpAsyncClient #" + mCount.getAndIncrement());
        }
    }
}
