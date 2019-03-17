package com.yixia.album;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yixia.album.model.VSMedia;
import com.yixia.album.model.VSVideo;
import com.yixia.video.business.AbnormalDataHelper;
import com.yixia.video.business.api.ConfigManager;
import com.yixia.video.business.api.clientRemote.CaptureClient;
import com.yixia.video.business.api.clientRemote.StaticsUtil;
import com.yixia.video.business.media.data.VSDraftEntity;
import com.yixia.video.business.media.data.Video;
import com.yixia.video.business.media.data.capture.RecordWorksInfo;
import com.yixia.video.business.media.drafts.DraftCleaner;
import com.yixia.video.business.media.drafts.VSDraftManager;
import com.yixia.video.business.music.MusicReportUtils;
import com.yixia.video.business.skin.QSkinLoaderManager;
import com.yixia.video.business.skin.SkinBaseActivity;
import com.yixia.video.business.user.UserInfoManager;
import com.yixia.videoeditorplugin.ui.VideoEditorSupports;
import com.yixia.widget.VSTabLayout;
import com.yixia.widget.VSTopBar;
import com.yixia.widget.dialog.OnDialogCreatedListener;
import com.yixia.widget.dialog.ProcessingDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.base.Constant;
import yixia.lib.core.base.Constant.AlbumModel;
import yixia.lib.core.util.ExportConstants;
import yixia.lib.core.util.Screen;
import yixia.lib.core.util.VSPermission;
import yixia.lib.core.util.WindowUtil;

public class VSImportAlbumActivity extends BaseActivity implements OnImportListener,
        AbnormalDataHelper.AbnormalCallBack {

    public static final int SPC_BTN_VIDEO = 1;
    public static final int SPC_BTN_IMAGE = 2;
    public static final int SPC_BTN_CANCEL = 3;
    public static final int SPC_BTN_NEXT = 4;
    private static final String EXTRA_FROM_EDIT = "com.yixia.album.extra.from.edit";

    public static void start(Context context, boolean fromEdit, AlbumModel model) {
        Intent intent = new Intent(context, VSImportAlbumActivity.class);
        intent.putExtra(EXTRA_FROM_EDIT, fromEdit);
        intent.putExtra(Constant.EXTRA_IMPORT_ALBUM_MODEL, model.code);
        context.startActivity(intent);
    }

    private static final int REQUEST_CODE_PERMISSION = 100;
    private VSTopBar topBar;
    private Dialog loadingDialog;
    private String shootId = UUID.randomUUID().toString();
    private AbnormalDataHelper abnormalDataHelper;
    private FrameLayout albumImage;
    private FrameLayout albumMedia;
    VSImportImageFragment fragmentImage;
    VSImportVideoFragment fragmentVideo;
    private SimpleDraweeView imagePreview;
    private AlbumModel albumModel = AlbumModel.ALL;
    private String page = StaticsUtil.PAGE_ID_ALBUM_PHOTO;
    private int screenWidth;
    private int screenHeight;

    private ProcessingDialogFragment mProcessingDialog;
    private static final String TAG_DIALOG = "dialog";
    private VSProcessImagesTask mProcessImageTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vs_import_album);
        this.albumModel = AlbumModel.find(getIntent().getStringExtra(Constant.EXTRA_IMPORT_ALBUM_MODEL));
        this.initView();
        HashMap<String, String> permissions = new HashMap<>();
        permissions.put(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_external_storage));
        if (VSPermission.applyPermission(this, REQUEST_CODE_PERMISSION, permissions)) {
            return;
        }
        this.loadData();
    }

    private void initView() {
        this.initTopBar();
        this.initPreview();
        this.albumImage = findViewById(R.id.importAlbumImage);
        this.albumMedia = findViewById(R.id.importAlbumMedia);
    }

    private void initTopBar() {
        this.topBar = findViewById(R.id.importAlbumVSTopBar);
        this.topBar.setRightVisibility(false);
        this.topBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initPreview() {
        int[] screen = Screen.getScreen(this);
        this.screenWidth = screen[0];
        this.screenHeight = screen[1];
        this.imagePreview = this.findViewById(R.id.importAlbumImagePreview);
        this.imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreview();
            }
        });
    }

    private void loadData() {
        this.loadingDialog = WindowUtil.createProgressDialog(this, 0, this.getString(R.string
                .importAlbumLoading), false, true);
        switch (this.albumModel) {
            case VIDEO:
                this.topBar.setCentreText(R.string.importAlbumVideo);
                this.switchFragment(0);
                break;
            case PHOTO:
                this.topBar.setCentreText(R.string.importAlbumImage);
                this.switchFragment(1);
                break;
            case ALL:
            default:
                View view = this.topBar.setCentreContent(R.layout.view_import_album_topbar_centre);
                if (view != null) {
                    VSTabLayout tabLayout = view.findViewById(R.id
                            .importAlbumTopBarCentreTabLayout);
                    tabLayout.setTextSize(Screen.dip2px(this, 18));
                    tabLayout.setTabClickListener(new VSTabLayout.OnTabClickListener() {
                        @Override
                        public void onClickTab(int position) {
                            switchFragment(position);
                        }
                    });
                    tabLayout.setTabs(R.string.importAlbumVideo, R.string.importAlbumImage);
                }
                break;
        }
    }

    private void switchFragment(int position) {
        switch (position) {
            case 0:
                if (this.fragmentVideo == null) {
                    this.fragmentVideo = VSImportVideoFragment.newInstance();
                    this.addFragment(getSupportFragmentManager(), this.fragmentVideo, R.id
                            .importAlbumMedia);
                }
                albumMedia.setVisibility(View.VISIBLE);
                albumImage.setVisibility(View.GONE);
                break;
            case 1:
                if (this.fragmentImage == null) {
                    this.fragmentImage = VSImportImageFragment.newInstance();
                    this.addFragment(getSupportFragmentManager(), this.fragmentImage, R.id
                            .importAlbumImage);
                }
                albumMedia.setVisibility(View.GONE);
                albumImage.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (this.imagePreview != null && this.imagePreview.getVisibility() == View.VISIBLE) {
            this.hidePreview();
            return;
        }
        super.onBackPressed();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //如果是草稿流程中，则忽略场景数据.
        if (!getIntent().getBooleanExtra(Constant.EXTRA_IS_FROM_DRAFT_BOX, false)) {
            this.abnormalDataHelper = AbnormalDataHelper.newInstance(this).setCallback(this)
                    .execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (VSPermission.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    this.loadData();
                } else {
                    this.finish();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, //
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (VSPermission.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    this.loadData();
                } else {
                    this.finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mProcessImageTask != null) {
            mProcessImageTask.setCallback(null);
        }
        if (this.abnormalDataHelper != null) {
            this.abnormalDataHelper.destroy();
            this.abnormalDataHelper = null;
        }
        super.onDestroy();
    }

    @Override
    public void loading() {
        if (this.loadingDialog != null && !this.loadingDialog.isShowing()) {
            this.loadingDialog.show();
        }
    }

    @Override
    public void loadEnd() {
        if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
            this.loadingDialog.dismiss();
            this.loadingDialog = null;
        }
    }

    @Override
    public void onExecuteSucceed() {
        this.loadEnd();
    }

    @Override
    public void showPreview(View itemView, VSMedia media) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Uri uri = Uri.fromFile(new File(media.getPath()));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new
                ResizeOptions(this.screenWidth, this.screenHeight)).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder().setOldController(this
                .imagePreview.getController()).setImageRequest(request).build();
        this.imagePreview.setController(controller);
        this.imagePreview.setVisibility(View.VISIBLE);
    }

    private void hidePreview() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.imagePreview.setVisibility(View.GONE);
    }

    @Override
    public void nextForVideo(VSVideo entity) {
        RecordWorksInfo works = new RecordWorksInfo();
        Video video = new Video();
        video.setFilePath(entity.getPath());
        ArrayList<Video> list = new ArrayList<>();
        list.add(video);
        works.setVideoList(list);
        works.setShootId(this.shootId);
        works.setTopicId(this.getTopicId());

        String clazz = "com.yixia.videoeditorplugin.ui.VideoRotateActivity";
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, clazz));
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.EXTRA_RECORDING_WORKS, works);
        intent.putExtra(Constant.EXTRA_RECORDING_WORKS, bundle);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    private class ProcessCallback implements VSProcessImagesTask.OnProcessCallback {
        private String messageProgress;
        private String messageSuccess;
        private String messageFailed;
        private volatile int mPicCount;

        ProcessCallback(int picCount) {
            mPicCount = picCount;
            messageProgress = getResources().getString(R.string.importProcessImage);
            messageSuccess = getResources().getString(R.string.importProcessImageSuccess);
            messageFailed = getResources().getString(R.string.importProcessImageFailed);
        }

        private void refreshDialogMessage(String message, int index, int totalCount) {
            if (mProcessingDialog != null) {
                String cMessage = String.format(Locale.getDefault(), message, index, totalCount);
                mProcessingDialog.setContent(cMessage);
            }
        }

        @Override
        public void onProcessStart() {
        }

        @Override
        public void onProcessProgress(int index, int status, String srcFile, String destFile) {
            String message;
            switch (status) {
                case VSProcessImagesTask.PROCESS_STATUS_SUCCESS:
//                        message = this.messageSuccess;
//                        this.refreshDialogMessage(message, index, mPicCount);
                    break;
                case VSProcessImagesTask.PROCESS_STATUS_FAILED:
                    message = this.messageFailed;
                    this.refreshDialogMessage(message, index, mPicCount);
                    break;
                case VSProcessImagesTask.PROCESS_STATUS_PROGRESS:
                default:
                    message = this.messageProgress;
                    this.refreshDialogMessage(message, index, mPicCount);
                    break;
            }
        }

        @Override
        public void onProcessSuccess(List<String> destFiles) {
            mProcessingDialog.dismiss();
            nextForImage(destFiles);
        }

        @Override
        public void onProcessFailed() {
            mProcessingDialog.dismiss();
            showMessage(true, R.string.importProcessImageAllFailed);
        }
    }


    @Override
    public void nextForImages(List<VSMedia> images) {
        List<String> strings = new ArrayList<>();
        for (VSMedia media : images) {
            if (media != null) {
                strings.add(media.getPath());
            }
        }
        HashMap<String, Object> moreParams = new HashMap<>();
        moreParams.put("picNum", strings.size());
        StaticsUtil.shootPageClick(2, this.page, SPC_BTN_NEXT, moreParams);

        mProcessingDialog = new ProcessingDialogFragment();
        mProcessingDialog.setOnDialogCreatedListener(new OnDialogCreatedListener() {
            @Override
            public void onDialogCreated() {
            }
        });
        mProcessingDialog.setCancelable(false);
        mProcessingDialog.show(getSupportFragmentManager(), TAG_DIALOG);

        mProcessImageTask = new VSProcessImagesTask(strings, this.shootId) //
                .setCallback(new ProcessCallback(strings.size()));
        mProcessImageTask.execute(this);

    }

    private void nextForImage(List<String> images) {
        //stop player
        VSDraftEntity vsDraftEntity = new VSDraftEntity();
        vsDraftEntity.setSource(VSDraftEntity.Source.PHOTOALBUM);
        vsDraftEntity.setDraftId(this.shootId);
        vsDraftEntity.setShootId(this.shootId);
        vsDraftEntity.setStatus(VSDraftEntity.Status.ROTATE);
        vsDraftEntity.setTopic(this.getTopicId());
        List<Video> list = new ArrayList<>();
        long totalTime = 0;
        final long duration = (long) (ConfigManager.getInstance().getPhotoClipDuration() * 1000);
        for (String image : images) {
            Video video = new Video();
            video.setFilePath(image);
            video.setSource(image);
            video.setDuration(duration);
            video.setRotation(0);
            video.setRatio(0);
            video.setTransform(VideoEditorSupports.Transform.LEFT2RIGHT.code);
            list.add(video);
            totalTime += duration;
        }
        vsDraftEntity.setTotalTime(totalTime);
        vsDraftEntity.setCutPositionStart(0);
        vsDraftEntity.setCutPositionEnd(totalTime);
        vsDraftEntity.setMedias(list);
        String clazz = "com.yixia.videoeditorplugin.ui.VideoEditorActivity";
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, clazz));
        intent.putExtra(Constant.EXTRA_FROM_ALBUM, "extra_from_album");
        intent.putExtra(Constant.EXTRA_DRAFT, vsDraftEntity);
        startActivity(intent);
        VSDraftManager.getInstance().add(vsDraftEntity);
    }

    @Override
    public void sendRunnableDelayed(Runnable runnable, long delayed) {
        this.postDelayed(runnable, delayed);
    }

    private String getTopicId() {
        return getIntent().getStringExtra(Constant.EXTRA_TOPIC_ID);
    }
}