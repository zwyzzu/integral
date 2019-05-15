package com.zhangwy.http;

import com.zhangwy.common.ErrorMessage;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import yixia.lib.core.exception.CodeException;
import yixia.lib.core.util.Util;

/**
 * class for packaging the request parameters.
 */
@SuppressWarnings("unused")
public class HttpParams {
    //array list for holding the @Param objects
    private List<Param> params = new ArrayList<>();

    public static HttpParams newParams() {
        return new HttpParams();
    }

    /**
     * put a string <key, value> pair value to the request parameters
     *
     * @param key:   parameter key
     * @param value: parameter value
     * @return current @RequestParams object
     */
    public HttpParams put(String key, String value) {
        if (key != null) {
            if (value == null) {
                params.add(new Param(key, ""));
            } else {
                params.add(new Param(key, value));
            }
        }
        return this;
    }

    /**
     * put a string <key, value> pair value to the request parameters
     *
     * @param params parameters
     * @return current @RequestParams object
     */
    public HttpParams put(Map<String, String> params) {
        if (params == null)
            return this;

        for (String key :params.keySet()) {
            this.put(key, params.get(key));
        }
        return this;
    }

    public String get(String key) {
        try {
            for (Param param : this.params) {
                if (param.getKey().equals(key)) {
                    return param.getValue();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return null;
    }

    public void replace(String key, String value) {
        try {
            for (Param param : this.params) {
                if (param.getKey().equals(key)) {
                    param.value = value;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public boolean contain(String key) {
        try {
            for (Param param : this.params) {
                if (param.getKey().equals(key)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        return false;
    }

    /**
     * add the input params to the head of this params
     * @return this
     */
    public HttpParams mergeToHead(HttpParams params) {
        if (params != null)
            this.params.addAll(0, params.getAll());
        return this;
    }

    /**
     * add the input params to the end of this params
     * @return this
     */
    public HttpParams mergeToEnd(HttpParams params) {
        if (params != null)
            this.params.addAll(params.getAll());
        return this;
    }

    private List<Param> getAll() {
        return this.params;
    }

    /**
     * encode all the parameters to an request string like: key1=value1&key2=value2&...
     * @return encoded url
     * @throws CodeException 异常
     */
    public String encode() throws CodeException {
        return encode("&", "=");
    }

    /**
     * encode all the parameters to an request string like: key1=value1&key2=value2&...
     * @param connector 连接符
     * @param assignment 赋值符
     * @return encoded url
     * @throws CodeException 异常
     */
    public String encode(String connector, String assignment) throws CodeException {
        final String DEFAULT_ENC = "UTF-8";
        try {
            connector = connector == null ? "" : connector;
            assignment = assignment == null ? "" : assignment;
            StringBuilder buffer = new StringBuilder();
            Iterator<Param> iterator = params.iterator();
            while (iterator.hasNext()) {
                Param param = iterator.next();
                buffer.append(URLEncoder.encode(param.getKey(), DEFAULT_ENC));
                buffer.append(assignment);
                buffer.append(URLEncoder.encode(param.getValue(), DEFAULT_ENC));
                if (iterator.hasNext())
                    buffer.append(connector);
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new CodeException(ErrorMessage.CODE_HTTPPARAMS, e.getMessage());
        }
    }

    public boolean isEmpty() {
        return Util.isEmpty(this.params);
    }

    public void clear() {
        if (this.params != null)
            this.params.clear();
    }

    /**
     * <key, value> pair for request parameter
     */
    private static final class Param {
        private String key;
        private String value;

        Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}