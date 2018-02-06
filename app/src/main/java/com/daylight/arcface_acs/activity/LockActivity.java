package com.daylight.arcface_acs.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.arch.QMUIFragmentActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.List;

public class LockActivity extends QMUIFragmentActivity {

    private PatternLockView lockView;
    private TextView hint;
    private TextView hintWrong;
    private String password;
    private int count;
    private User user;
    private UserViewModel viewModel;
    @Override
    protected int getContextViewId() {
        return R.id.lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);
        viewModel=ViewModelProviders.of(this).get(UserViewModel.class);
        user= viewModel.loadUser(SharedPreferencesUtil.getAccount(this));
        initLockView();
        initHint();
        initData();
        initForgetBtn();
    }

    private void initLockView() {
        lockView = findViewById(R.id.pattern_lock_view);
        lockView.addPatternLockListener(mPatternLockViewListener);
        lockView.setTactileFeedbackEnabled(false);
    }

    private void initHint() {
        hint = findViewById(R.id.hint_pattern);
        hint.setText("请输入密码");
        hintWrong=findViewById(R.id.hint_pattern_wrong);
    }

    private void initForgetBtn(){
        TextView forgetPwd=findViewById(R.id.forgetPwd);
        forgetPwd.setOnClickListener(view->{
            SharedPreferencesUtil.setPassword(this,"");
            user.setPattern(null);
            user.setHasPatternLock(false);
            viewModel.update(user);
            startActivity(new Intent(LockActivity.this,LoginActivity.class));
            finish();
        });
    }

    private void initData() {
        count = SharedPreferencesUtil.getCount(this);
        password = user.getPattern();
    }

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {

        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
                if (password.equals(PatternLockUtils.patternToMD5(lockView, pattern))) {
                    lockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    SharedPreferencesUtil.resetCount(LockActivity.this);
                    Intent intent = new Intent(LockActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    SharedPreferencesUtil.SubCount(LockActivity.this);
                    count--;
                    hint.setText(String.valueOf("密码错误，还可以再试"+count+"次"));
                    if (count > 0) {
                        hint.setTextColor(getResources().getColor(R.color.grapefruit));
                        hint.startAnimation(AnimationUtils.loadAnimation(LockActivity.this,R.anim.shake));
                        hintWrong.setVisibility(View.VISIBLE);
                        lockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    } else {
                        user.setPattern(null);
                        user.setHasPatternLock(false);
                        viewModel.update(user);
                        SharedPreferencesUtil.setPassword(LockActivity.this,"");
                        QMUIDialog mDialog=new QMUIDialog.MessageDialogBuilder(LockActivity.this)
                                .setTitle("解锁失败")
                                .setMessage("连续5次解锁失败，请重新登录。")
                                .addAction("确定", (dialog, index) -> {
                                    startActivity(new Intent(LockActivity.this, LoginActivity.class));
                                    finish();
                                }).create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.show();
                    }
                }
            new Handler().postDelayed(() -> {
                hint.clearAnimation();
                lockView.clearPattern();
            }, 1000);
        }

        @Override
        public void onCleared() {

        }
    };

}
