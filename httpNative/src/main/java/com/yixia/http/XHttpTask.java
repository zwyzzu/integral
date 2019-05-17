package com.yixia.http;

import android.text.TextUtils;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Future;


public abstract class XHttpTask implements Runnable {
    final int READ_TIMEOUT = 10000; //10s
    final int SIZE_READ_BUFFER = 16 * 1024;
    final int CONNECT_TIMEOUT = 10000; //10s
    int maxRetryCount = 1;
    private boolean stop = false;
    private Future<?> future;

    protected XHttpRequest request = null;
    protected XHttpResponse response = null;
    protected XHttpHandler handler = null;

    HttpURLConnection conn = null;

    protected HashMap<String, String> header = new HashMap<>();

    public XHttpTask(String url, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(url);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public XHttpTask(String hostPath, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(hostPath, params);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public XHttpTask(String host, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(host, path, params);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public XHttpTask(String host, int port, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(host, port, path, params);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public XHttpTask(String remoteFile, String localDir, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(remoteFile, localDir);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public XHttpTask(String remoteFile, String localDir, String fileName, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        this.request = new XHttpRequest(remoteFile, localDir, fileName);
        this.maxRetryCount = maxRetryCount;
        this.handler = handler;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    protected abstract void request() throws XHttpException;

    private void query() throws Throwable {
        boolean success = false;
        while (this.maxRetryCount-- > 0 && !success) {
            if (isStop())
                break;
            try {
                if (this.handler != null)
                    this.handler.onStart(this.request);

                this.request();
                success = true;
            } catch (Throwable e) {
                if (this.maxRetryCount <= 0) {
                    throw e;
                } else {
                    if (this.handler != null) {
                        String msg = e.getMessage();
                        if (msg == null) {
                            msg = "null, unknown error";
                        }
                        this.handler.onRetry(this.request, msg);
                    }
                }
            }
        }
    }

    private void inform() throws XHttpException {
        if (this.handler == null) {
            return;
        }
        if (this.stop) {
            this.handler.onStop(request);
            return;
        }

        if (this.response == null) {
            throw new XHttpException("null response");
        }

        if (this.response.getCode() >= HttpURLConnection.HTTP_OK && this.response.getCode() < 400) {
            this.handler.onSuccess(this.request, this.response);
        } else {
            this.handler.onFailed(this.request, this.response);
        }
    }

    public void run() {
        try {
            this.query();

            this.inform();

        } catch (Throwable e) {
            if (this.handler != null)
                this.handler.onError(this.request, e.getMessage());
        }
    }

    final boolean isStop() {
        return stop || (stop = (future != null && future.isCancelled()));
    }

    final void setRequestProperties() {
        if (Util.isEmpty(header) || this.conn == null)
            return;

        for (Entry<String, String> entry : this.header.entrySet()) {
            if (TextUtils.isEmpty(entry.getKey()))
                continue;
            conn.setRequestProperty(entry.getKey(), TextUtils.isEmpty(entry.getValue()) ? "" : entry.getValue());
        }
    }

    public void setUserAgent(String userAgent) {
        final String UserAgentKey = "User-Agent";
        if (TextUtils.isEmpty(userAgent)) {
            if (this.header.containsKey(UserAgentKey))
                this.header.remove(UserAgentKey);
        } else {
            this.header.put(UserAgentKey, userAgent);
        }
    }

    public void putHeaders(HashMap<String, String> header) {
        if (Util.isEmpty(header))
            return;
        this.header.putAll(header);
    }
}