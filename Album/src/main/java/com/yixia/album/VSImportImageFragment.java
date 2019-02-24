package com.yixia.album;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yixia.album.model.VSFolder;
import com.yixia.album.model.VSMedia;
import com.yixia.album.presenter.VSImportImagePresenter;
import com.yixia.album.view.VSImportAlbumView;
import com.yixia.video.business.api.clientRemote.StaticsUtil;
import com.yixia.video.business.media.edit.VideoEditManager;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerDivider;
import com.yixia.widget.recycler.VSRecyclerView;

import org.qcode.qskinloader.SkinManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Screen;
import yixia.lib.core.util.Util;
import yixia.lib.core.widget.tooltip.Tooltip;
import yixia.lib.core.widget.tooltip.TooltipAnimation;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnImportListener} interface
 * to handle interaction events.
 * Use the {@link VSImportImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by zhangwy on 2018/10/17 下午4:34.
 * Updated by zhangwy on 2018/10/17 下午4:34.
 * Description 图片相册导入
 */
@SuppressWarnings("unused")
public class VSImportImageFragment extends BaseFragment implements
        VSImportAlbumView<VSFolder, VSMedia>, RecyclerAdapter.OnItemClickListener<VSMedia> {
    private static final String TAG = VSImportImageFragment.class.getSimpleName();
    private static final String SP_KEY_HINT_COUNT = "sp_key_import_image_hint_count";
    private VSImportImagePresenter albumPresenter;
    private VSRecyclerView<VSMedia> recyclerMedia;
    private VSRecyclerView<VSMedia> checkedMedia;
    private View bottom;
    private View noDateTip;
    private View dragAnchor;
    private TextView hint;
    private Tooltip tooltip;
    private int length = 200;

    public static VSImportImageFragment newInstance() {
        VSImportImageFragment fragment = new VSImportImageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_import_image;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.noDateTip = view.findViewById(R.id.importAlbumImagesTip);
        this.dragAnchor = view.findViewById(R.id.importAlbumTipAnchor);
        this.hint = view.findViewById(R.id.importAlbumImageHint);
        this.length = Screen.getScreenWidth(this.getContext()) / 4;
        this.initBottom(view);
        this.initAllMedias(view);
        this.albumPresenter = new VSImportImagePresenter(getContext(), this);
        this.albumPresenter.requestAlbums(getContext());
        this.loading();
        StaticsUtil.shootPageShow(2, StaticsUtil.PAGE_ID_ALBUM_PHOTO);
    }

    private void initBottom(View view) {
        this.checkedMedia = view.findViewById(R.id.importAlbumCheckedImages);
        this.checkedMedia.setLinearLayoutManager(VSRecyclerView.HORIZONTAL, false);
        this.checkedMedia.loadData(new ArrayList<VSMedia>(), new LoadingCheckedItem());
        this.bottom = view.findViewById(R.id.importAlbumImageBottom);
        this.bottom.setVisibility(View.GONE);
        view.findViewById(R.id.importAlbumImageNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextForImages();
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(new CheckedTouchHelper());
        helper.attachToRecyclerView(this.checkedMedia);
    }

    private void initAllMedias(View view) {
        this.recyclerMedia = view.findViewById(R.id.importAlbumImages);
        this.recyclerMedia.setGridLayoutManager(4, VSRecyclerView.VERTICAL, false);
        this.recyclerMedia.addItemDecoration(new RecyclerDivider(this.getContext(), //
                LinearLayoutManager.VERTICAL, 1, //
                this.getResources().getColor(android.R.color.transparent)));
        this.recyclerMedia.addItemDecoration(new RecyclerDivider(this.getContext(), //
                LinearLayoutManager.HORIZONTAL, 1, //
                this.getResources().getColor(android.R.color.transparent)));
        this.recyclerMedia.loadData(new ArrayList<VSMedia>(), new LoadingImageItem());
        this.recyclerMedia.setOnItemClickListener(this);
    }

    public List<VSMedia> getChecked() {
        if (this.checkedMedia == null) {
            return new ArrayList<>();
        }
        return this.checkedMedia.getData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.albumPresenter = null;
    }

    @Override
    public void refreshMedia(VSFolder folder, List<VSMedia> medias) {
        this.recyclerMedia.reload(medias);
        if (this.noDateTip != null) {
            this.noDateTip.setVisibility(Util.isEmpty(medias) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void refreshFolder(List<VSFolder> folders) {
        this.loadEnd();
        boolean empty = Util.isEmpty(folders);
        if (this.noDateTip != null) {
            this.noDateTip.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        if (!empty) {
            this.loadHint();
        }
    }

    private void loadHint() {
        if (this.hint == null) {
            return;
        }
        PreferencesHelper.defaultInstance().init(this.getContext());
        int count = PreferencesHelper.defaultInstance().getInt(this.SP_KEY_HINT_COUNT, 0);
        if (count < 3) {
            this.hint.setVisibility(View.VISIBLE);
            this.hint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeHint();
                }
            });
            OnImportListener listener = this.callActivityListener();
            if (listener != null) {
                listener.sendRunnableDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeHint();
                    }
                }, 3000);
            }
        }
    }

    private void closeHint() {
        if (this.hint == null) {
            return;
        }
        this.hint.setVisibility(View.GONE);
        int count = PreferencesHelper.defaultInstance().getInt(this.SP_KEY_HINT_COUNT, 0);
        PreferencesHelper.defaultInstance().applyInt(this.SP_KEY_HINT_COUNT, count + 1);
    }

    @Override
    public void onItemClick(View view, int viewType, VSMedia entity, int position) {
        OnImportListener listener = callActivityListener();
        if (listener != null) {
            listener.showPreview(view, entity);
        }
    }

    public void loading() {
        OnImportListener listener = callActivityListener();
        if (listener != null) {
            listener.loading();
        }
    }

    public void loadEnd() {
        OnImportListener listener = callActivityListener();
        if (listener != null) {
            listener.loadEnd();
        }
    }

    private boolean checkedImage(VSMedia media, boolean checked, int position, boolean refreshAll) {
        if (checked) {
            if (this.checkedMedia.has(media)) {
                return false;
            }
            if (this.checkedMedia.getCount() >= 10) {
                this.showMessage(true, true, R.string.importAlbumImageCheckedCountMax);
                return false;
            }
        } else {
            if (!this.checkedMedia.has(media)) {
                return false;
            }
        }

        media.setChecked(checked);
        if (position < 0) {
            if (refreshAll) {
                this.recyclerMedia.notifyDataSetChanged();
            }
        } else {
            media.setPosition(position);
            if (refreshAll) {
                this.recyclerMedia.notifyItemChanged(position);
            }
        }
        if (checked) {
            this.checkedMedia.add(media);
            this.checkedMedia.scrollToPosition(this.checkedMedia.getCount() - 1);
            if (this.checkedMedia.getCount() == 2) {
                dragTip();
            }
        } else {
            this.checkedMedia.remove(media);
        }
        this.bottom.setVisibility(this.checkedMedia.getCount() <= 0 ? View.GONE : View.VISIBLE);
        return true;
    }

    private static final String SP_KEY_HINT_DRAG = "sp_key_import_image_hint_drag";
    private void dragTip() {
        if (PreferencesHelper.defaultInstance().getBoolean(SP_KEY_HINT_DRAG, false)) {
            return;
        }
        PreferencesHelper.defaultInstance().applyBoolean(SP_KEY_HINT_DRAG, true);
        ViewGroup rootView = (ViewGroup) this.mRootView;
        TextView tipText = (TextView) getLayoutInflater().inflate(R.layout.layout_bubble_tip, //
                rootView, false);
        tipText.setText(R.string.importAlbumImageDrag);
        tipText.setTextSize(14);
        tooltip = new Tooltip.Builder(getActivity())
                .anchor(this.dragAnchor, Tooltip.TOP)
                .animate(new TooltipAnimation(TooltipAnimation.SCALE_AND_FADE, 400))
                .autoAdjust(true)
                .withPadding(Screen.dip2px(getContext(), 6))
                .content(tipText)
                .autoCancel(3000)
                .cancelable(false)
                .into(rootView)
                .show();
    }

    private void nextForImages() {
        VideoEditManager.renderDestroy();
        VideoEditManager.destroyEngine();
        List<VSMedia> images = getChecked();
        if (Util.isEmpty(images)) {
            this.showMessage(true, true, R.string.importAlbumImageCheckedCountMin);
            return;
        }
        OnImportListener listener = callActivityListener();
        if (listener == null) {
            return;
        }
        listener.nextForImages(images);
    }

    private OnImportListener callActivityListener() {
        Activity activity = this.getActivity();
        if (activity != null && activity instanceof OnImportListener) {
            return  (OnImportListener) activity;
        }
        return null;
    }

    private class LoadingCheckedItem extends RecyclerAdapter.OnItemLoading<VSMedia> {

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(//
                    R.layout.view_item_checked_image, parent, false);
            SkinManager.getInstance().applySkin(view, true);
            return view;
        }

        @Override
        public void onLoadView(View root, int viewType, VSMedia entity, int position) {
            SimpleDraweeView cover = root.findViewById(R.id.itemCheckedImageCover);
            refreshThumbnail(entity.getThumbnail(), cover);
            ImageView deleted = root.findViewById(R.id.itemCheckedDelete);
            deleted.setOnClickListener(this.createOnClickListener(entity));
            TextView positionText = root.findViewById(R.id.itemCheckedPosition);
            positionText.setText(String.valueOf(position + 1));
            SkinManager.getInstance().applySkin(root, true);
        }

        private View.OnClickListener createOnClickListener(final VSMedia media) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedImage(media, false, media.getPosition(), true);
                }
            };
        }
    }

    private class LoadingImageItem extends RecyclerAdapter.OnItemLoading<VSMedia> {

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(//
                    R.layout.view_item_import_image, parent, false);
            SkinManager.getInstance().applySkin(view, true);
            return view;
        }

        @Override
        public void onLoadView(View root, int viewType, final VSMedia entity, final int position) {
            SimpleDraweeView cover = root.findViewById(R.id.itemImportImageCover);
            refreshThumbnail(entity.getThumbnail(), cover);
            CheckBox checkBox = root.findViewById(R.id.itemImportImageStatus);
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(entity.isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!checkedImage(entity, isChecked, position, false)) {
                        buttonView.setOnCheckedChangeListener(null);
                        buttonView.setChecked(!isChecked);
                        buttonView.setOnCheckedChangeListener(this);
                    }
                }
            });
            SkinManager.getInstance().applySkin(root, true);
        }
    }

    private void refreshThumbnail(String file, SimpleDraweeView cover) {
        Object tag = cover.getTag();
        if (TextUtils.isEmpty(file) || file.equals(tag)) {
            return;
        }
        Uri uri = Uri.fromFile(new File(file));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(this.length, this.length))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(cover.getController())
                .setImageRequest(request)
                .build();
        cover.setController(controller);
        cover.setTag(file);
    }

    private class CheckedTouchHelper extends ItemTouchHelper.Callback {

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN //
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, //
                              RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Logger.d(TAG, "fromPosition:" + fromPosition + "-toPosition:" + toPosition);
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(getChecked(), i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(getChecked(), i, i - 1);
                }
            }
            checkedMedia.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Logger.d(TAG, "onSwiped:" + direction + "");
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//            Logger.d(TAG, "onSelectedChanged:" + actionState + "");
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                this.refreshShade(viewHolder, true);
                if (tooltip != null) {
                    tooltip.dismiss();
                }

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    //获取系统震动服务
                    Vibrator vib = (Vibrator) getContext() //
                            .getSystemService(Service.VIBRATOR_SERVICE);
                    if (vib != null) {
                        vib.vibrate(40);
                    }
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            this.refreshShade(viewHolder, false);
            checkedMedia.notifyDataSetChanged();
        }

        private void refreshShade(RecyclerView.ViewHolder holder, boolean show) {
            if (holder == null) {
                return;
            }
            View itemView = holder.itemView;
            if (itemView == null) {
                return;
            }
            View shade = itemView.findViewById(R.id.itemCheckedImageShade);
            if (shade == null) {
                return;
            }
            shade.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
