package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.ImagePagerAdapter;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.dialog.PinLockDialog;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.github.demono.AutoScrollViewPager;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 解锁界面
 * Created by Daylight on 2018/1/20.
 */

public class UnlockFragment extends BaseFragment {
    private View view;
    private User user;
    private UserViewModel viewModel;
    private AutoScrollViewPager viewPager;
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
        initViewPage();
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

    private void initViewPage(){
        viewPager=view.findViewById(R.id.viewPager);
        ImagePagerAdapter adapter=new ImagePagerAdapter(getContext());
        List<String> data=new ArrayList<>();
        data.add("http://img2.imgtn.bdimg.com/it/u=1655431608,2212416494&fm=27&gp=0.jpg");
        data.add("http://d.hiphotos.baidu.com/zhidao/pic/item/279759ee3d6d55fb2166d92165224f4a20a4dd11.jpg");
        data.add("http://f2.dn.anqu.com/down/NjY4Mg==/allimg/1208/48-120P31A014.jpg");
        data.add("http://imgsrc.baidu.com/forum/w%3D580/sign=7ca341b5d8f9d72a17641015e428282a/efc8840e7bec54e7503e3f94b8389b504dc26a65.jpg");
        adapter.setData(data);
        viewPager.setAdapter(adapter);
        viewPager.startAutoScroll();
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

    @Override
    public void onStop() {
        viewPager.stopAutoScroll();
        super.onStop();
    }
}
