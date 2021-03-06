package yixia.lib.core.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;

import yixia.lib.core.task.Task;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/7 下午2:27
 * 修改时间：2017/4/7 下午2:27
 * Description: 目录相关
 */
@SuppressWarnings("unused")
public class DirMgmt {
    private static final String ROOT = "integral";
    private static DirMgmt instance;

    public static DirMgmt getInstance() {
        if (instance == null) {
            synchronized (DirMgmt.class) {
                if (instance == null) {
                    instance = new DirMgmt();
                }
            }
        }
        return instance;
    }

    private Context context;

    public void init(Context ctx) {
        if (ctx == null) return;
        this.context = ctx.getApplicationContext();
        this.clearDirs();
        this.makeDirs();
    }

    public String getPath(WorkDir workDir) {
        if (workDir != null) {
            String path = workDir.ram ? getRamRootPath(context) : getRootPath();
            if (!FileUtil.makeDirs(path)) {
                Logger.e("create directory1: " + path + " failed!");
            }
            path = FileUtil.pathAddBackslash(path) + workDir.path;
            if (!FileUtil.makeDirs(path)) {
                Logger.e("create directory2: " + path + " failed!");
            }
            return path;
        }

        return getRootPath();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public long getSDCardAvailableSize() {
        try {
            if (Device.OS.getAndroidOSVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return new StatFs(Environment.getExternalStorageDirectory().getPath())
                        .getAvailableBytes();
            } else {
                return new StatFs(Environment.getExternalStorageDirectory().getPath())
                        .getAvailableBlocks();
            }
        } catch (Throwable throwable) {
            Logger.e("getSDCardAvailableSize", throwable);
            return 0;
        }
    }

    private String getPath(WorkDir workDir, boolean ram) {
        if (ram) {
            String path = getRamRootPath(context);
            if (!FileUtil.makeDirs(path)) {
                Logger.e("create directory3: " + path + " failed!");
            }
            path = FileUtil.pathAddBackslash(path) + workDir.path;
            if (!FileUtil.makeDirs(path)) {
                Logger.e("create directory4: " + path + " failed!");
            }
            return path;
        } else {
            return getPath(workDir);
        }
    }

    private String getRootPath() {
        String rootPath;
        if (isSdCardAvailable()) {
            rootPath = getSdCardRootPath();
        } else {
            rootPath = getRamRootPath(context);
        }
        if (!FileUtil.makeDirs(rootPath)) {
            Logger.e("create directory: " + rootPath + " failed!");
        }
        return rootPath;
    }

    private String getSdCardRootPath() {
        return FileUtil.pathAddBackslash(sdCardRootPath()) + ROOT;
    }

    private String getRamRootPath(Context ctx) {
        return FileUtil.pathAddBackslash(ramRootPath(ctx)) + ROOT;
    }

    // sd卡路径
    private String sdCardRootPath() {
        try {
            File file;
            if (isSdCardAvailable() && (file = context.getExternalFilesDir(null)) != null) {
                return file.getPath();
            }
        } catch (Exception e) {
            Logger.e("sdCardRootPath", e);
        }
        return this.ramRootPath(context);
    }

    // 私有数据目录
    private String ramRootPath(Context ctx) {
        if (ctx == null)
            return "";
        return FileUtil.pathRemoveBackslash(ctx.getFilesDir().getAbsolutePath());
    }

    private Boolean isSdCardAvailable() {
        return TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)
                && !Environment.isExternalStorageRemovable();
    }

    private void makeDirs() {
        for (WorkDir dir : WorkDir.values()) {
            FileUtil.makeDirs(getPath(dir));
        }
    }

    private void clearDirs() {
        Task<DirMgmt> task = new Task<DirMgmt>() {
            @Override
            protected void doInBackground(DirMgmt... params) {
                if (Util.isEmpty(params)) {
                    return;
                }
                DirMgmt mgmt = params[0];
                for (WorkDir dir : WorkDir.values()) {
                    if (dir.useTime > 0) {
                        FileUtil.clear(mgmt.getPath(dir), "", dir.useTime);
                        FileUtil.clear(mgmt.getPath(dir, true), "", dir.useTime);
                    }
                }
            }
        };
        task.execute(this);
    }

    public enum WorkDir {
        IMAGE("image", "广告图片素材目录", false, 7),
        MEDIA("media", "广告视频素材目录", false, 7),
        GIF("gif", "广告GIF素材目录", false, 7),
        APK("apk", "广告点击下载apk目录", false, -1),
        OTHER("other", "未知文件目录目录", false, -1),;
        public String path; //目录路径
        public String desc; //目录描述
        public boolean ram; //强制使用ram目录
        public long useTime; //有效时间，单位毫秒

        WorkDir(String path, String desc, boolean ram, int useTime) {
            this.path = path;
            this.desc = desc;
            this.ram = ram;
            this.useTime = useTime * 86400000; //86400000=24*60*60*1000一天时间
        }
    }
}
