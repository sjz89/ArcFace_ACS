package com.daylight.arcface_acs.activity;

import android.os.Bundle;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.fragment.BaseFragment;
import com.daylight.arcface_acs.fragment.LoginFragment;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

public class LoginActivity extends QMUIFragmentActivity{
    @Override
    protected int getContextViewId() {
        return R.id.login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState==null){
            BaseFragment fragment=new LoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(getContextViewId(),fragment,fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
