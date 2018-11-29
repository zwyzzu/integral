package yixia.lib.core.exception;

/**
 * Author: zhangwy(张维亚)
 * Email:  zhangweiya@yixia.com
 * 创建时间:2017/6/23 下午5:32
 * 修改时间:2017/6/23 下午5:32
 * Description: 包含errorCode的异常
 */
@SuppressWarnings("unused")
public class CodeException extends Exception {

    public final int code;

    public CodeException(int code) {
        super();
        this.code = code;
    }

    public CodeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public CodeException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public CodeException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
