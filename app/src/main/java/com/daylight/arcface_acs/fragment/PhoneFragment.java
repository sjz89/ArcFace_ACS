package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.viewmodel.PhoneViewModel;

public class PhoneFragment extends BaseFragment {
    private View view;
    private TextView state;
    private Chronometer time;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_phone,null);
        initView();
        return view;
    }
    private void initView(){
        PhoneViewModel viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(PhoneViewModel.class);
        CommonData data=viewModel.getData();

        ImageView head=view.findViewById(R.id.phone_head);
        GlideApp.with(this).load(data.getIcon()==R.drawable.ic_profile_male?
                R.drawable.ic_profile_male_head:R.drawable.ic_profile_female_head).into(head);

        TextView name=view.findViewById(R.id.phone_name);
        name.setText(data.getText());
        countDownTimer.start();

        state=view.findViewById(R.id.phone_state);
        state.setText("等待对方接通");

        time=view.findViewById(R.id.phone_time);

        Button end=view.findViewById(R.id.phone_end);
        end.setText("挂断");
        end.setOnClickListener(v -> {
            time.stop();
            countDownTimer.cancel();
            state.setText("已结束");
            new Handler().postDelayed(this::popBackStack,2000);
        });
    }

    private CountDownTimer countDownTimer=new CountDownTimer(4*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) { }

        @Override
        public void onFinish() {
            state.setText("正在通话");
            time.setVisibility(View.VISIBLE);
            time.setBase(SystemClock.elapsedRealtime());
            time.start();
        }
    };

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
