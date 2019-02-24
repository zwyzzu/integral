package com.yixia.album;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yixia.album.model.VSFolder;
import com.yixia.album.model.VSVideo;
import com.yixia.album.presenter.VSImportVideoPresenter;
import com.yixia.album.view.VSImportAlbumView;
import com.yixia.video.business.api.clientRemote.StaticsUtil;
import com.yixia.video.business.media.util.AndroidUtils;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerDivider;
import com.yixia.widget.recycler.VSRecyclerView;

import org.qcode.qskinloader.SkinManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.InputEventUtils;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnImportListener} interface
 * to handle interaction events.
 * Use the {@link VSImportVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Created by zhangwy on 2018/10/17 下午4:34.
 * Updated by zhangwy on 2018/10/17 下午4:34.
 * Description 图片相册导入
 */
@SuppressWarnings("unused")
public class VSImportVideoFragment extends BaseFragment //
        implements VSImportAlbumView<VSFolder, VSVideo>, //
        RecyclerAdapter.OnItemClickListener<VSVideo> {

    private VSImportVideoPresenter albumPresenter;
    private VSRecyclerView<VSVideo> recyclerMedia;
    private View noDateTip;

    public static VSImportVideoFragment newInstance() {
        VSImportVideoFragment fragment = new VSImportVideoFragment();
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
        return R.layout.fragment_import_video;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.noDateTip = view.findViewById(R.id.importAlbumVideosTip);
        this.recyclerMedia = view.findViewById(R.id.importAlbumVideos);
        this.recyclerMedia.setGridLayoutManager(4, VSRecyclerView.VERTICAL, false);
        this.recyclerMedia.addItemDecoration(new RecyclerDivider(this.getContext(), //
                LinearLayoutManager.VERTICAL, 1, //
                this.getResources().getColor(android.R.color.transparent)));
        this.recyclerMedia.addItemDecoration(new RecyclerDivider(this.getContext(), //
                LinearLayoutManager.HORIZONTAL, 1, //
                this.getResources().getColor(android.R.color.transparent)));
        this.recyclerMedia.loadData(new ArrayList<VSVideo>(), new LoadingMediaItem());
        this.recyclerMedia.setOnItemClickListener(this);

        this.albumPresenter = new VSImportVideoPresenter(getContext(), this);
        this.albumPresenter.requestAlbums(getContext());
        this.loading();
        StaticsUtil.shootPageShow(2, "10");
    }

    @Override
    public void onItemClick(View view, int viewType, VSVideo entity, int position) {
        boolean isRepeatClick = InputEventUtils.isRepeatClick(view.getId() + "", 400);
        if (isRepeatClick) {
            return;
        }
        if (entity.getDuration() < 3000) {
            showMessage(true, R.string.importAlbumLessThan3s);
            return;
        }
        //check the format is supported
        int[] dimension = AndroidUtils.getVideoDimension(entity.getPath());
        if (Math.min(dimension[0], dimension[1]) >= 2160 //
                && Math.max(dimension[0], dimension[1]) >= 3840) {
            showMessage(true, R.string.importAlbumFormatUnsupport);
            return;
        }
        this.toNext(entity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnImportListener)) {
            Logger.d("can't callback");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.albumPresenter = null;
    }

    @Override
    public void refreshMedia(VSFolder folder, List<VSVideo> medias) {
        this.recyclerMedia.reload(medias);
        if (this.noDateTip != null) {
            this.noDateTip.setVisibility(Util.isEmpty(medias) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void refreshFolder(List<VSFolder> folders) {
        this.loadEnd();
        if (this.noDateTip != null) {
            this.noDateTip.setVisibility(Util.isEmpty(folders) ? View.VISIBLE : View.GONE);
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

    public void toNext(VSVideo video) {
        OnImportListener listener = callActivityListener();
        if (listener != null) {
            listener.nextForVideo(video);
        }
    }

    private OnImportListener callActivityListener() {
        Activity activity = this.getActivity();
        if (activity instanceof OnImportListener) {
            return  (OnImportListener) activity;
        }
        return null;
    }

    private class LoadingMediaItem extends RecyclerAdapter.OnItemLoading<VSVideo> {

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(//
                    R.layout.view_item_import_album, parent, false);
            SkinManager.getInstance().applySkin(view, true);
            return view;
        }

        @Override
        public void onLoadView(View root, int viewType, VSVideo entity, int position) {
            SimpleDraweeView cover = root.findViewById(R.id.itemImportAlbumCover);
            cover.setImageURI(Uri.parse(String.format(Locale.getDefault(), //
                    "file://%s", entity.getThumbnail())));
            TextView duration = root.findViewById(R.id.itemImportAlbumDuration);
            duration.setText(TimeUtil.dateMilliSecond2String(entity.getDuration(), //
                    TimeUtil.PATTERN_MMSS));
            SkinManager.getInstance().applySkin(root, true);
        }
    }
}
