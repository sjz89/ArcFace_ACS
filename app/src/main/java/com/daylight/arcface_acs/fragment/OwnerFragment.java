package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.adapter.ImagePagerAdapter;
import com.daylight.arcface_acs.adapter.MainItemAdapter;
import com.daylight.arcface_acs.bean.MainItemData;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.dialog.PinLockDialog;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.github.demono.AutoScrollViewPager;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * app首页
 * Created by Daylight on 2018/3/13.
 */

public class OwnerFragment extends BaseFragment {
    private View view;
    private AutoScrollViewPager viewPager;
    private User user;
    private UserViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        user= viewModel.loadUser(SharedPreferencesUtil.getAccount(getBaseFragmentActivity()));
        viewModel.getUser().observe(getBaseFragmentActivity(),user1 -> {
            if (user1!=null) {
                user = user1;
                SharedPreferencesUtil.setPin(getBaseFragmentActivity(),user.getPin());
                if (user.getStatus()== Values.UNREGISTER){
                    QMUIDialog mDialog=new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                            .setTitle("提示")
                            .setMessage("您的账号还未被完善信息，请先去完善信息")
                            .addAction("退出", (dialog, index) -> {
                                dialog.dismiss();
                                popBackStack();
                            })
                            .addAction("完善信息",((dialog, index) -> {
                                startFragment(new InfoFragment());
                                dialog.dismiss();
                            }))
                            .create();
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
            }
        });
    }
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_owner,null);
        initTopBar();
        initViewPage();
        initRecyclerView();
        RxBusHelper.doOnChildThread(User.class, user1 -> viewModel.update(user1));
        RxBusHelper.doOnMainThread(String.class,msg -> new QMUIDialog.MessageDialogBuilder(getContext())
                .setTitle("AEye小助手")
                .setMessage("\u3000\u3000"+msg)
                .addAction("不了",((dialog, index) -> dialog.dismiss()))
                .addAction("好的",((dialog, index) -> dialog.dismiss()))
                .show());
        return view;
    }

    private void initTopBar(){
        QMUICollapsingTopBarLayout mCollapsingTopBarLayout = view.findViewById(R.id.collapsing_topbar_layout);
        mCollapsingTopBarLayout.setTitle("AEye");
        mCollapsingTopBarLayout.setExpandedTitleColor(getBaseFragmentActivity().getResources().getColor(R.color.transparent));
        mCollapsingTopBarLayout.setScrimUpdateListener(animation -> {
            if (animation.getAnimatedValue().equals(0))
                viewPager.startAutoScroll();
            else
                viewPager.stopAutoScroll();
        });
    }

    private void initViewPage(){
        viewPager=view.findViewById(R.id.viewPager);
        ImagePagerAdapter adapter=new ImagePagerAdapter(getContext());
        List<String> data=new ArrayList<>();
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1521465414314&di=30bff1fb7c930b3ccfe059daab1774b7&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130220%2F4499633_175555660355_2.jpg");
        data.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1521465486388&di=feb117aaa6b75df40a16c9a611256f45&imgtype=0&src=http%3A%2F%2Fimgs.focus.cn%2Fupload%2Fbj%2F6830%2Fb_68290470.jpg");
        data.add("http://imgsrc.baidu.com/imgad/pic/item/4b90f603738da97734fa5c06bb51f8198718e3c2.jpg");
        adapter.setData(data);
        adapter.setOnPageClickListener((v,position)->startFragment(new NewsFragment()));
        viewPager.setAdapter(adapter);
        viewPager.startAutoScroll();
    }

    private List<MainItemData> initData(){
        List<MainItemData> data=new ArrayList<>();
        data.add(new MainItemData("公告",R.drawable.ic_notification));
        data.add(new MainItemData("成员管理",R.drawable.ic_profile));
        data.add(new MainItemData("门禁申请",R.drawable.ic_pin));
        data.add(new MainItemData("邻居",R.drawable.ic_chat));
        data.add(new MainItemData("一键开门",R.drawable.ic_lock_unlock));
        data.add(new MainItemData("通行记录",R.drawable.ic_time_oclock));
        data.add(new MainItemData("快递",R.drawable.ic_shopping));
        data.add(new MainItemData("物业服务",R.drawable.ic_home));
        data.add(new MainItemData("设置",R.drawable.ic_setting_cog));
        return data;
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        MainItemAdapter adapter=new MainItemAdapter(getContext(),initData());
        adapter.setOnItemClickListener((view, position) -> {
            switch ((String)view.getTag()) {
                case "一键开门":
                    if (user.getPin()!=null) {
                        new QMUIDialog.MenuDialogBuilder(getContext())
                                .setTitle("请选择要开启的门禁")
                                .addItems(new String[]{"15栋","北门(进口)","北门(出口)","南门(进口)","南门(出口)"},
                                        (dialog,position1)->{
                                            dialog.dismiss();
                                            PinLockDialog passwordInput = new PinLockDialog(getContext(),user);
                                            passwordInput.verifyPin();
                                        }).show();
                    } else {
                        new QMUIDialog.MessageDialogBuilder(getContext())
                                .setMessage("您还没设置开门密码，请先前往设置")
                                .addAction("取消", (dialog, index) -> dialog.dismiss())
                                .addAction("前往设置", (dialog, index) -> {
                                    dialog.dismiss();
                                    new PinLockDialog(getContext(),user).setPin();
                                })
                                .show();
                    }
                    break;
                case "成员管理":
                    startFragment(new FacesFragment());
                    break;
                case "通行记录":
                    startFragment(new PassRecordFragment());
                    break;
                case "物业服务":
                    startFragment(new PropertyFragment());
                    break;
                case "快递":
                    startFragment(new ExpressFragment());
                    break;
                case "邻居":
                    startFragment(new NeighborFragment());
                    break;
                case "门禁申请":
                    startFragment(new RequestFragment());
                    break;
                case "公告":
                    startFragment(new BoardFragment());
                    break;
                case "设置":
                    startFragment(new SettingFragment());
                    break;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }

    @Override
    public void onDestroy() {
        viewPager.stopAutoScroll();
        super.onDestroy();
    }
}
