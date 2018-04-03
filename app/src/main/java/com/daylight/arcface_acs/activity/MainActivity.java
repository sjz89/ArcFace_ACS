package com.daylight.arcface_acs.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.daylight.arcface_acs.app.MessageService;
import com.daylight.arcface_acs.fragment.OwnerFragment;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.fragment.BaseFragment;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends QMUIFragmentActivity implements ServiceConnection{
    private MessageService.MsgBinder msgBinder;
    @Override
    protected int getContextViewId() {
        return R.id.main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SQLiteStudioService.instance().start(this);
        Intent bindIntent = new Intent(this, MessageService.class);
        bindService(bindIntent, this, BIND_AUTO_CREATE);
        UserViewModel viewModel= ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.setUser(SharedPreferencesUtil.getAccount(this));
        if (savedInstanceState==null){
            BaseFragment fragment=new OwnerFragment();
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
        RxBusHelper.release();
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        msgBinder=(MessageService.MsgBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public MessageService.MsgBinder getMsgBinder(){
        return msgBinder;
    }
}
