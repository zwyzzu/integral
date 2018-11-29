package yixia.lib.core.cipher;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import yixia.lib.core.util.FileUtil;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/5 下午4:19
 * 修改时间：2017/4/5 下午4:19
 * Description: MD5加密封装
 * md5Encode(String src)对字符串进行加密
 * md5Encode(byte[] src)对byte数组加密
 * md5EncodeFile(String file)对文件进行加密
 * md5EncodeFile(File file)对文件进行加密
 */
@SuppressWarnings("unused")
public class MD5 {

    /**
     * md5加密文件 生成32位md5码
     * 不使用NIO的计算MD5，不会造成文件被锁死
     *
     * @param file 要加密的filename
     * @return 加密后的值
     */
    public static String md5EncodeFile(String file) {
        return new MD5Impl(file, true).hexString();
    }

    /**
     * md5加密文件 生成32位md5码
     * 不使用NIO的计算MD5，不会造成文件被锁死
     *
     * @param file 要加密的file
     * @return 加密后的值
     */
    public static String md5EncodeFile(File file) {
        return new MD5Impl(file).hexString();
    }

    /**
     * MD5加密字符串 生成32位md5码
     *
     * @return 返回32位md5码
     */
    public static String md5Encode(String src) {
        return new MD5Impl(src, false).hexString();
    }

    public static String md5Encode(byte[] src) {
        return new MD5Impl(src).hexString();
    }

    /***************************************************************************************
     * md5 类实现了RSA Data Security, Inc.在提交给IETF 的RFC1321中的MD5 message-digest 算法  *
     ***************************************************************************************/
    private static class MD5Impl{
        private final char[] digestValues = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        private byte[] digest;

        private MD5Impl(byte[] bytes) {
            this.encode(bytes);
        }

        private MD5Impl(String string, boolean file) {
            if (TextUtils.isEmpty(string)) {
                return;
            }
            if (file) {
                this.encode(new File(string));
            } else {
                this.encode(string.getBytes());
            }
        }

        private MD5Impl(File file) {
            this.encode(file);
        }

        private void encode(byte[] bytes) {
            if (Util.isEmpty(bytes)) {
                return;
            }
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                this.digest = md5.digest(bytes);
            } catch (Throwable throwable) {
                Logger.d("encode", throwable);
            }
        }

        private void encode(File file) {
            if (!FileUtil.fileExists(file)) {
                return;
            }
            FileInputStream inputStream = null;
            for (int i = 0; i < 3; i++) {
                try {
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    byte buffer[] = new byte[1024];
                    int len;
                    inputStream = new FileInputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        md5.update(buffer, 0, len);
                    }
                    this.digest = md5.digest();
                    break;
                } catch (Throwable throwable) {
                    Logger.d("encode", throwable);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            Logger.d("close.InputStream", e);
                        }
                    }
                }
            }
        }

        String hexString() {
            if (Util.isEmpty(this.digest)) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            for (byte b : this.digest) {
                builder.append(byteHEX(b));
            }
            return builder.toString();
        }

        private String byteHEX(byte ib) {
            char[] ob = new char[2];
            ob[0] = digestValues[(ib >>> 4) & 0X0F];
            ob[1] = digestValues[ib & 0X0F];
            return new String(ob);
        }
    }
}
