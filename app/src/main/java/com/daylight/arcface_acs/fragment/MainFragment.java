package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.dialog.PinLockDialog;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.HashMap;

public class MainFragment extends BaseFragment {

    private View root;
    private QMUITabSegment tabSegment;

    private CharSequence[] titles;
    private HashMap<Pager,BaseFragment> mPages;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel viewModel=ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        user= viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        if (user.getStatus()==Values.EXAMINE){
            QMUIDialog mDialog=new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                    .setTitle("提示")
                    .setMessage("您的账号还未被物业审核通过，请先完善信息或者耐心等待。如有问题，请联系物业。")
                    .addAction(new QMUIDialogAction(getContext(), "退出", (dialog, index) -> {
                        dialog.dismiss();
                        popBackStack();
                    }))
                    .addAction(new QMUIDialogAction(getContext(),"完善信息",((dialog, index) -> {
                        startFragment(new InfoFragment());
                        dialog.dismiss();
                    })))
                    .create();
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();
        }
        viewModel.getUser().observe(getBaseFragmentActivity(),user1 -> {
            if (user1!=null) {
                user = user1;
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main,null);
        initTitles();
        initTabs();
        initPagers();
        return root;
    }

    private void initTitles(){
        titles=new String[3];
        titles[0]=getResources().getString(R.string.title_unlock);
        titles[1]=getResources().getString(R.string.title_faces);
        titles[2]=getResources().getString(R.string.title_setting);
    }
    @SuppressWarnings("ConstantConditions")
    private void initTabs() {
        tabSegment=root.findViewById(R.id.tabs);
        int normalColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_gray_6);
        int selectColor = QMUIResHelper.getAttrColor(getActivity(), R.attr.qmui_config_color_blue);
        tabSegment.setDefaultNormalColor(normalColor);
        tabSegment.setDefaultSelectedColor(selectColor);

        QMUITabSegment.Tab unlock = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_component),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_component_selected),
                titles[0], false
        );

        QMUITabSegment.Tab faces = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_util),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_util_selected),
                titles[1], false
        );
        QMUITabSegment.Tab me = new QMUITabSegment.Tab(
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_lab),
                ContextCompat.getDrawable(getContext(), R.mipmap.icon_tabbar_lab_selected),
                titles[2], false
        );
        tabSegment.addTab(unlock)
                .addTab(faces)
                .addTab(me);
    }

    private void initPagers() {
        ViewPager mViewPager=root.findViewById(R.id.pager);

        mPages=new HashMap<>();
        BaseFragment unlockFragment=new UnlockFragment();
        mPages.put(Pager.UNLOCK,unlockFragment);
        BaseFragment facesFragment=new FacesFragment();
        mPages.put(Pager.FACES,facesFragment);
        BaseFragment settingFragment=new SettingFragment();
        mPages.put(Pager.SETTING,settingFragment);

        FragmentPagerAdapter mPageAdapter=new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mPages.get(Pager.getPagerFromPosition(position));
            }

            @Override
            public int getCount() {
                return mPages.size();
            }
        };

        mViewPager.setAdapter(mPageAdapter);
        tabSegment.setupWithViewPager(mViewPager,false);

        RxBusHelper.doOnMainThread(Integer.class, integer -> {
            if (integer==Values.GO_TO_SET_PIN){
                mViewPager.setCurrentItem(2,false);
                new PinLockDialog(getContext(),user).setPin();
            }
        });
    }

    enum Pager {
        UNLOCK, FACES, SETTING;

        public static Pager getPagerFromPosition(int position) {
            switch (position) {
                case 0:
                    return UNLOCK;
                case 1:
                    return FACES;
                case 2:
                    return SETTING;
                default:
                    return UNLOCK;
            }
        }
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
