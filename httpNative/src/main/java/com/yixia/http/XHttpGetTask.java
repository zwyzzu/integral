package com.yixia.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import yixia.lib.core.util.Logger;

public class XHttpGetTask extends XHttpTask {
    public XHttpGetTask(String url, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        super(url, maxRetryCount, handler);
    }

    public XHttpGetTask(String hostPath, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        super(hostPath, params, maxRetryCount, handler);
    }

    public XHttpGetTask(String host, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        super(host, path, params, maxRetryCount, handler);
    }

    public XHttpGetTask(String host, int port, String path, XHttpParams params, int maxRetryCount, XHttpHandler handler) throws XHttpException {
        super(host, port, path, params, maxRetryCount, handler);
    }

    protected void request() throws XHttpException {
        //the response content reading input stream
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            //record the request start time point
            long startTime = System.currentTimeMillis();

            //prepare the url connection
            URL url = new URL(request.getUrlString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);

            this.setRequestProperties();

            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            conn.setRequestProperty("Connection", "Close");

            //get the response code and message
            int code = conn.getResponseCode();
            try {
                //判定是否会进行302||301重定向
                while ((code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM) && (System.currentTimeMillis() - startTime <= CONNECT_TIMEOUT)) {
                    url = new URL(conn.getHeaderField("Location"));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setInstanceFollowRedirects(true);
                    conn.setConnectTimeout(CONNECT_TIMEOUT);
                    conn.setReadTimeout(READ_TIMEOUT);

                    this.setRequestProperties();

                    conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
                    conn.setRequestProperty("Connection", "Close");

                    code = conn.getResponseCode();
                }
            } catch (Throwable e) {
                Logger.e("302 Redirects", e);
            }

            String msg = conn.getResponseMessage();
            String encoding = conn.getContentEncoding();
            Map<String, List<String>> headers = conn.getHeaderFields();

            //read the response content
            if (encoding != null && encoding.toLowerCase().matches("gzip")) {
                in = new GZIPInputStream(new BufferedInputStream(conn.getInputStream()));
            } else {
                in = new BufferedInputStream(conn.getInputStream());
            }

            //for read response content from remote host
            byte[] buf = new byte[SIZE_READ_BUFFER];
            baos = new ByteArrayOutputStream();

            //read the response content
            int sz = in.read(buf);
            while (sz != -1) {
                baos.write(buf, 0, sz);
                sz = in.read(buf);
            }

            //record the request end time point
            long endTime = System.currentTimeMillis();

            //make the response object
            this.response = new XHttpResponse(code, msg, headers, baos.toByteArray(), endTime - startTime);

        } catch (Exception e) {
            throw new XHttpException(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (baos != null) {
                    baos.close();
                }

                //close the connection
                conn.disconnect();
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}
