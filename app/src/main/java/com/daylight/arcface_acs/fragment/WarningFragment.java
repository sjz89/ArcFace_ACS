package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

public class WarningFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_warning,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        initData();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_warning);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.warning);
    }

    private void initPullRefreshLayout(){
        SwipeRefreshLayout swipeRefreshLayout =view.findViewById(R.id.refresh_warning);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(()->swipeRefreshLayout.setRefreshing(false),2000));
    }

    private void initData(){
        List<CommonData> data=new ArrayList<>();

        CommonData commonData0=new CommonData("人员预警","独居老人出入异常");
        commonData0.setIcon(R.drawable.ic_warning_red);
        commonData0.setTime("2018年04月10日 09:30");
        data.add(commonData0);

        CommonData commonData1=new CommonData("安全预警","检测到黑名单人员");
        commonData1.setIcon(R.drawable.ic_warning_yellow);
        commonData1.setTime("2018年04月09日 13:28");
        data.add(commonData1);

        CommonData commonData2=new CommonData("安全预警","临时人员滞留超时");
        commonData2.setIcon(R.drawable.ic_warning_green);
        commonData2.setTime("2018年04月06日 16:15");
        data.add(commonData2);

        CommonData commonData3=new CommonData("设备预警","门禁设备状态异常");
        commonData3.setIcon(R.drawable.ic_warning_red);
        commonData3.setTime("2018年04月04日 10:42");
        data.add(commonData3);

        adapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_warning);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL,QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        adapter.setOnCommonItemClickListener((v,position)->{

        });
        recyclerView.setAdapter(adapter);
    }
}
