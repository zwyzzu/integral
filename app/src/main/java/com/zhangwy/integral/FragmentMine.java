package com.zhangwy.integral;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pgyersdk.feedback.PgyFeedback;
import com.yixia.widget.recycler.RecyclerAdapter;
import com.yixia.widget.recycler.RecyclerAdapter.OnItemClickListener;
import com.yixia.widget.recycler.VSRecyclerView;
import com.zhangwy.common.Common;
import com.zhangwy.common.ErrorMessage;
import com.zhangwy.common.entities.IUserEntity;
import com.zhangwy.user.IUser;
import com.zhangwy.user.auth.AuthCallback;
import com.zhangwy.user.auth.AuthLogin;
import com.zhangwy.user.auth.AuthWeiXin;

import java.util.List;

import yixia.lib.core.base.BaseFragment;
import yixia.lib.core.exception.CodeException;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Device;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2018/12/21 下午8:13.
 * Updated by zhangwy on 2018/12/21 下午8:13.
 * Description
 */
@SuppressWarnings("unused")
public class FragmentMine extends BaseFragment implements OnItemClickListener<FragmentMine.MineItem>, View.OnClickListener, AuthCallback, IUser.LoginCallBack {

    public static FragmentMine newInstance() {
        return new FragmentMine();
    }

    public static final int REQUESTCODE = 101;
    private SimpleDraweeView icon;
    private TextView nickName;
    private TextView message;
    private Button login;
    private VSRecyclerView<MineItem> recyclerView;
    private AuthLogin<?> authLogin;
    private long lastAuth = 0;
    private Dialog loginingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void init(View view, Bundle saveInstanceState) {
        this.recyclerView = view.findViewById(R.id.mineRecyclerView);
        this.icon = view.findViewById(R.id.mineIcon);
        this.nickName = view.findViewById(R.id.mineNickname);
        this.message = view.findViewById(R.id.mineMessage);
        this.login = view.findViewById(R.id.mineLogin);
        this.icon.setImageResource(R.mipmap.icon_avatar);
        this.reloadItem();
        this.refreshLogin();
        this.login.setOnClickListener(this);
    }

    private void refreshLogin() {
        if (this.isLogin()) {
            this.nickName.setVisibility(View.VISIBLE);
            this.message.setVisibility(View.VISIBLE);
            this.login.setVisibility(View.INVISIBLE);
        } else {
            this.nickName.setVisibility(View.INVISIBLE);
            this.message.setVisibility(View.INVISIBLE);
            this.login.setVisibility(View.VISIBLE);
        }
        this.refreshUser();
    }

    private void refreshUser() {
        if (!this.isLogin()) {
            return;
        }
        IUserEntity userEntity = IUser.getInstance().getUserInfo();

        if (userEntity == null) {
            return;
        }
        this.nickName.setText(userEntity.getNickName());
        this.message.setText(userEntity.getMessage());
        this.icon.setImageURI(userEntity.getIcon());
    }

    private void reloadItem() {
        this.recyclerView.setLinearLayoutManager(VSRecyclerView.VERTICAL, false);
        List<MineItem> array = Util.array2List(MineItem.values());
        this.recyclerView.loadData(array, new RecyclerAdapter.OnItemLoading<MineItem>() {

            @Override
            public int getItemViewType(MineItem entity, int position) {
                return entity.viewType;
            }

            @Override
            public View onCreateView(ViewGroup parent, int viewType) {
                int layout = R.layout.view_item_mine;
                switch (viewType) {
                    case 0:
                        layout = R.layout.view_item_mine;
                        break;
                    case 1:
                        layout = R.layout.view_item_mine1;
                        break;
                    case 2:
                        layout = R.layout.view_item_mine2;
                        break;
                }
                return LayoutInflater.from(getContext()).inflate(layout, parent, false);
            }

            @Override
            public void onLoadView(View root, int viewType, MineItem entity, int position) {
                TextView textView = root.findViewById(R.id.mineItemTitle);
                textView.setText(entity.res);
                if (viewType == 1) {
                    SwitchCompat switchCompat = root.findViewById(R.id.mineItemSwitch);
                    switchCompat.setChecked(PreferencesHelper.defaultInstance().getBoolean(Common.PFRC_SHOW_MEMBER_AVATAR, true));
                    switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesHelper.defaultInstance().applyBoolean(Common.PFRC_SHOW_MEMBER_AVATAR, isChecked));
                } else if (viewType == 2) {
                    TextView value = root.findViewById(R.id.mineItemValue);
                    value.setText(Device.App.getVersionName(getContext()));
                }
            }
        });
        this.recyclerView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int viewType, MineItem entity, int position) {
        if (!this.isLogin()) {
            this.showMessage(true, R.string.error_message_logout);
            return;
        }
        switch (entity) {
            case Address:
                break;
            case Feedback:
                PgyFeedback.getInstance().showDialog(this.getAppCompatActivity());
                this.checkPermission();
                break;
            case Integral:
                IntegralElementActivity.start(getContext());
                break;
            case Coupons:
                CouponsConfigActivity.start(getContext());
                break;
            case CouponsNearOverdue:
                CouponsNearOverdueActivity.start(getContext());
                break;
            case ShowMemberAvatar:
                //独立处理
                break;
            case Version:
                //不做操作
                break;
        }
    }

    private void checkPermission() {
        Activity activity = getAppCompatActivity();
        if (activity instanceof OnRequestPermission) {
            OnRequestPermission permission = (OnRequestPermission) activity;
            permission.requestPermission(Manifest.permission.RECORD_AUDIO, getString(R.string.permission_record_audio));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mineLogin:
                this.auth();
                break;
        }
    }

    private void auth() {
        long cTime = System.currentTimeMillis() - lastAuth;
        long AUTH_INTERVAL = 2000;
        if (cTime < AUTH_INTERVAL) {
            cTime = (cTime + 999) / 1000;
            this.showMessage(true, getString(R.string.auth_waiting, cTime));
            return;
        }
        this.lastAuth = System.currentTimeMillis();
        this.authLogin = AuthWeiXin.getInstance();
        try {
            this.authLogin.login(getAppCompatActivity(), "", this);
        } catch (CodeException e) {
            Logger.d("auth", e);
            switch (e.code) {
                case ErrorMessage.ERROR_CODE_APPKEY_EMPTY:
                    break;
                case ErrorMessage.ERROR_CODE_NOT_INSTALL:
                    this.showMessage(true, R.string.wechat_uninstall);
                    break;
            }
        }
    }

    @Override
    public void onAuthSuccess(String openId, String accessToken) {
        Logger.d("onAuthSuccess.UserId:" + openId + ";token:" + accessToken);
        this.login(openId, accessToken);
    }

    @Override
    public void onAuthException(CodeException e) {
        switch (e.code) {
            case ErrorMessage.ERROR_CODE_USER_CANCEL:
                this.showMessage(true, R.string.user_auth_canceled);
                break;
            case ErrorMessage.ERROR_CODE_AUTH_DENY:
                this.showMessage(true, R.string.user_auth_deny);
                break;
            case ErrorMessage.ERROR_CODE_AUTH_FAILED:
            case ErrorMessage.ERROR_CODE_SENT_FAILED:
                this.showMessage(true, R.string.user_auth_failed);
                break;
        }
    }

    private void login(String openId, String accessToken) {
        Logger.d("login.UserId:" + openId + ";token:" + accessToken);
        this.dismissDialog(this.loginingDialog);
        this.loginingDialog = this.createProgressDialog(R.string.user_logining);
        try {
            IUser.getInstance().login(accessToken, this);
        } catch (CodeException e) {
            this.dismissDialog(this.loginingDialog);
            switch (e.code) {
                case ErrorMessage.ERROR_CODE_TOKEN_EMPTY:
                    this.showMessage(true, R.string.error_password_empty);
                    break;
                default:
                    this.showMessage(true, R.string.error_unknown);
                    break;
            }
        }
    }

    @Override
    public void onSuccess(IUserEntity userInfo) {
        Logger.d("login success user:" + userInfo.getNickName());
        this.dismissDialog(this.loginingDialog);
        this.refreshLogin();
    }

    @Override
    public void onException(CodeException e) {
        Logger.d("onException", e);
        this.dismissDialog(this.loginingDialog);
        switch (e.code) {
            case ErrorMessage.ERROR_CODE_NETWORK:
                this.showMessage(true, R.string.error_network);
                break;
            case ErrorMessage.ERROR_CODE_ACCOUNT_LOCKOUT:
                this.showMessage(true, R.string.error_account_lockout);
                break;
            case ErrorMessage.ERROR_CODE_LOGIN:
            default:
                showMessage(true, R.string.error_unknown);
                break;
        }
    }

    @Override
    public boolean isDestroy() {
        return this.isDetached();
    }

    public enum MineItem {
        Integral("integral", "积分项", R.string.mine_integral, 0),
        Coupons("coupons", "优惠券配置", R.string.mine_coupons, 0),
        CouponsNearOverdue("couponsNearOverdue", "临期优惠券", R.string.mine_coupons_near_overdue, 0),
        Address("address", "导出地址", R.string.mine_address, 0),
        Feedback("Feedback", "问题反馈", R.string.mine_feedback, 0),
        ShowMemberAvatar("showMemberAvatar", "显示成员头像", R.string.mine_show_member_avatar, 1),
        Version("version", "版本", R.string.mine_app_version, 2),
        ;
        public String code;
        public String name;
        public int res;
        public int viewType;

        MineItem(String code, String name, int res, int viewType) {
            this.code = code;
            this.name = name;
            this.res = res;
            this.viewType = viewType;
        }
    }

    private void dismissDialog(Dialog dialog) {
        if (dialog == null) {
            return;
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private Dialog createProgressDialog(@StringRes int string) {
        return WindowUtil.createProgressDialog(getContext(), 0, string, false);
    }

    private boolean isLogin() {
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.authLogin != null) {
            this.authLogin.unRegister(this);
        }
    }
}
