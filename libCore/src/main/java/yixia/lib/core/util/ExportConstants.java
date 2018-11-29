package yixia.lib.core.util;

import android.content.Intent;

/**
 * Created by lishizhong on 2018/8/13.
 */

public class ExportConstants {

    public static int exportWidth;
    public static int exportHeight;

    public static void loadData(Intent intent) {
        if (intent != null) {
            int exportWidth = intent.getIntExtra("record_width", 0);
            int exportHeight = intent.getIntExtra("record_height", 0);
            ExportConstants.exportWidth = exportWidth;
            ExportConstants.exportHeight = exportHeight;
        }
    }
}
