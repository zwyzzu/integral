package yixia.lib.core.base;

import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by zhaoliangtai on 2018/5/24.
 */

public interface Constant {

    int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    int CAPTURE_TO_EDIT_REQUEST_CODE = 10089;
    int CAPTURE_TO_IMPORT_REQUEST_CODE = 10088;
    String SENCE_LIC_DIR = "lic";
    String VIDEO_DIR = "video";
    String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";
    String ASSETS_PATH = "data";
    String FILTER_PATH = "filter_config";
    String EFFECT_PATH = "Filter_shake_bobo";
    String TEMPLATE_PATH = "template_config";
    String SMOOTH_FILE_NAME = "smooth.glsl";
    String EXPORT_PATH = "video_export";
    String EXTRA_RECORDING_WORKS = "recording_wroks";
    String EXTRA_FROM_ALBUM = "extra_from_album";
    String EXTRA_IS_FROM_DRAFT_BOX = "extra_from_draft_box";
    String EXTRA_DRAFT = "extra_draft";
    String VIDEO_THUMBNAIL_CACHE = "video_thumbnail_cache";
    String DRAFTID_KEY = "draftIdKey"; //存入sp中draftId的key值
    String PREFERENCE = "video_core_preference";
    String EXTRA_DRAFT_ID = "draft_id";
    String EXTRA_COVER_RECORD_SCENE_KEY = "cover_record_scene_key";
    String EXTRA_COVER_RECORD_BACK = "cover_record_back";
    String DRAFT_LIST_COME = "draft_list"; //1表示拍摄，2表示相册
    String RECORD_SCENE_KEY = "scene_key";
    String CAMERA_ID = "camera_id";
    String PACKAGE_NAME = "yixia.video.core";
    String DRAFT_PRE_CLEAN_TIME = "draft_pre_clean_time";
    String CLASS_CAPTURE_ACTIVITY = "com.yixia.capture.ui.capture.CaptureActivity";
    String CLASS_EDIT_ACTIVITY = "com.yixia.videoeditorplugin.ui.VideoEditorActivity";
    String CLASS_PUBLIC_ACTIVITY = "com.yixia.publish.VSPublishActivity";
    String EXTRA_RECORD_FOLLOW = "record_follow";
    String EXTRA_RECORD_TEMPLATE_FOLLOW = "record_template_follow";
    String KEY_SOURCE = "source";       // 埋点时 拍摄页面曝光需要用到的
    String KEY_SOURCE_SP = "source_sp";       // 埋点时 拍摄页面曝光需要用到的
    String EXTRA_WITHOUT_HISTORY = "without_history"; //不需要保留记录
    String EXTRA_WITH_CRASH = "with_crash"; //crash 数据恢复
    String EXTRA_NIGHT_MODEL = "night_model"; //夜间模式
    String EXTRA_DIALOG_FRAGMENT_BACK_PRESSED = "isRespondBackBtn";
    String REF_CAPTURE = "CAPTURE";
    String REF_GALLERY = "GALLERY";

    String MUSIC_CACHE = "music_online_cache";
    String TEMPLATE_EFFECT_CACHE = "template_effect_cache";
    String CACHE_DIR = "VideoCore";

    String EXTRA_IMPORT_ALBUM_MODEL = "extra_import_album_model";

    String EXTRA_TOPIC_DATA = "EXTRA_TOPIC_DATA";

    String[] FILTER_TITLES = {
            "无",    // 0
            // 人物
            "恋空",
            "温暖",
            "冷艳",
            "清纯",
            "甜美",
            "初恋",
            "少女",
            "年华",   // 8
            // 风景
            "日杂",
            "午后",
            "海洋",
            "樱花",
            "度假",
            "幽静",
            "天空",
            "森林",
            "氧气",
            "晚霞",
            "明亮",   // 19
            // 大片
            "复古",   // 20
            // 美食
            "食物",
            "咖啡",
            "可可",   // 23
            // 艺术
            "珊瑚",
            "忧郁",
            "黑白",
            "浪漫",
            "冷静",
            "回忆",
            "光晕",
            "微光",
            "沁蓝",   // 32
            // 日系
            "日系",
            "清新",   // 34
            // 季节
            "初雪",
            "冬至",
            "秋色"    // 37
    };

    String[] FILTER_NAMES = {
            "",

            "filter_3D_qingxin",
            "filter_3D_feifan",
            "filter_3D_dongren",
            "filter_3D_chunzhen",
            "filter_3D_huopo",
            "filter_3D_sudalv",
            "filter_3D_yinghua",
            "filter_3D_nianhua",

            "filter_3D_xiaoquexing",
            "filter_3D_shenqiu",
            "filter_3D_jiaolan",
            "filter_3D_qiangwei",
            "filter_3D_songlu",
            "filter_3D_qingyou",
            "filter_3D_weilan",
            "filter_3D_dianqing",
            "filter_3D_forest",
            "filter_3D_sunset",
            "filter_3D_luomiou",

            "filter_3D_vintage",

            "filter_3D_kekou",
            "filter_3D_yinni",
            "filter_3D_quqi",

            "filter_3D_lanshan",
            "filter_3D_xianjing",
            "filter_3D_shenhei",
            "filter_3D_pixar",
            "filter_3D_guowang",
            "filter_3D_fortune",
            "filter_3D_seep",
            "filter_3D_riguang",
            "filter_3D_bohe",

            "filter_3D_rixi",
            "filter_3D_qingchun",

            "filter_3D_chunjing",
            "filter_3D_baixue",
            "filter_3D_zhiqiu",
    };
    int NOTCH_TYPE_NORMAL = 0x34;
    int NOTCH_TYPE_OPPO = 0x35;
    int NOTCH_TYPE_VIVO = 0x36;
    int NOTCH_TYPE_HW = 0x37;

    interface WorksEntry extends BaseColumns {
        String TABLE_NAME = "RecordWorks";
        String SHOOT_ID = "shoot_id";
        String CONTENT = "content";
    }

    public enum AlbumModel {
        PHOTO("album_photo"), VIDEO("album_video"), ALL("album_all");
        public String code;
        AlbumModel(String code) {
            this.code = code;
        }

        public static AlbumModel find(String code) {
            for (AlbumModel model: AlbumModel.values()) {
                if (TextUtils.equals(code, model.code)) {
                    return model;
                }
            }
            return ALL;
        }
    }
}
