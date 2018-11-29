package yixia.lib.core.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/7 上午11:20
 * 修改时间：2017/4/7 上午11:20
 * Description:
 */
@SuppressWarnings("unused")
public class FileUtil {
    public static boolean makeDirs(String dir) {
        return makeDirs(new File(dir));
    }

    public static boolean makeDirs(File file) {
        try {
            if (file.exists()) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    deleteFile(file);
                }
                file = new File(file.getAbsolutePath());
            }
            return file.mkdirs();
        } catch (Throwable throwable) {
            Logger.e("FileUtil.makeDirs", throwable);
            return false;
        }
    }

    // 去掉path中的反斜线
    public static String pathRemoveBackslash(String path) {
        if (TextUtils.isEmpty(path))
            return "";
        //
        char ch = path.charAt(path.length() - 1);
        if (ch == '/' || ch == '\\')
            return path.substring(0, path.length() - 1);
        return path;
    }

    // 在path中添加反斜线
    public static String pathAddBackslash(String path) {
        if (path == null)
            return File.separator;
        if (path.length() == 0)
            return File.separator;
        //
        char ch = path.charAt(path.length() - 1);
        if (ch == '/' || ch == '\\')
            return path;
        return path + File.separator;
    }

    /**
     * clear the files in the directory that match the regex
     *
     * @param regex: regex of the file name
     */
    public static void clear(String dir, String regex) {
        try {
            File[] files = new File(dir).listFiles();
            if (Util.isEmpty(files))
                return;
            boolean success = true;
            for (File file : files) {
                if (regex != null) {
                    if (file.getName().matches(regex)) {
                        success &= file.delete();
                    }
                } else {
                    success &= file.delete();
                }
            }
            Logger.d(String.valueOf(success));
        } catch (Exception e) {
            Logger.e("clear(String dir, String regex)", e);
        }
    }

    public static void clear(String dir, String regex, long millionSecondsAgo) {
        try {
            File[] files = new File(dir).listFiles();
            if (Util.isEmpty(files))
                return;
            boolean success = true;
            for (File file : files) {
                if (regex != null) {
                    if (file.getName().matches(regex)) {
                        if (System.currentTimeMillis() - file.lastModified() > millionSecondsAgo) {
                            success &= file.delete();
                        }
                    }
                } else {
                    if (System.currentTimeMillis() - file.lastModified() > millionSecondsAgo) {
                        success &= file.delete();
                    }
                }
            }
            Logger.d(String.valueOf(success));
        } catch (Exception e) {
            Logger.e("clear(String dir, String regex, long millionSecondsAgo)", e);
        }
    }

    public static boolean findFileByName(String dir, String fileName) {
        File dirFile = new File(dir);

        if (!dirFile.isDirectory())
            return false;

        File files[] = dirFile.listFiles();
        if (files == null || files.length == 0) return false;
        for (File file : files) {
            String name = file.getName();
            name = name.substring(0, name.length() - 4);
            if (name.equalsIgnoreCase(fileName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 文件拷贝
     * {@link IllegalAccessException} 输入||输出文件路径为空
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param srcFile 源文件
     * @param destFile 目的文件
     * @param listener 拷贝文件监听接口
     * @throws Exception 异常信息
     */
    public static void copyFile(String srcFile, String destFile, OnCopyProgressListener listener) throws Exception {
        if (TextUtils.isEmpty(srcFile) || TextUtils.isEmpty(destFile)) {
            throw new IllegalArgumentException("file name path is empty");
        }
        copyFile(new File(srcFile), new File(destFile), listener);
    }

    /**
     * 文件拷贝
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param srcFile 输入文件
     * @param destFile 输出文件
     * @param listener 拷贝文件监听接口
     * @throws Exception 异常信息
     */
    public static void copyFile(File srcFile, File destFile, OnCopyProgressListener listener) throws Exception {
        if (srcFile == null) {
            throw new NullPointerException("src file is null");
        }
        if (destFile == null) {
            throw new NullPointerException("dest file is null");
        }
        if (!FileUtil.fileExists(srcFile)) {
            throw new FileNotFoundException();
        }
        long fileSize = srcFile.length();
        if (fileSize <= 0) {
            throw new EOFException("file size is 0");
        }
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = new FileInputStream(srcFile); //读入原文件
            output = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024 * 1024];
            long length = 0;
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer);
                length += count;
                float progress = length * 100.0f / fileSize;
                if (listener != null) {
                    listener.onProgress(length, fileSize);
                }
            }
        } catch (Throwable e) {
            Logger.d("SaveVideo", e);
            throw new Exception(e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                Logger.d("close input", e);
            }
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                Logger.d("close output", e);
            }
        }
    }

    /**
     * 文件拷贝
     * {@link IllegalAccessException} 输入||输出文件路径为空
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param srcFile 源文件
     * @param destFile 目的文件
     * @throws Exception 异常信息
     */
    public static void copyFileByFileChannel(String srcFile, String destFile) throws Exception {
        if (TextUtils.isEmpty(srcFile) || TextUtils.isEmpty(destFile)) {
            throw new IllegalArgumentException("file name path is empty");
        }
        copyFileByFileChannel(new File(srcFile), new File(destFile));
    }

    /**
     * 文件拷贝
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param srcFile 输入文件
     * @param destFile 输出文件
     * @throws Exception 异常信息
     */
    public static void copyFileByFileChannel(File srcFile, File destFile) throws Exception {
        if (srcFile == null) {
            throw new NullPointerException("src file is null");
        }
        if (destFile == null) {
            throw new NullPointerException("dest file is null");
        }
        if (!FileUtil.fileExists(srcFile)) {
            throw new FileNotFoundException();
        }
        long fileSize = srcFile.length();
        if (fileSize <= 0) {
            throw new EOFException("file size is 0");
        }

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(srcFile).getChannel();
            outputChannel = new FileOutputStream(destFile).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            try {
                if (inputChannel != null) {
                    inputChannel.close();
                }
            } catch (Exception e) {
                Logger.d("close input", e);
            }
            try {
                if (outputChannel != null) {
                    outputChannel.close();
                }
            } catch (Exception e) {
                Logger.d("close output", e);
            }
        }
    }

    /**
     * 转移文件
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param src 源文件
     * @param dest 目的文件
     * @throws Exception 异常信息
     */
    public static void moveFile(String src, String dest) throws Exception {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(dest)) {
            throw new IllegalArgumentException("file name path is empty");
        }
        moveFile(new File(src), new File(dest));
    }

    /**
     * 转移文件
     * {@link NullPointerException} 输入||输出文件为空
     * {@link FileNotFoundException} 文件未找到时抛出
     * {@link EOFException} 文件大小为0时抛出
     * {@link Exception} 文件copy过程中异常抛出
     * @param src 源文件
     * @param dest 目的文件
     * @throws Exception 异常信息
     */
    public static void moveFile(File src, File dest) throws Exception {
        if (src == null) {
            throw new NullPointerException("src file is null");
        }
        if (dest == null) {
            throw new NullPointerException("dest file is null");
        }
        if (!FileUtil.fileExists(src)) {
            throw new FileNotFoundException();
        }
        if (src.renameTo(dest)) {
            return;
        }
        try {
            copyFileByFileChannel(src, dest);
            deleteFile(src);
        } catch (Exception e) {
            copyFile(src, dest, null);
            deleteFile(src);
        }
    }

    public static String saveBitmap(String path, String name, Bitmap bitmap) {
        if (bitmap == null || TextUtils.isEmpty(path))
            return "";
        try {
            path = pathAddBackslash(path);
            makeDirs(path);
            name = TextUtils.isEmpty(name) ? UUID.randomUUID().toString() : name;
            File file = new File(path + name + ".jpg");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            return "";
        }
    }

    // 删除文件
    public static boolean deleteFile(String filename) {
        return deleteFile(new File(filename));
    }

    // 删除文件
    public static boolean deleteFile(File file) {
        try {
            return !fileExists(file) || file.delete();
        } catch (Exception e) {
            return false;
        }
    }

    // 文件查找
    public static boolean fileExists(String fileName) {
        try {
            String path = extractFilePath(fileName); // 获取文件路径
            String name = extractFileName(fileName); // 获取文件名
            return fileExists(new File(path, name)); // 判断是否能在path中找到name文件，如果找到返回true否则返回false
        } catch (Exception err) {
            return false;
        }
    }

    // 文件查找
    public static boolean fileExists(File file) {
        try {
            return file.exists();
        } catch (Exception err) {
            return false;
        }
    }

    // 获取文件路径
    public static String extractFilePath(String fileName) {
        fileName = pathRemoveBackslash(fileName);
        //
        int pos = fileName.lastIndexOf('/');
        if (-1 == pos)
            return "";
        return fileName.substring(0, pos);
    }

    // 获取文件名
    public static String extractFileName(String fileName) {
        if (!TextUtils.isEmpty(fileName) && hasPathSlash(fileName)) {
            int i = fileName.lastIndexOf('/');
            if (i < 0)
                i = fileName.lastIndexOf("\\");
            //
            if ((i > -1) && (i < (fileName.length()))) {
                return fileName.substring(i + 1);
            }
        }
        return fileName;
    }

    public static String extractFileExtension(String fileName) {
        String name = extractFileName(fileName);
        if (!TextUtils.isEmpty(name)) {
            int index = name.lastIndexOf('.');
            if ((index > -1) && (index < name.length())) {
                return name.substring(index + 1);
            }
        }
        return "";
    }

    public static long fileSize(String fileName) {
        if (fileExists(fileName))
            return new File(fileName).length();
        return 0;
    }

    // 判断字符段中是否存在反斜线
    private static boolean hasPathSlash(String path) {
        return !TextUtils.isEmpty(path) && (path.contains("/") || path.contains("\\"));
    }

    public static byte[] toByteArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
                Logger.e("in.close", e);
            }
            try {
                bos.close();
            } catch (Exception e) {
                Logger.e("bos.close", e);
            }
        }
    }

    public interface OnCopyProgressListener {
        void onProgress(long hasCopy, long length);
    }
}
