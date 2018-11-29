package yixia.lib.core.util;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 注意，此So 基于Tinker，如果发现新版本无法工作，请更新对应的处理.
 *
 */
public class SoLoader {
    private static final String TAG = "SoLoader";

    public static synchronized void installNativeLibraryPath(ClassLoader classLoader, File
            folder) throws Throwable {
        if (folder == null || !folder.exists()) {
            Logger.e(TAG, "installNativeLibraryPath, folder %" + folder + " is illegal");
            return;
        }
        // android o sdk_int 26
        // for android o preview sdk_int 25
        if ((Build.VERSION.SDK_INT == 25 && getPreviousSdkInt() != 0)
                || Build.VERSION.SDK_INT > 25) {
            try {
                V25.install(classLoader, folder);
                return;
            } catch (Throwable throwable) {
                // install fail, try to treat it as v23
                // some preview N version may go here
                Logger.e(TAG, "installNativeLibraryPath, v25 fail, sdk: " + Build.VERSION.SDK_INT
                        + ", error: " + throwable.getMessage() + ", try to fallback to V23");
                V23.install(classLoader, folder);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            try {
                V23.install(classLoader, folder);
            } catch (Throwable throwable) {
                // install fail, try to treat it as v14
                Logger.e(TAG, "installNativeLibraryPath, v23 fail, sdk: " + Build.VERSION.SDK_INT
                        + ", error: " + throwable.getMessage() + ", try to fallback to V14");

                V14.install(classLoader, folder);
            }
        } else if (Build.VERSION.SDK_INT >= 14) {
            V14.install(classLoader, folder);
        }
    }

    /**
     * fuck部分机型删了该成员属性，兼容
     *
     * @return 被厂家删了返回1，否则正常读取
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static int getPreviousSdkInt() {
        try {
            return Build.VERSION.PREVIEW_SDK_INT;
        } catch (Throwable ignore) {
        }
        return 1;
    }

    private static final class V14 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Field pathListField = ShareReflectUtil.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            ShareReflectUtil.expandFieldArray(dexPathList, "nativeLibraryDirectories",
                    new File[]{folder});
        }
    }

    private static final class V23 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Field pathListField = ShareReflectUtil.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            Field nativeLibraryDirectories = ShareReflectUtil.findField(dexPathList,
                    "nativeLibraryDirectories");

            List<File> libDirs = (List<File>) nativeLibraryDirectories.get(dexPathList);
            if (libDirs != null) {
                for (File tempFile : libDirs) {
                    Logger.d("loading_123" + tempFile.getAbsolutePath());
                }
            }
//            Logger.d("loading123", );
            libDirs.add(0, folder);
            Field systemNativeLibraryDirectories = ShareReflectUtil.findField(dexPathList,
                    "systemNativeLibraryDirectories");
            List<File> systemLibDirs = (List<File>) systemNativeLibraryDirectories.get(dexPathList);
            Method makePathElements = ShareReflectUtil.findMethod(dexPathList,
                    "makePathElements", List.class, File.class, List.class);
            ArrayList<IOException> suppressedExceptions = new ArrayList<>();
            libDirs.addAll(systemLibDirs);
            Object[] elements = (Object[]) makePathElements.
                    invoke(dexPathList, libDirs, null, suppressedExceptions);
            Field nativeLibraryPathElements = ShareReflectUtil.findField(dexPathList,
                    "nativeLibraryPathElements");
            nativeLibraryPathElements.setAccessible(true);
            nativeLibraryPathElements.set(dexPathList, elements);
        }
    }

    private static final class V25 {
        private static void install(ClassLoader classLoader, File folder) throws Throwable {
            Field pathListField = ShareReflectUtil.findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);

            Field nativeLibraryDirectories = ShareReflectUtil.findField(dexPathList,
                    "nativeLibraryDirectories");

            List<File> libDirs = (List<File>) nativeLibraryDirectories.get(dexPathList);
            libDirs.add(0, folder);
            Field systemNativeLibraryDirectories = ShareReflectUtil.findField(dexPathList,
                    "systemNativeLibraryDirectories");
            List<File> systemLibDirs = (List<File>) systemNativeLibraryDirectories.get(dexPathList);
            Method makePathElements = ShareReflectUtil.findMethod(dexPathList,
                    "makePathElements", List.class);
            libDirs.addAll(systemLibDirs);
            Object[] elements = (Object[]) makePathElements.
                    invoke(dexPathList, libDirs);
            Field nativeLibraryPathElements = ShareReflectUtil.findField(dexPathList,
                    "nativeLibraryPathElements");
            nativeLibraryPathElements.setAccessible(true);
            nativeLibraryPathElements.set(dexPathList, elements);
        }
    }
}
