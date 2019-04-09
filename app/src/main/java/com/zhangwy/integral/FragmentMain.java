package com.zhangwy.integral;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.MemberEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.Screen;

/**
 * Created by zhangwy on 2018/12/21 下午8:10.
 * Updated by zhangwy on 2018/12/21 下午8:10.
 * Description
 */
public class FragmentMain extends BaseFragment {

    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    private final int REQUEST_CODE_MEMBER = 100;
    private final int spanCount = 3;
    private int length = 200;
    private VSRecyclerView<MemberEntity> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.mainRecycler);
        int divisionSize = getResources().getDimensionPixelOffset(R.dimen.padding_15);
        this.length = (Screen.getScreenWidth(getContext()) - divisionSize * (spanCount + 1)) / spanCount;
        this.recyclerView.setGridLayoutManager(spanCount, VSRecyclerView.VERTICAL, false);
        this.recyclerView.loadData(new ArrayList<>(), new RecyclerAdapter.OnItemLoading<MemberEntity>() {

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return LayoutInflater.from(getContext()).inflate(R.layout.view_item_main, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, MemberEntity entity, int position) {
                SimpleDraweeView icon = root.findViewById(R.id.mainItemIcon);
                TextView name = root.findViewById(R.id.mainItemName);
                TextView integral = root.findViewById(R.id.mainItemIntegral);
                refreshThumbnail(entity.getIcon(), icon);
                name.setText(entity.getName());
                integral.setText(String.valueOf(entity.getIntegral()));
            }
        });
        this.reloadData();
        this.recyclerView.setOnItemClickListener((view1, viewType, entity, position) -> MemberActivity.start(FragmentMain.this, entity.getId(), REQUEST_CODE_MEMBER));
    }

    private void reloadData() {
        List<MemberEntity> members = IDataManager.getInstance().getMembers();
        Collections.sort(members, (o1, o2) -> o2.getIntegral() - o1.getIntegral());
        this.recyclerView.reload(members);
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
}
