package yixia.lib.core.util;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.IOException;

/**
 * 获取media信息.
 */
public class MediaUtils {

    public static final String FORMAT_H265 = "video/hevc";
    /**
     * 获取视频格式.
     * @param path
     * @return
     * @throws IOException
     */
    public static String getVideoFormatType(String path) throws IOException {
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(path);
        String keyMime = null;
        int nTracks = mediaExtractor.getTrackCount();
        for (int i = 0; i < nTracks; ++i) {
            mediaExtractor.unselectTrack(i);
        }
        for (int i = 0; i < nTracks; ++i) {
            keyMime = mediaExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME);
            if (keyMime.startsWith("video/")) {
                break;
            }
        }
        mediaExtractor.release();
        return keyMime;
    }
}
