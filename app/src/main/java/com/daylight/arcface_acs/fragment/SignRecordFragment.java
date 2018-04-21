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
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

public class SignRecordFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    private User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        user=userViewModel.loadUser(SharedPreferencesUtil.getAccount(getBaseFragmentActivity()));
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_signrecord,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        initData();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_sign_record);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.sign_record);
    }

    private void initPullRefreshLayout(){
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_sign_record);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(()->swipeRefreshLayout.setRefreshing(false),2000));
    }

    private void initData(){
        List<CommonData> data=new ArrayList<>();
        CommonData commonData1=new CommonData(user.getName(),"签到");
        commonData1.setTime("2018年04月03日 07:30");
        commonData1.setIcon(R.drawable.ic_sign_in_blue);
        data.add(commonData1);

        CommonData commonData2=new CommonData(user.getName(),"签退");
        commonData2.setTime("2018年04月03日 18:30");
        commonData2.setIcon(R.drawable.ic_sign_out_blue);
        data.add(commonData2);

        adapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_sign_record);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL,QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        recyclerView.setAdapter(adapter);
    }
}
