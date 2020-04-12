package com.zhangwy.integral;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IBookingManager;
import com.zhangwy.integral.entity.BookingBindEntity;
import com.zhangwy.integral.entity.BookingEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2020-03-19.
 * Updated by zhangwy on 2020-03-19.
 * Description 预订列表
 */
@SuppressWarnings("unused")
public class FragmentBooking extends BaseFragment implements IBookingManager.OnBookingDataListener {

    public static FragmentBooking newInstance() {
        return new FragmentBooking();
    }

    private final int ITEM_TYPE_GROUP = 1;
    private final int ITEM_TYPE_ORDER = 2;
    private VSRecyclerView<MixtureEntity> recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_booking;
    }

    @Override
    protected void init(View root, Bundle saveInstanceState) {
        this.recyclerView = root.findViewById(R.id.bookingRecycler);
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.recyclerView.loadData(new ArrayList<>(), new RecyclerAdapter.OnItemLoading<MixtureEntity>() {

            @Override
            public int getItemViewType(MixtureEntity entity, int position) {
                return entity.getType();
            }

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = R.layout.view_item_booking_order;
                if (viewType == ITEM_TYPE_GROUP) {
                    layout = R.layout.view_item_booking_group;
                }
                return LayoutInflater.from(getContext()).inflate(layout, parent, false);
            }

            @Override
            public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(View root, int viewType) {
                if (viewType == ITEM_TYPE_GROUP) {
                    return new GroupViewHolder(root);
                }
                return new OrderViewHolder(root);
            }

            @Override
            public void onBind(RecyclerAdapter.RecyclerViewHolder holder, int viewType, MixtureEntity entity, int position) {
                switch (viewType) {
                    case ITEM_TYPE_ORDER:
                        ((OrderViewHolder) holder).bind(entity, position);
                        break;
                    case ITEM_TYPE_GROUP:
                        ((GroupViewHolder) holder).bind(entity, position);
                        break;
                }
            }
        });

        this.reloadData();
        this.recyclerView.setOnItemClickListener((view, viewType, entity, position) -> {
            if (viewType == ITEM_TYPE_ORDER) {
                BookingBindEntity bindEntity = entity.bindEntity;
                BookingInfoActivity.start(this.getContext(), bindEntity.getBind(), bindEntity.getId());
            }
        });
        this.setToolbar(root);
    }

    private void setToolbar(View root) {
        Toolbar toolbar = root.findViewById(R.id.bookingToolbar);
        if (getActivity() == null) {
            return;
        }
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        this.setHasOptionsMenu(true);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment_booking, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bookingAdd) {
            BookingActivity.start(getContext());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IBookingManager.getInstance().unRegister(this);
    }

    private void reloadData() {
        IBookingManager.getInstance().register(this);
    }

    @Override
    public void onBookingDataChanged(String memberId) {
        HashMap<String, MixtureEntity> collection = new HashMap<>();
        insertArray(collection, IBookingManager.getInstance().getUnOrder(""));
        insertArray(collection, IBookingManager.getInstance().getOrdered(""));
        insertArray(collection, IBookingManager.getInstance().getInvalid(""));
        List<MixtureEntity> array = new ArrayList<>();
        for (MixtureEntity entity : collection.values()) {
            array.add(entity);
            if (entity.isOpen()) {
                array.addAll(entity.getOrders());
            }
        }
        this.recyclerView.reload(array);
    }

    private void insertArray(HashMap<String, MixtureEntity> collection, List<BookingBindEntity> array) {
        for (BookingBindEntity entity : array) {
            MixtureEntity group = collection.get(entity.getBookingId());
            if (group == null) {
                BookingEntity groupEntity = new BookingEntity();
                groupEntity.setId(entity.getBookingId());
                groupEntity.setText(entity.getText());
                groupEntity.setDesc(entity.getDesc());
                group = new MixtureEntity(groupEntity);
                collection.put(groupEntity.getId(), group);
            }
            group.put(new MixtureEntity(entity));
        }
    }

    @SuppressWarnings("unused")
    private class GroupViewHolder extends RecyclerAdapter.RecyclerViewHolder implements View.OnClickListener {

        private ImageView open;
        private TextView text;
        private View order;
        private View next;
        private MixtureEntity entity;

        private GroupViewHolder(View view) {
            super(view);
            this.open = view.findViewById(R.id.itemBookingGroupOpen);
            this.text = view.findViewById(R.id.itemBookingGroupTitle);
            view.findViewById(R.id.itemBookingGroupOrder).setOnClickListener(this);
            view.findViewById(R.id.itemBookingGroupMore).setOnClickListener(this);
        }

        @Override
        public RecyclerAdapter.RecyclerViewHolder setOnClick(View.OnClickListener listener) {
            return this;
        }

        private void bind(MixtureEntity entity, int position) {
            this.entity = entity;
            this.text.setText(entity.booking.getText());
            this.open.setImageResource(entity.isOpen() ? R.mipmap.icon_minus : R.mipmap.icon_plus);
            this.open.setOnClickListener(v -> {
                entity.open = !entity.isOpen();
                this.open.setImageResource(entity.isOpen() ? R.mipmap.icon_minus : R.mipmap.icon_plus);
                if (entity.isOpen()) {
                    recyclerView.addAll(entity.getOrders(), position + 1);
                } else {
                    recyclerView.remove(position + 1, entity.getOrders().size());
                }
            });
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemBookingGroupOrder:
                    //TODO 批量下单
                    break;
                case R.id.itemBookingGroupMore:
                    //TODO 查看当前预订项预订
                    break;
            }
        }
    }

    private class OrderViewHolder extends RecyclerAdapter.RecyclerViewHolder {

        private SimpleDraweeView icon;
        private TextView bookers;
        private TextView number;
        private TextView created;
        private TextView ordered;
        private TextView address;
        private ImageView mark;
        private View button;

        private OrderViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.itemBookingOrderIcon);
            this.bookers = view.findViewById(R.id.itemBookingOrderBookers);
            this.number = view.findViewById(R.id.itemBookingOrderNumber);
            this.mark = view.findViewById(R.id.itemBookingOrderMark);
            this.created = view.findViewById(R.id.itemBookingOrderCreated);
            this.ordered = view.findViewById(R.id.itemBookingOrderOrdered);
            this.button = view.findViewById(R.id.itemBookingOrderOrderButton);
            this.address = view.findViewById(R.id.itemBookingOrderAddress);
        }

        private void bind(MixtureEntity entity, int position) {
            BookingBindEntity bindEntity = entity.bindEntity;
            refreshThumbnail(bindEntity.getBindIcon(), this.icon);
            this.bookers.setText(bindEntity.getBindName());
            this.number.setText(getResources().getString(R.string.number, bindEntity.getCount()));
            this.created.setText(getResources().getString(R.string.booking_element_created, getTime(bindEntity.getCreateTime())));
            this.ordered.setVisibility(View.GONE);
            this.mark.setVisibility(View.GONE);
            this.button.setOnClickListener(null);
            this.button.setVisibility(View.GONE);
            this.address.setText(bindEntity.getAddressText());
            if (bindEntity.isInvalid()) {
                this.mark.setImageResource(R.mipmap.icon_invalid);
                this.mark.setVisibility(View.VISIBLE);
                this.ordered.setText(getResources().getString(R.string.booking_element_invalid, getTime(bindEntity.getInvalidTime())));
                this.ordered.setVisibility(View.VISIBLE);
            } else if (bindEntity.isOrdered()) {
                this.mark.setImageResource(R.mipmap.icon_ordered);
                this.mark.setVisibility(View.VISIBLE);
                this.ordered.setText(getResources().getString(R.string.booking_element_ordered, getTime(bindEntity.getInvalidTime())));
                this.ordered.setVisibility(View.VISIBLE);
            } else {
                this.button.setVisibility(View.VISIBLE);
                this.button.setOnClickListener(v -> IBookingManager.getInstance().order(getContext(), bindEntity));
            }
        }

        private String getTime(long milliSecond) {
            return TimeUtil.dateMilliSecond2String(milliSecond, TimeUtil.PATTERN_DAY4Y);
        }
    }

    private class MixtureEntity {
        private int type;
        private BookingEntity booking;
        private BookingBindEntity bindEntity;
        private List<MixtureEntity> orders = new ArrayList<>();
        private boolean open = true;

        private MixtureEntity(BookingEntity bookingEntity) {
            this.type = ITEM_TYPE_GROUP;
            this.booking = bookingEntity;
        }

        private MixtureEntity(BookingBindEntity bindEntity) {
            this.type = ITEM_TYPE_ORDER;
            this.bindEntity = bindEntity;
        }

        private int getType() {
            return type;
        }

        private void put(MixtureEntity entity) {
            this.orders.add(entity);
        }

        private List<MixtureEntity> getOrders() {
            return this.orders;
        }

        public boolean isOpen() {
            return open;
        }
    }

    private void refreshThumbnail(String file, SimpleDraweeView cover) {
        Object tag = cover.getTag();
        if (TextUtils.isEmpty(file)) {
            cover.setImageResource(this.getAvatar());
            return;
        }
        if (file.equals(tag)) {
            return;
        }
        Uri uri = Uri.fromFile(new File(file));
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(cover.getController())
                .setImageRequest(request)
                .build();
        cover.setController(controller);
        cover.setTag(file);
    }

    private @DrawableRes
    int getAvatar() {
        Activity activity = getActivity();
        if (activity == null) {
            return R.drawable.avatar_ff7f50;
        }
        Resources resources = activity.getResources();
        int[] avatars = resources.getIntArray(R.array.avatar);
        if (Util.arrayEmpty(avatars)) {
            return R.drawable.avatar_ff8c00;
        }
        int maxIndex = avatars.length - 1;
        int index = (int) (maxIndex * Math.random());
        if (index > maxIndex) {
            index = maxIndex;
        }
        TypedArray array = null;
        try {
            array = resources.obtainTypedArray(R.array.avatar);
            return array.getResourceId(index, 0);
        } finally {
            if (array != null) {
                array.recycle();
            }
        }
    }
}
