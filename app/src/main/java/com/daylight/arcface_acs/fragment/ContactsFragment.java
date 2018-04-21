package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.viewmodel.PhoneViewModel;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    private List<CommonData> data;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_contacts,null);
        initTopBar();
        initRecyclerView();
        initData();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_contacts);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.contacts);
    }
    private void initData(){
        data=new ArrayList<>();

        CommonData commonData1=new CommonData("小周","在线");
        commonData1.setIcon(R.drawable.ic_profile_male);
        data.add(commonData1);

        CommonData commonData2=new CommonData("小吴","在线");
        commonData2.setIcon(R.drawable.ic_profile_male);
        data.add(commonData2);

        CommonData commonData3=new CommonData("大朱","在线");
        commonData3.setIcon(R.drawable.ic_profile_male);
        data.add(commonData3);

        CommonData commonData4=new CommonData("李华","在线");
        commonData4.setIcon(R.drawable.ic_profile_female);
        data.add(commonData4);

        CommonData commonData5=new CommonData("张三","离线");
        commonData5.setIcon(R.drawable.ic_profile_male);
        data.add(commonData5);

        adapter.setData(data);
    }
    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_contacts);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        adapter.setOnCommonItemClickListener((v,position)->{
            PhoneViewModel viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(PhoneViewModel.class);
            viewModel.setData(data.get(position));
            startFragment(new PhoneFragment());
        });
        recyclerView.setAdapter(adapter);
    }
}
