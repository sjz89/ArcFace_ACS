package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
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

/**
 * 快递
 * Created by Daylight on 2018/3/18.
 */

public class ExpressFragment extends BaseFragment {
    private View view;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_express,null);
        initTopBar();
        initRecyclerView();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_express);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.express);
    }

    private List<CommonData> initData(){
        List<CommonData> data=new ArrayList<>();
        CommonData commonData1=new CommonData("菜鸟驿站1号柜","取件码:ML2548");
        commonData1.setIcon(R.drawable.ic_package);
        commonData1.setTime("待取\n2018年3月19日 17:10");
        CommonData commonData2=new CommonData("菜鸟驿站1号柜","取件码:ML3275");
        commonData2.setIcon(R.drawable.ic_package);
        commonData2.setTime("已取\n2018年3月15日 16:47");

        data.add(commonData1);
        data.add(commonData2);
        return data;
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_express);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        CommonAdapter adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL,QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        recyclerView.setAdapter(adapter);
        adapter.setData(initData());
    }
}
