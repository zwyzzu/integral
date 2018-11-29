package yixia.lib.core.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liutao on 1/16/15.
 */
@SuppressWarnings("unused")
public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();

    /**
     * get scaled bitmap
     *
     * @param filePath  local file path
     * @param maxWidth  scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     *                  bitmap width is maxWidth while bitmap height is maxWidth * sRatio
     * @param maxHeight scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     *                  bitmap height is maxHeight while bitmap width is maxHeight / sRatio.
     * @return scaled bitmap
     */
    public static Bitmap getScaledBitmap(String filePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        //Log.d(TAG, "Actual width: " + actualWidth + ", actual height: " + actualHeight);
        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);
        Log.d(TAG, "Desired width: " + desiredWidth + ", desired height: " + desiredHeight);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // (ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth
                || tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }

    /**
     * get scaled bitmap
     *
     * @param imageResId image resource id
     * @param maxWidth   scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     *                   bitmap width is maxWidth while bitmap height is maxWidth * sRatio
     * @param maxHeight  scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     *                   bitmap height is maxHeight while bitmap width is maxHeight / sRatio.
     * @return scaled bitmap
     */
    public static Bitmap getScaledBitmap(Context context, int imageResId, int maxWidth,
                                         int maxHeight) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResId, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
//        Log.d(TAG, "Actual width: " + actualWidth + ", actual height: " + actualHeight);

        // Then compute the dimensions we would ideally like to decode to.
        int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                actualHeight, actualWidth);
        Log.d(TAG, "Desired width: " + desiredWidth + ", desired height: " + desiredHeight);

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false;
        // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize =
                findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), imageResId,
                decodeOptions);
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth
                || tempBitmap.getHeight() > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true);
            tempBitmap.recycle();
        } else {
            bitmap = tempBitmap;
        }
        return bitmap;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    private static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    /**
     * Scales one side of a rectangle to fit aspect sRatio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for
     *                        max width), or zero to maintain aspect sRatio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect sRatio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling sRatio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    /**
     * get actual image dimension
     *
     * @param imagePath local file path
     * @return 『』
     */
    public static int[] getActualImageDimension(String imagePath) {
        int[] imageSize = new int[2];
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        imageSize[0] = actualWidth;
        imageSize[1] = actualHeight;
        return imageSize;
    }

    /**
     * get actual image dimension
     *
     * @param imageResId image resource id
     * @return 『』
     */
    public static int[] getActualImageDimension(Context context, int imageResId) {
        int[] imageSize = new int[2];
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        // If we have to resize this image, first get the natural bounds.
        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResId, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        imageSize[0] = actualWidth;
        imageSize[1] = actualHeight;
        return imageSize;
    }

    private static int[] getDesiredImageDimension(String imagePath, int maxWidth, int maxHeight) {
        int[] desiredImageDimension = new int[2];
        int[] actualImageDimension = getActualImageDimension(imagePath);
//        Log.d(TAG, "Actual width: " + actualImageDimension[0] + ", actual height: "
//                + actualImageDimension[1]);
        int maxPrimary;
        int maxSecondary;
        if (actualImageDimension[0] >= actualImageDimension[1]) {
            maxPrimary = maxWidth;
            maxSecondary = 0;
            desiredImageDimension[0] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[0], actualImageDimension[1]);
            desiredImageDimension[1] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[1], actualImageDimension[0]);
        } else {
            maxPrimary = maxHeight;
            maxSecondary = 0;
            desiredImageDimension[1] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[1], actualImageDimension[0]);
            desiredImageDimension[0] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[0], actualImageDimension[1]);
        }
        Log.d(TAG, "Desired width: " + desiredImageDimension[0] + ", desired height: "
                + desiredImageDimension[1]);
        return desiredImageDimension;
    }

    private static int[] getDesiredImageDimension(Context context, int imageResId, int maxWidth,
                                                  int maxHeight) {
        int[] desiredImageDimension = new int[2];
        int[] actualImageDimension = getActualImageDimension(context, imageResId);
//        Log.d(TAG, "Actual width: " + actualImageDimension[0] + ", actual height: "
//                + actualImageDimension[1]);
        int maxPrimary;
        int maxSecondary;
        if (actualImageDimension[0] >= actualImageDimension[1]) {
            maxPrimary = maxWidth;
            maxSecondary = 0;
            desiredImageDimension[0] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[0], actualImageDimension[1]);
            desiredImageDimension[1] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[1], actualImageDimension[0]);
        } else {
            maxPrimary = maxHeight;
            maxSecondary = 0;
            desiredImageDimension[1] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[1], actualImageDimension[0]);
            desiredImageDimension[0] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[0], actualImageDimension[1]);
        }
        Log.d(TAG, "Desired width: " + desiredImageDimension[0] + ", desired height: "
                + desiredImageDimension[1]);
        return desiredImageDimension;
    }

    public static int[] transformDimension(int width, int height, int maxWidth, int maxHeight) {
        int[] desiredImageDimension = new int[2];
        int[] actualImageDimension = {width, height};
//        Log.d(TAG, "Actual width: " + actualImageDimension[0] + ", actual height: "
//                + actualImageDimension[1]);
        int maxPrimary;
        int maxSecondary;
        if (actualImageDimension[0] >= actualImageDimension[1]) {
            if (actualImageDimension[0] < maxWidth) {
                maxPrimary = actualImageDimension[0];
            } else {
                maxPrimary = maxWidth;
            }
            maxSecondary = 0;
            desiredImageDimension[0] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[0], actualImageDimension[1]);
            desiredImageDimension[1] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[1], actualImageDimension[0]);
        } else {
            if (actualImageDimension[1] < maxHeight) {
                maxPrimary = actualImageDimension[1];
            } else {
                maxPrimary = maxHeight;
            }
            maxSecondary = 0;
            desiredImageDimension[1] = getResizedDimension(maxPrimary, maxSecondary,
                    actualImageDimension[1], actualImageDimension[0]);
            desiredImageDimension[0] = getResizedDimension(maxSecondary, maxPrimary,
                    actualImageDimension[0], actualImageDimension[1]);
        }
        Log.d(TAG, "Desired width: " + desiredImageDimension[0] + ", desired height: "
                + desiredImageDimension[1]);
        return desiredImageDimension;
    }

    /**
     * compress the image file, create a scaled compressed image file, and overwrite the origin one.
     *
     * @param path      origin image file path
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @param quality   Hint to the compressor, 0-100. 0 meaning compress for
     *                  small size, 100 meaning compress for max quality. Some
     *                  formats, like PNG which is lossless, will ignore the
     *                  quality setting
     */
    public static void compress(String path, int maxWidth, int maxHeight, int quality,
                                Bitmap.Config config) {
        FileOutputStream out;
        try {
            Bitmap scaledBitmap = getScaledBitmap(path, maxWidth, maxHeight);
            Bitmap rotatedBitmap = rotateBitmap(getBitmapDegree(path), scaledBitmap);
            out = new FileOutputStream(path);
            Bitmap mutableBitmap = rotatedBitmap.copy(config, true);

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean compress(String inputPath, String outputPath, int maxWidth,
                                int maxHeight, int quality) {
        FileOutputStream out;
        try {
            Bitmap scaledBitmap = getScaledBitmap(inputPath, maxWidth, maxHeight);
            Bitmap rotatedBitmap = rotateBitmap(getBitmapDegree(inputPath), scaledBitmap);
            out = new FileOutputStream(outputPath);
            Bitmap mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            return true;
        } catch (FileNotFoundException e) {
            Logger.d("compress", e);
        } catch (NullPointerException npe) {
            Logger.d("compress", npe);
        } catch (Throwable throwable) {
            Logger.d("compress", throwable);
        }
        return false;
    }

    /**
     * add water destMark at the left top of image.
     *
     * @param context       上下文
     * @param srcPath      local image file path
     * @param watermarkRes watermark resource
     * @param maxWidth     scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     *                     bitmap width is maxWidth while bitmap height is maxWidth * sRatio
     * @param maxHeight    scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     *                     bitmap height is maxHeight while bitmap width is maxHeight / sRatio.
     * @param quality      compress quality
     */
    // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    public static void watermark(Context context, String srcPath, int watermarkRes, int maxWidth,
                                 int maxHeight, int quality) {

        FileOutputStream out;
        try {
            Bitmap scaledBitmap = getScaledBitmap(srcPath, maxWidth, maxHeight);
            Bitmap rotatedBitmap = rotateBitmap(getBitmapDegree(srcPath), scaledBitmap);
            Bitmap scaledWatermark = getScaledBitmap(context, watermarkRes, maxWidth, maxHeight);
            out = new FileOutputStream(srcPath);

            Bitmap mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawBitmap(scaledWatermark, 0, 0, null);

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Add watermark result in OOM");
            e.printStackTrace();
        }
    }

    /**
     * add watermark at the right bottom of the image
     *
     * @param context      上下文
     * @param srcPath      local image file path
     * @param watermarkRes watermark resource
     * @param maxWidth     scaled bitmap width you desired, if maxWidth < maxHeight, then scaled
     *                     bitmap width is maxWidth while bitmap height is maxWidth * sRatio
     * @param maxHeight    scaled bitmap height you desired, if maxHeight < maxWidth, then scaled
     *                     bitmap height is maxHeight while bitmap width is maxHeight / sRatio.
     * @param quality      compress quality
     */
    public static void watermarkAtRightBottom(Context context, String srcPath, int watermarkRes,
                                              int maxWidth, int maxHeight, int quality) {

        FileOutputStream out;
        try {
            Bitmap scaledBitmap = getScaledBitmap(srcPath, maxWidth, maxHeight);
            Bitmap rotatedBitmap = rotateBitmap(getBitmapDegree(srcPath), scaledBitmap);
            Bitmap scaledWatermark = getScaledBitmap(context, watermarkRes, maxWidth, maxHeight);
            out = new FileOutputStream(srcPath);

            int left = rotatedBitmap.getWidth() - scaledWatermark.getWidth();
            int top = rotatedBitmap.getHeight() - scaledWatermark.getHeight();

            Bitmap mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);
            canvas.drawBitmap(scaledWatermark, left, top, null);

            // write the compressed bitmap at the destination specified by filename.
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Add watermark result in OOM");
            e.printStackTrace();
        }
    }

    /**
     * get bitmap degree, you may get an rotated photo when you take a picture in some devices.
     *
     * @param path local image file path
     * @return 『』
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * rotate bitmap
     *
     * @param angle  rotate angle
     * @param bitmap origin bitmap
     * @return rotated bitmap
     */
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * create a copied image.
     *
     * @param context
     * @param photoUri   origin image uri
     * @param outputPath output image uri.
     * @return true if successfully compressed to the specified stream.
     * @throws IOException 『』
     */
    public static boolean copyBitmapFile(Context context,
                                         Uri photoUri, String outputPath) throws IOException {
        // Load image from path
        InputStream input = context.getContentResolver().openInputStream(photoUri);

        // compress it
        Bitmap bitmapOrigin = BitmapFactory.decodeStream(input);
        if (bitmapOrigin == null) return false;
        // save to file
        FileOutputStream output = new FileOutputStream(outputPath);
        return bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 100, output);
    }

    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param image 『』
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return 『』
     */
    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (os.toByteArray().length / 1024 > 1024) {
            os.reset(); //重置baos即清空baos
            //这里压缩50%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;  // 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;  // 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; //be=1表示不缩放
        if (w > h && w > ww) {  //如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {   //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;  //设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            baos.reset();   //重置baos即清空baos
            //这里压缩50%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;    //这里设置高度为800f
        float ww = 480f;    //这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1; //be=1表示不缩放
        if (w > h && w > ww) {  //如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {   //如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;  //设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);   //压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();   //重置baos即清空baos
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;  //每次都减少10
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight); // 不改变原来图像大小
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static Bitmap zoomBitmap(Bitmap target, int length, boolean small) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) length) / width;
        float scaleHeight = ((float) length) / height;
        float scale = small ? Math.min(scaleWidth, scaleHeight) : Math.max(scaleWidth, scaleHeight);
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix, true);
        if (!target.equals(bmp) && !target.isRecycled()) {
            target.recycle();
        }
        return bmp;
    }

    public static Bitmap zoomBitmap(Bitmap target, int sWidth, int sHeight, boolean small) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) sWidth) / width;
        float scaleHeight = ((float) sHeight) / height;
        float scale = small ? Math.min(scaleWidth, scaleHeight) : Math.max(scaleWidth, scaleHeight);
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix, true);
        if (!target.equals(bmp) && !target.isRecycled()) {
            target.recycle();
        }
        return bmp;
    }

    public static Bitmap cutBitmap(Bitmap target, int length) {
        return cutBitmap(target, length, length);
    }

    public static Bitmap cutBitmap(Bitmap target, int sWidth, int sHeight) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) sWidth) / width;
        float scaleHeight = ((float) sHeight) / height;
        float scale = Math.max(scaleWidth, scaleHeight);
        matrix.postScale(scale, scale);
        int nWidth = (int) (sWidth / scale);
        int nHeight = (int) (sHeight / scale);
        int xStart = (width - nWidth) / 2;
        int yStart = (height - nHeight) / 2;
        xStart = xStart < 0 ? 0 : xStart;
        yStart = yStart < 0 ? 0 : yStart;
        Bitmap bmp = Bitmap.createBitmap(target, xStart, yStart, nWidth, nHeight, matrix, true);
        if (!target.equals(bmp) && !target.isRecycled()) {
            target.recycle();
        }
        return bmp;
    }
    /**
     * 选择变换
     *
     * @param bitmap 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float alpha) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        if (!bitmap.equals(newBM) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBM;
    }

    public static Bitmap getFrameAtTime(String path, long time) {
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(path);
            if (time >= 0) {
                return media.getFrameAtTime(time);
            } else {
                return media.getFrameAtTime();
            }
        } catch (Throwable e) {
            return null;
        }
    }
}
