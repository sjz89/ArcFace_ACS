package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.dialog.PinLockDialog;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

/**
 * 解锁界面
 * Created by Daylight on 2018/1/20.
 */

public class UnlockFragment extends BaseFragment {
    private View view;
    private User user;
    private UserViewModel viewModel;
    private QMUITopBarLayout topBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        user= viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView() {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_unlock, null);
        initTopBar();
        initButton();
        viewModel.getUser().observe(this,user1 -> {
            if (user1!=null)
                user=user1;
            if (user.getStatus()== Values.EXAMINE){
                topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.grapefruit));
                topBar.setSubTitle(R.string.account_examine);
            }else{
                topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.app_color_blue_2));
                topBar.setSubTitle(null);
            }
        });
        return view;
    }

    private void initTopBar() {
        topBar = view.findViewById(R.id.topbar_unlock);
        topBar.setTitle(R.string.title_unlock);
    }

    private void initButton() {
        Button btn_unlock = view.findViewById(R.id.btn_unlock);
        btn_unlock.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_unlock:
                if (user.getPin()!=null) {
                    PinLockDialog passwordInput = new PinLockDialog(getContext(),user);
                    passwordInput.verifyPin();
                } else {
                    new QMUIDialog.MessageDialogBuilder(getContext())
                            .setMessage("您还没设置开门密码，请先前往设置")
                            .addAction("取消", (dialog, index) -> dialog.dismiss())
                            .addAction("前往设置", (dialog, index) -> {
                                dialog.dismiss();
                                RxBusHelper.post(Values.GO_TO_SET_PIN);
                            })
                            .show();
                }
                break;
        }
    };

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
