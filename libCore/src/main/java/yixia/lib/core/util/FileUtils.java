package yixia.lib.core.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 将文本文件中的内容读入到buffer中
     *
     * @param filePath 文件路径
     */
    public static void readToBuffer(StringBuilder builder, String filePath) {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(is));
            String line;
            line = reader.readLine();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = reader.readLine();
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            Logger.e(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        readToBuffer(sb, filePath);
        return sb.toString();
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        readToBuffer(sb, file.getPath());
        return sb.toString();
    }

    public static String readFile(InputStream inputStream) throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        line = reader.readLine();
        StringBuilder builder = new StringBuilder();
        while (line != null) {
            builder.append(line);
            builder.append("\n");
            line = reader.readLine();
        }
        reader.close();
        inputStream.close();
        return builder.toString();
    }


    public static boolean writeFile(File dest, byte[] data) {
        if (dest == null || data == null) {
            return false;
        }
        if (!dest.exists()) {
            try {
                dest.createNewFile();
            } catch (IOException e) {
                Logger.w(e.getMessage());
                return false;
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dest);
            fileOutputStream.write(data);
            fileOutputStream.close();
            fileOutputStream = null;
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * @return data/data/${packageName}/${pluginPackageName}
     */
    public static String getPrivateCachePath(Context context, String directory) {
        File cacheDir = context.getCacheDir();
        File file;
        if (!TextUtils.isEmpty(directory)) {
            file = new File(cacheDir, directory);
        } else {
            file = cacheDir;
        }
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 复制文件
     *
     * @param destFile
     * @param sourceFile
     * @return
     */
    public static boolean copy(@NonNull File sourceFile, @NonNull File destFile) {
        FileInputStream ins = null;
        FileOutputStream out = null;
        try {
            if (!sourceFile.exists()) {
                return false;
            }
            ins = new FileInputStream(sourceFile);
            out = new FileOutputStream(destFile);
            byte[] buff = new byte[4096];
            int readCount;
            while ((readCount = ins.read(buff)) != -1) {
                out.write(buff, 0, readCount);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "copy file failed! file name: " + sourceFile.getAbsolutePath());
            return false;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    Log.e(TAG, "close input stream failed! " + e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, "close output stream failed! " + e.getMessage());
                }
            }
        }
    }

    /**
     * 清空文件夹。
     *
     * @param path
     * @param removeSelf true 删除文件夹及内容，false 仅仅删除文件夹内容
     */
    public static boolean clearDirectory(File path, boolean removeSelf) {
        if (path == null || !path.exists()) {
            return true;
        }
        if (path.isDirectory()) {
            final File[] files = path.listFiles();
            if (files != null) {
                for (File child : files) {
                    clearDirectory(child, true);
                }
            }
        }
        if (removeSelf) {
            return path.delete();
        } else {
            return true;
        }
    }

    /**
     * 解压缩文件到指定的目录.
     *
     * @param unzipFileName 需要解压缩的文件
     * @param destPath      解压缩后存放的路径
     */
    public static boolean unZip(String unzipFileName, String destPath) {
        if (!destPath.endsWith("/")) {
            destPath = destPath + "/";
        }
        FileOutputStream fileOut;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry;
        File file;
        int readBytes;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unzipFileName)));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(destPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readBytes);
                    }
                    fileOut.close();
                }
            }
            return true;
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            if (null != zipIn) {
                try {
                    zipIn.closeEntry();
                } catch (IOException e) {
                    Log.e(TAG, "close input entry failed! " + e.getMessage());
                }
                try {
                    zipIn.close();
                } catch (IOException e) {
                    Log.e(TAG, "close input stream failed! " + e.getMessage());
                }
            }
        }
        return false;
    }

    public static void moveFile(String fromPath, String toPath) {
        File fromFile = new File(fromPath);
        if (fromFile.isDirectory()) {
            File[] fromFiles = fromFile.listFiles();
            if (fromFiles == null) {
                return;
            }
            File toFolder = new File(toPath);
            if (!toFolder.exists()) {
                toFolder.mkdirs();
            }
            for (int i = 0, len = fromFiles.length; i < len; i++) {
                File file = fromFiles[i];
                if (file.isDirectory()) {
                    moveFile(file.getPath(), toPath + File.separator + file.getName());
                }
                if (file.isFile()) {
                    File toFile = new File(toFolder + File.separator + file.getName());
                    file.renameTo(toFile);
                }
            }
        } else if (fromFile.isFile()) {
            File toFolder = new File(toPath);
            if (!toFolder.exists()) {
                toFolder.mkdirs();
            }
            File toFile = new File(toFolder + File.separator + fromFile.getName());
            fromFile.renameTo(toFile);
        }
    }


}