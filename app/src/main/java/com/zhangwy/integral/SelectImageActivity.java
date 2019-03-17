package com.zhangwy.integral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.util.UUID;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.DirMgmt;
import yixia.lib.core.util.Logger;

public class SelectImageActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXTRA_CUT = "extra_cut";

    public static void start(Activity activity, boolean cut, int requestCode) {
        Intent intent = new Intent(activity, SelectImageActivity.class);
        intent.putExtra(EXTRA_CUT, cut);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, boolean cut, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), SelectImageActivity.class);
        intent.putExtra(EXTRA_CUT, cut);
        fragment.startActivityForResult(intent, requestCode);
    }

    private final int REQUEST_CODE_TAKE_PHOTO = 100;
    private final int REQUEST_CODE_SELECT_IMAGE = 101;
    private final int REQUEST_CODE_CUT_IMAGE = 102;

    private boolean cut = false;
    private Uri takePhotoUri;
    private Uri outPutUri;
    private String filePath = DirMgmt.getInstance().getPath(DirMgmt.WorkDir.OTHER);
    private String fileName = UUID.randomUUID().toString() + ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.cut = getIntent().getBooleanExtra(EXTRA_CUT, false);
        this.findViewById(R.id.selectImageTakePhoto).setOnClickListener(this);
        this.findViewById(R.id.selectImageSelectImage).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectImageTakePhoto:
                this.takePhoto();
                break;
            case R.id.selectImageSelectImage:
                this.selectImage();
                break;
        }
    }

    private void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(this.filePath, this.fileName);
        this.takePhotoUri = Uri.fromFile(file);
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.takePhotoUri);
        startActivityForResult(openCameraIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    private void selectImage() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO: {
                if (cut) {
                    this.zoomPhoto(this.takePhotoUri);
                } else {
                    this.close(this.takePhotoUri);
                }
                break;
            }
            case REQUEST_CODE_SELECT_IMAGE: {
                Uri uri = data.getData();
                if (uri != null) {
                    if (cut) {
                        this.zoomPhoto(uri);
                    } else {
                        this.close(uri);
                    }
                } else {
                    this.showMessage(true, R.string.select_image_select_failed);
                }
                break;
            }
            case REQUEST_CODE_CUT_IMAGE: {
                this.close(this.outPutUri);
                break;
            }
        }
    }

    private void zoomPhoto(Uri uri) {
        if (uri == null) {
            Logger.i("The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);

        this.outPutUri = Uri.fromFile(new File(filePath, this.fileName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.outPutUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_CODE_CUT_IMAGE);
    }

    private void close(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        this.finish();
    }
}
