package com.zhangwy.common;

/**
 * Created by zhangwy on 2019/4/29.
 * Updated by zhangwy on 2019/4/29.
 * Description TODO
 */
public class ErrorMessage {
    public final static int CODE_HTTPPARAMS = 15001;
    // native error 401~599
    // 请求前判断参数为空
    public final static int ERROR_CODE_USER_EMPTY = 401;
    public final static int ERROR_CODE_TOKEN_EMPTY = 402;
    // 请求服务器时直接报错
    public final static int ERROR_CODE_LOGIN_STATUS = 451;
    public final static int ERROR_CODE_LOGIN_FUN = 452;
    public final static int ERROR_CODE_LOGIN_WEIXIN = 453;
    public final static int ERROR_CODE_LOGIN_TENCENT = 454;
    public final static int ERROR_CODE_LOGIN_SINA = 455;
    public final static int ERROR_CODE_USERINFO = 456;
    public final static int ERROR_CODE_VIPINFO = 457;
    public final static int ERROR_CODE_ORDER_STATUS = 458;
    public final static int ERROR_CODE_DASREQ_NULL = 459;
    //加密过程中
    public final static int ERROR_CODE_ENCRYPT = 551;
    public final static int ERROR_CODE_ENCRYPT_ALGORITHM = 552;
    public final static int ERROR_CODE_ENCRYPT_CIPHERTEXT_NULL = 553;
    public final static int ERROR_CODE_BYTE2HEX_BYTE_NULL = 554;
    public final static int ERROR_CODE_BYTE2HEX_BYTE_EMPTY = 555;

    // funshion network error601~799
    // 返回failed
    public final static int ERROR_CODE_NETWORK = 601;
    public final static int ERROR_CODE_LOGIN = 602;
    public final static int ERROR_CODE_VIP = 603;
    public final static int ERROR_CODE_VERIFY_ORDER = 604;
    public final static int ERROR_CODE_ENTITY_NULL = 621;
    public final static int ERROR_CODE_USERNAME_NOT_REGISTERED = 622;
    public final static int ERROR_CODE_WRONG_PASSWORD = 623;
    public final static int ERROR_CODE_ACCOUNT_LOCKOUT = 624;
    public final static int ERROR_CODE_TOKEN_INVALID = 625;

    // 返回success但缺少参数
    public final static int ERROR_CODE_SRESP_NULL = 651;
    public final static int ERROR_CODE_SIGN_EMPTY = 652;// 数字签名为空
    public final static int ERROR_CODE_SIGN_INVALID = 653;// 数字签名无效
    public final static int ERROR_CODE_USER_NULL = 654;
    public final static int ERROR_CODE_USER_INVALID = 655;
    public final static int ERROR_CODE_VIP_NULL = 656;
    public final static int ERROR_CODE_VIP_INVALID = 657;
    //other error 801~999
    //第三方登录需要
    public final static int ERROR_CODE_NOT_INSTALL = 801;//未安装
    public final static int ERROR_CODE_APPKEY_EMPTY = 802;//
    public final static int ERROR_CODE_USER_CANCEL = 803;//
    public final static int ERROR_CODE_AUTH_DENY = 804;//被拒
    public final static int ERROR_CODE_AUTH_FAILED = 805;//授权失败
    public final static int ERROR_CODE_SENT_FAILED = 806;//发送失败
    //支付
    public final static int ERROR_CODE_PAY_INFO_EMPTY = 821;//支付信息为空
    public final static int ERROR_CODE_PAY_WAITING = 822;
    public final static int ERROR_CODE_PAY_FAILED = 823;
    public final static int ERROR_CODE_PAY_REQNULL = 824;
    public final static int ERROR_CODE_PAY_COMM= 825;//签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
}
