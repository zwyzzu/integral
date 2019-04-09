package com.zhangwy.address;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.address.entities.AddressEntity;
import com.zhangwy.address.entities.AreaEntity;
import com.zhangwy.address.entities.CityEntity;
import com.zhangwy.address.entities.ProvinceEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yixia.lib.core.util.FileUtil;
import yixia.lib.core.util.Screen;
import yixia.lib.core.util.Util;

@SuppressWarnings("unused")
public class AreaPickerView extends Dialog {

    private Gson gson = new Gson();
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Callback callback;
    private List<View> views = new ArrayList<>();
    private List<String> strings = new ArrayList<>();

    private List<ProvinceEntity> provinces; //省、直辖市
    private List<CityEntity> cities = new ArrayList<>(); //县市
    private List<AreaEntity> areas = new ArrayList<>(); //区县
    private List<AddressEntity> towns = new ArrayList<>(); //镇、街道、办事处

    private ViewPagerAdapter viewPagerAdapter;

    private int lastProvincePosition = -1;
    private int lastCityPosition = -1;
    private int lastAreaPosition = -1;
    private int lastTownPosition = -1;

    private VSRecyclerView<ProvinceEntity> provinceRecycler;
    private VSRecyclerView<CityEntity> cityRecycler;
    private VSRecyclerView<AreaEntity> areaRecycler;
    private VSRecyclerView<AddressEntity> townRecycler;

    private final int colorCheckedTrue = Color.parseColor("#65C15C");
    private final int colorCheckedFalse = Color.parseColor("#444444");
    private final String unCheckedLabel;

    private final int screenHeight;
    public AreaPickerView(@NonNull Activity activity, int themeResId) {
        super(activity, themeResId);
        String json = FileUtil.readAssets(activity, "areas.json");
        this.provinces = this.gson.fromJson(json, new TypeToken<List<ProvinceEntity>>(){}.getType());
        this.screenHeight = Screen.getScreenHeight(activity);
        this.unCheckedLabel = activity.getString(R.string.unCheckedLabel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_address);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
            window.setWindowAnimations(R.style.PickerAnim);
        }

        ViewGroup root = findViewById(R.id.addressRoot);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        params.height = screenHeight * 3 / 4;
        root.setLayoutParams(params);

        this.tabLayout = findViewById(R.id.addressTabLayout);
        this.viewPager = findViewById(R.id.addressViewPager);
        this.findViewById(R.id.addressClose).setOnClickListener(view -> dismiss());

        View provinceView = LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview, root, false);
        View cityView = LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview, root, false);
        View areaView = LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview, root, false);
        View townView = LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview, root, false);

        this.provinceRecycler = provinceView.findViewById(R.id.recycler);
        this.cityRecycler = cityView.findViewById(R.id.recycler);
        this.areaRecycler = areaView.findViewById(R.id.recycler);
        this.townRecycler = townView.findViewById(R.id.recycler);

        this.views.add(provinceView);
        this.views.add(cityView);
        this.views.add(areaView);
        this.views.add(townView);

        this.viewPagerAdapter = new ViewPagerAdapter();
        this.viewPager.setAdapter(this.viewPagerAdapter);
        this.tabLayout.setupWithViewPager(this.viewPager);

        this.initProvince();
        this.initCity();
        this.initArea();
        this.initTown();

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        scrollToPosition(provinceRecycler, lastProvincePosition);
                        break;
                    case 1:
                        scrollToPosition(cityRecycler, lastCityPosition);
                        break;
                    case 2:
                        scrollToPosition(areaRecycler, lastAreaPosition);
                        break;
                    case 3:
                        scrollToPosition(townRecycler, lastTownPosition);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        {//自动切到省列
            this.strings.add(this.unCheckedLabel);
            this.tabLayout.setupWithViewPager(viewPager);
            this.viewPagerAdapter.notifyDataSetChanged();
            this.switchTabLayout(0);
        }
    }

    private void initProvince() {
        this.provinceRecycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.provinceRecycler.loadData(this.provinces, new AreaRecyclerLoading<>());
        this.provinceRecycler.setOnItemClickListener((view, viewType, entity, position) -> {
            if (this.lastProvincePosition != position) {
                this.clearChecked(1);
                entity.setChecked(true);
                this.cities.clear();
                this.areas.clear();
                this.towns.clear();
                this.cities.addAll(entity.getCity());
                this.provinceRecycler.notifyDataSetChanged();
                this.cityRecycler.reload(this.cities);
                this.areaRecycler.reload(this.areas);
                this.townRecycler.reload(this.towns);
                this.strings.set(0, entity.getName());
                this.removeTabs(1);
                this.strings.add(this.unCheckedLabel);
            }
            this.tabLayout.setupWithViewPager(viewPager);
            this.viewPagerAdapter.notifyDataSetChanged();
            this.switchTabLayout(1);
            this.lastProvincePosition = position;
        });
    }

    private void initCity() {
        this.cityRecycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.cityRecycler.loadData(this.cities, new AreaRecyclerLoading<>());
        this.cityRecycler.setOnItemClickListener((view, viewType, entity, position) -> {
            if (this.lastCityPosition != position) {
                this.clearChecked(2);
                entity.setChecked(true);
                this.cityRecycler.notifyDataSetChanged();
                this.strings.set(1, entity.getName());
                this.removeTabs(2);
                this.areas.clear();
                this.towns.clear();
                if (!Util.isEmpty(entity.getArea())) {
                    this.areas.addAll(entity.getArea());
                    this.areaRecycler.reload(this.areas);
                    this.townRecycler.reload(this.towns);
                    this.strings.add(this.unCheckedLabel);
                }
            }
            this.lastCityPosition = position;
            this.tabLayout.setupWithViewPager(viewPager);
            this.viewPagerAdapter.notifyDataSetChanged();
            if (Util.isEmpty(entity.getArea())) {
                dismiss();
                this.callback.callback(this.provinces.get(this.lastProvincePosition), entity);
            } else {
                this.switchTabLayout(2);
            }
        });
    }

    private void initArea() {
        this.areaRecycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.areaRecycler.loadData(this.areas, new AreaRecyclerLoading<>());
        this.areaRecycler.setOnItemClickListener((view, viewType, entity, position) -> {
            if (this.lastAreaPosition != position) {
                this.clearChecked(3);
                entity.setChecked(true);
                this.areaRecycler.notifyDataSetChanged();
                this.strings.set(2, entity.getName());
                this.removeTabs(3);
                this.towns.clear();
                List<AddressEntity> rTowns = readTowns(entity.getCode());
                if (!Util.isEmpty(rTowns)) {
                    entity.setTown(rTowns);
                    this.towns.addAll(rTowns);
                    this.townRecycler.reload(this.towns);
                    this.strings.add(this.unCheckedLabel);
                }
            }
            this.lastAreaPosition = position;
            this.tabLayout.setupWithViewPager(viewPager);
            this.viewPagerAdapter.notifyDataSetChanged();
            if (Util.isEmpty(entity.getTown())) {
                dismiss();
                this.callback.callback(this.provinces.get(this.lastProvincePosition), this.cities.get(this.lastCityPosition), entity);
            } else {
                this.switchTabLayout(3);
            }
        });
    }

    private void initTown() {
        this.townRecycler.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        this.townRecycler.loadData(this.towns, new AreaRecyclerLoading<>());
        this.townRecycler.setOnItemClickListener((view, viewType, entity, position) -> {
            if (this.lastTownPosition != position) {
                this.clearChecked(4);
                entity.setChecked(true);
                this.areaRecycler.notifyDataSetChanged();
                this.strings.set(3, entity.getName());
                this.removeTabs(4);
            }
            this.lastTownPosition = position;
            this.tabLayout.setupWithViewPager(viewPager);
            this.viewPagerAdapter.notifyDataSetChanged();
            this.dismiss();
            this.callback.callback(this.provinces.get(this.lastProvincePosition),
                    this.cities.get(this.lastCityPosition),
                    this.areas.get(this.lastAreaPosition), entity);
        });
    }

    private void switchTabLayout(int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.select();
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings.get(position);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }
    }

    public interface Callback {
        void callback(AddressEntity... value);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void show(AddressEntity... value) {
        super.show();
        this.clearChecked(1);
        this.cities.clear();
        this.areas.clear();
        this.towns.clear();
        this.strings.clear();
        this.resetData(value);
        if (this.strings.size() < 4) {
            this.strings.add(this.unCheckedLabel);
        }
        this.provinceRecycler.notifyDataSetChanged();
        this.cityRecycler.reload(this.cities);
        this.areaRecycler.reload(this.areas);
        this.townRecycler.reload(this.towns);
        this.scrollToPosition(this.provinceRecycler, this.lastProvincePosition);
        this.scrollToPosition(this.cityRecycler, this.lastCityPosition);
        this.scrollToPosition(this.areaRecycler, this.lastAreaPosition);
        this.scrollToPosition(this.townRecycler, this.lastTownPosition);

        this.tabLayout.setupWithViewPager(this.viewPager);
        this.viewPagerAdapter.notifyDataSetChanged();
        this.switchTabLayout(value == null ? 0 : (value.length - 1));
    }

    private void resetData(AddressEntity[] values) {
        if (values == null || values.length == 0) {
            return;
        }
        ProvinceEntity province = getEntity(this.provinces, values[0]);
        if (province == null) {
            return;
        }
        province.setChecked(true);
        this.lastProvincePosition = this.provinces.indexOf(province);
        this.strings.add(province.getName());
        this.cities.addAll(province.getCity());

        if (values.length < 2) {
            return;
        }
        CityEntity city = getEntity(this.cities, values[1]);
        if (city == null) {
            return;
        }
        city.setChecked(true);
        this.lastCityPosition = this.cities.indexOf(city);
        this.strings.add(city.getName());
        this.areas.addAll(city.getArea());

        if (values.length < 3) {
            return;
        }
        AreaEntity area = getEntity(this.areas, values[2]);
        if (area == null) {
            return;
        }
        area.setChecked(true);
        this.lastAreaPosition = this.areas.indexOf(area);
        this.strings.add(area.getName());
        area.setTown(this.readTowns(area.getCode()));
        this.towns.addAll(area.getTown());

        if (values.length < 4) {
            return;
        }
        AddressEntity town = getEntity(this.towns, values[3]);
        if (town == null) {
            return;
        }
        town.setChecked(true);
        this.lastTownPosition = this.towns.indexOf(town);
        this.strings.add(town.getName());
    }

    private <T extends AddressEntity> T getEntity(List<T> array, AddressEntity address) {
        if (Util.isEmpty(array) || address == null) {
            return null;
        }
        for (T entity: array) {
            if (entity == null) {
                continue;
            }
            if (TextUtils.equals(address.getName(), entity.getName())) {
                return entity;
            }
        }
        return null;
    }

    private class AreaRecyclerLoading<T extends AddressEntity> extends RecyclerAdapter.OnItemLoading<T> {

        @Override
        public View onCreateView(ViewGroup parent, int viewType) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_address, parent, false);
        }

        @Override
        public void onLoadView(View root, int viewType, T entity, int position) {
            TextView textView = root.findViewById(R.id.textview);
            textView.setText(entity.getName());
            textView.setTextColor(entity.isChecked() ? colorCheckedTrue : colorCheckedFalse);
        }
    }

    private void clearChecked(int level) {
        if (this.lastProvincePosition != -1) {
            ProvinceEntity province = this.provinces.get(this.lastProvincePosition);
            if (level < 2) {
                province.setChecked(false);
                this.lastProvincePosition = -1;
            }
            if (this.lastCityPosition != -1) {
                CityEntity city = province.getCity().get(this.lastCityPosition);
                if (level < 3) {
                    city.setChecked(false);
                    this.lastCityPosition = -1;
                }
                if (this.lastAreaPosition != -1) {
                    AreaEntity area = city.getArea().get(this.lastAreaPosition);
                    if (level < 4) {
                        area.setChecked(false);
                        this.lastAreaPosition = -1;
                    }
                    if (this.lastTownPosition != -1) {
                        if (level < 5) {
                            area.getTown().get(this.lastTownPosition).setChecked(false);
                            if (level < 4) {
                                area.getTown().clear();
                            }
                            this.lastTownPosition = -1;
                        }
                    }
                }
            }
        }
    }

    private void removeTabs(int startIndex) {
        if (startIndex >= this.strings.size()) {
            return;
        }
        ArrayList<String> backup = new ArrayList<>(this.strings);
        this.strings.clear();
        for (int i = 0; i < startIndex; i++) {
            this.strings.add(backup.get(i));
        }
    }

    private void scrollToPosition(RecyclerView recycler, int position) {
        if (recycler == null)
            return;
        recycler.scrollToPosition(position < 0 ? 0 : position);
    }

    private List<AddressEntity> readTowns(String code) {
        String file = "town/%s.json";
        file = String.format(Locale.getDefault(), file, code);
        String json = FileUtil.readAssets(getContext(), file);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        return this.gson.fromJson(json, new TypeToken<List<AddressEntity>>(){}.getType());
    }
}
