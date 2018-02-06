package com.daylight.arcface_acs.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.fragment.BaseFragment;
import com.daylight.arcface_acs.fragment.MainFragment;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends QMUIFragmentActivity {

    @Override
    protected int getContextViewId() {
        return R.id.main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel viewModel= ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.setUser(SharedPreferencesUtil.getAccount(this));
        if (savedInstanceState==null){
            BaseFragment fragment=new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(),fragment,fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }
    @Override
    protected void onDestroy() {
        SQLiteStudioService.instance().stop();
        super.onDestroy();
        RxBusHelper.release();
    }
}
