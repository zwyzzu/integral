package com.zhangwy.integral;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerDivider;
import com.yixia.widget.recycler.RecyclerTouchHelper;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.integral.data.IDataManager;
import com.zhangwy.integral.entity.BookingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import yixia.lib.core.base.BaseActivity;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.TimeUtil;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2020-03-20.
 * Updated by zhangwy on 2020-03-20.
 * Description
 */
@SuppressWarnings("unused")
public class BookingElementActivity extends BaseActivity implements RecyclerTouchHelper.OnSwipedListener {

    public static void start(Context context) {
        if (context == null)
            return;
        context.startActivity(new Intent(context, BookingElementActivity.class));
    }

    private VSRecyclerView<BookingEntity> recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_booking_element);
        this.setToolbar();
        this.initRecycler();
        this.reload();
    }

    private void setToolbar() {
        Toolbar toolbar = this.findViewById(R.id.bookingElementToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_booking_element, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookingElementAdd:
                this.add(0,null);
                return true;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecycler() {
        this.recyclerView = this.findViewById(R.id.bookingElementRecyclerView);
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        RecyclerDivider divider = RecyclerDivider.create(this, VSRecyclerView.VERTICAL, 1, Color.TRANSPARENT);
        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.loadData(new ArrayList<>(), new RecyclerAdapter.OnItemLoading<BookingEntity>() {
            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                return getLayoutInflater().inflate(R.layout.view_item_booking_element, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, BookingEntity entity, int position) {
                TextView text = root.findViewById(R.id.bookingElementItemText);
                TextView desc = root.findViewById(R.id.bookingElementItemDesc);
                TextView date = root.findViewById(R.id.bookingElementItemDate);
                text.setText(getString(R.string.element_item_title, entity.getText()));
                desc.setText(getString(R.string.element_item_message, entity.getDesc()));
                date.setText(getString(R.string.element_item_date, TimeUtil.dateMilliSecond2String(entity.getLastUseTime(), TimeUtil.PATTERN_DAY4Y)));
            }
        });
        this.recyclerView.setOnItemClickListener((view, viewType, entity, position) -> add(position, entity));
        ItemTouchHelper touchHelper = new ItemTouchHelper(new RecyclerTouchHelper(this));
        touchHelper.attachToRecyclerView(this.recyclerView);
    }

    private void reload() {
        List<BookingEntity> entities = IDataManager.getInstance().getBookings();
        Collections.sort(entities);
        this.recyclerView.reload(entities);
    }

    private void add(int position, BookingEntity oldEntity) {
        View root = getLayoutInflater().inflate(R.layout.dialog_booking_element, this.recyclerView, false);
        final EditText title = root.findViewById(R.id.elementAddTitle);
        final EditText message = root.findViewById(R.id.elementAddMessage);
        Button cancel = root.findViewById(R.id.elementAddCancel);
        Button ok = root.findViewById(R.id.elementAddOk);
        if (oldEntity != null) {
            title.setText(oldEntity.getText());
            message.setText(oldEntity.getDesc());
        }
        final Dialog dialog = WindowUtil.createAlertDialog(this, 0, root, null, R.string.ok, null, R.string.cancel);
        ok.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            String titleValue = title.getText().toString();
            String messageValue = message.getText().toString();
            BookingEntity entity = new BookingEntity();
            entity.setText(titleValue);
            entity.setDesc(messageValue);
            if (oldEntity == null) {
                entity.setLastUseTime(System.currentTimeMillis());
                IDataManager.getInstance().addBooking(entity);
                recyclerView.add(entity, 0);
            } else {
                entity.setLastUseTime(oldEntity.getLastUseTime());
                entity.setId(oldEntity.getId());
                IDataManager.getInstance().updateBooking(entity);
                recyclerView.replace(entity, position);
            }
        });
        cancel.setOnClickListener(v -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        if (dialog == null) {
            this.showMessage(true, R.string.retry);
        } else {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    public void onSwiped(int position, int direction) {
        Logger.d(String.format(Locale.getDefault(), "onSwiped(%d, %d)", position, direction));
        this.recyclerView.notifyItemChanged(position);
        this.deleteBookingElement(position, this.recyclerView.getData(position));
    }

    private void deleteBookingElement(int position, BookingEntity entity) {
        if (entity == null) {
            return;
        }
        String title = "";
        String message = getString(R.string.booking_element_delete, entity.getText());
        DialogInterface.OnClickListener okListener = (dialog, which) -> {
            if (IDataManager.getInstance().dldBooking(entity.getId())) {
                recyclerView.remove(position);
            }
        };
        WindowUtil.createAlertDialog(this, title, message,
                getString(R.string.ok), okListener,
                getString(R.string.cancel), (dialog, which) -> dialog.dismiss()).show();
    }
}
