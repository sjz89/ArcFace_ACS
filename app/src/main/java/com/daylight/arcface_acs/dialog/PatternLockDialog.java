package com.daylight.arcface_acs.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import java.util.List;

/**
 * 九宫格密码锁界面
 * Created by Daylight on 2018/1/20.
 */

public class PatternLockDialog extends QMUIBottomSheet {
    private View view;
    private PatternLockView patternLockView;
    private TextView hint;
    private String password;
    private int count;
    private User user;
    @SuppressLint("InflateParams")
    public PatternLockDialog(Context context,User user) {
        super(context);
        this.user=user;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_pattern, null);
        initView();
        setContentView(view);
    }

    private void initView() {
        patternLockView = view.findViewById(R.id.pattern_lock_view);
        hint = view.findViewById(R.id.hint_pattern_dialog);
    }

    public void setPattern() {
        count = 0;
        patternLockView.addPatternLockListener(setPatternLockListener);
        patternLockView.setTactileFeedbackEnabled(false);
        hint.setText("绘制解锁图案");
        show();
    }

    private PatternLockViewListener setPatternLockListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {

        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {

        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            switch (count) {
                case 0:
                    if (pattern.size() < 4) {
                        hint.setText("密码长度不能小于4位");
                        hint.setTextColor(getContext().getResources().getColor(R.color.grapefruit));
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                    } else {
                        password = PatternLockUtils.patternToMD5(patternLockView, pattern);
                        hint.setText("确认解锁图案");
                        hint.setTextColor(getContext().getResources().getColor(R.color.black));
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
                        count++;
                    }
                    break;
                case 1:
                    if (password.equals(PatternLockUtils.patternToMD5(patternLockView, pattern))) {
                        hint.setText("密码设置成功");
                        hint.setTextColor(getContext().getResources().getColor(R.color.black));
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
                        user.setPattern(password);
                        user.setHasPatternLock(true);
                        SharedPreferencesUtil.resetCount(getContext());
                        RxBusHelper.post(user);
                        new Handler().postDelayed(() -> dismiss(), 1000);
                    } else {
                        hint.setText("与上次绘制图案不一致，请重试");
                        hint.setTextColor(getContext().getResources().getColor(R.color.grapefruit));
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                    }
                    break;
            }
            new Handler().postDelayed(() -> patternLockView.clearPattern(), 800);
        }

        @Override
        public void onCleared() {

        }
    };
}
