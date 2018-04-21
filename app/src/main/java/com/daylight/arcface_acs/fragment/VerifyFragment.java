package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

public class VerifyFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    private UserViewModel userViewModel;
    private FaceViewModel faceViewModel;
    private List<User> users;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        faceViewModel=ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        userViewModel.getUsers(userViewModel.loadUser(SharedPreferencesUtil.getAccount(getBaseFragmentActivity())).getCommunityName())
                .observe(this,users1 -> {
                    if (users1!=null&&users1.size()!=0){
                        users=users1;
                        initData();
                    }
                });
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_verify,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_verify);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.verify);
    }

    private void initPullRefreshLayout(){
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_verify);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(()->swipeRefreshLayout.setRefreshing(false),2000));
    }

    private void initData(){
        List<CommonData> data=new ArrayList<>();
        for (User user:users){
            CommonData commonData=new CommonData(user.getName(),user.getBuildingName()+user.getDoorNum());
            commonData.setImage(faceViewModel.loadFace(user.getPhoneNum()).getFaceData());
            commonData.setTime(user.getStatus()==100?"已审核":user.getStatus()==-100?"未审核":"已冻结");
            data.add(commonData);
        }
        adapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_verify);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL,QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        adapter.setOnCommonItemClickListener((v,position)->{
            if (users.get(position).getStatus()==Values.EXAMINE){
                new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                        .setMessage("是否通过该业主的身份审核？")
                        .setTitle("业主审核")
                        .addAction("拒绝",((dialog, index) -> {
                            users.get(position).setStatus(Values.FROZEN);
                            userViewModel.update(users.get(position));
                            dialog.dismiss();
                        }))
                        .addAction("通过",((dialog, index) -> {
                            users.get(position).setStatus(Values.AVAILABLE);
                            userViewModel.update(users.get(position));
                            dialog.dismiss();
                        })).show();
            }else if (users.get(position).getStatus()==Values.AVAILABLE){
                new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                        .setMessage("是否冻结该账号？")
                        .setTitle("业主审核")
                        .addAction("取消",((dialog, index) -> dialog.dismiss()))
                        .addAction("冻结",((dialog, index) -> {
                            users.get(position).setStatus(Values.FROZEN);
                            userViewModel.update(users.get(position));
                            dialog.dismiss();
                        })).show();
            }else if (users.get(position).getStatus()==Values.FROZEN){
                new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                        .setMessage("是否解冻该账号？")
                        .setTitle("业主审核")
                        .addAction("取消",((dialog, index) -> dialog.dismiss()))
                        .addAction("确定",((dialog, index) -> {
                            users.get(position).setStatus(Values.AVAILABLE);
                            userViewModel.update(users.get(position));
                            dialog.dismiss();
                        })).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
