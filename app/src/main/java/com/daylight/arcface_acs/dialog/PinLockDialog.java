package com.daylight.arcface_acs.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Pin密码界面
 * Created by Daylight on 2018/1/20.
 */

public class PinLockDialog extends QMUIBottomSheet implements View.OnClickListener{
    private View view;
    private PinLockView pinLockView;
    private IndicatorDots indicatorDots;
    private TextView hint;
    private TextView next;
    private String password;
    private int count;
    private int mPinLength;
    private String mIntermediatePin;
    private User user;
    @SuppressLint("InflateParams")
    public PinLockDialog(Context context,User user) {
        super(context);
        this.user=user;
        view=LayoutInflater.from(context).inflate(R.layout.dialog_pin,null);
        initView();
        setContentView(view);
    }
    private void initView(){
        pinLockView=view.findViewById(R.id.pin_lock_view);
        indicatorDots = view.findViewById(R.id.indicator_dots);
        hint = view.findViewById(R.id.hint_pin);
        TextView cancel=view.findViewById(R.id.pin_lock_cancel);
        cancel.setOnClickListener(this);
        next=view.findViewById(R.id.pin_lock_next);
        next.setOnClickListener(this);
        pinLockView.attachIndicatorDots(indicatorDots);
    }

    public void setPin(){
        count=0;
        pinLockView.setPinLockListener(setPinLockListener);
        pinLockView.setPinLength(6);
        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL);
        hint.setText("请输入4-6位新密码");
        next.setVisibility(VISIBLE);
        show();
    }

    public void verifyPin(){
        pinLockView.setPinLockListener(verifyPinLockListener);
        pinLockView.setPinLength(user.getPin().length());
        indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
        hint.setText("请输入密码");
        show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pin_lock_cancel:
                dismiss();
                break;
            case R.id.pin_lock_next:
                pinLockView.resetPinLockView();
                pinLockView.setPinLength(mPinLength);
                password = mIntermediatePin;
                hint.setText("再次输入密码");
                hint.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));
                indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
                count++;
                next.setVisibility(GONE);
                break;
        }
    }
    private PinLockListener setPinLockListener=new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            switch (count){
                case 0:
                    hint.setText("再次输入密码");
                    hint.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade));
                    pinLockView.resetPinLockView();
                    indicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
                    password=pin;
                    count++;
                    break;
                case 1:
                    if (password.equals(pin)) {
                        hint.setText("密码设置成功");
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade));
                        user.setPin(pin);
                        RxBusHelper.post(user);
                        new Handler().postDelayed(() -> dismiss(),1500);
                    }else{
                        hint.setText("密码不匹配，请重试");
                        hint.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                        new Handler().postDelayed(() -> {
                            pinLockView.resetPinLockView();
                            hint.clearAnimation();
                        },500);
                    }
                    break;
            }
        }

        @Override
        public void onEmpty() {
            next.setTextColor(getContext().getResources().getColor(R.color.grey));
            next.setClickable(false);
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            mPinLength =pinLength;
            mIntermediatePin=intermediatePin;
            if (pinLength>=4){
                next.setTextColor(getContext().getResources().getColor(R.color.blueJeans));
                next.setClickable(true);
            }else{
                next.setTextColor(getContext().getResources().getColor(R.color.grey));
                next.setClickable(false);
            }
        }
    };
    private PinLockListener verifyPinLockListener=new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (pin.equals(user.getPin())){
                hint.setText("解锁成功");
                hint.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fade));
                new Handler().postDelayed(() -> dismiss(),1500);
            }else{
                hint.setText("密码错误，请重试");
                hint.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.shake));
                new Handler().postDelayed(() -> {
                    pinLockView.resetPinLockView();
                    hint.clearAnimation();
                },500);
            }
        }

        @Override
        public void onEmpty() {

        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };
}
