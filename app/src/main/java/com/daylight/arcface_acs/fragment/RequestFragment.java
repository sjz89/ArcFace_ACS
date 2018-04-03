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
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * 门禁申请
 * Created by Daylight on 2018/3/19.
 */

public class RequestFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    private List<CommonData> data;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_request,null);
        initTopBar();
        initRecyclerView();
        initData();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_request);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.request);
    }
    private void initData(){
        data=new ArrayList<>();
        CommonData commonData7=new CommonData("15栋","已申请");
        commonData7.setIcon(R.drawable.ic_lock_unlock_blue);
        data.add(commonData7);
        CommonData commonData1=new CommonData("北门(进口)","已申请");
        commonData1.setIcon(R.drawable.ic_lock_unlock_blue);
        data.add(commonData1);
        CommonData commonData2=new CommonData("北门(出口)","已申请");
        commonData2.setIcon(R.drawable.ic_lock_unlock_blue);
        data.add(commonData2);
        CommonData commonData3=new CommonData("南门(进口)","已申请");
        commonData3.setIcon(R.drawable.ic_lock_unlock_blue);
        data.add(commonData3);
        CommonData commonData4=new CommonData("南门(出口)","已申请");
        commonData4.setIcon(R.drawable.ic_lock_unlock_blue);
        data.add(commonData4);
        CommonData commonData5=new CommonData("东门(进口)","未申请");
        commonData5.setIcon(R.drawable.ic_lock_blue);
        data.add(commonData5);
        CommonData commonData6=new CommonData("东门(出口)","未申请");
        commonData6.setIcon(R.drawable.ic_lock_blue);
        data.add(commonData6);
        CommonData commonData8=new CommonData("西门(进口)","未申请");
        commonData8.setIcon(R.drawable.ic_lock_blue);
        data.add(commonData8);
        CommonData commonData9=new CommonData("西门(出口)","未申请");
        commonData9.setIcon(R.drawable.ic_lock_blue);
        data.add(commonData9);
        adapter.setData(data);
    }
    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_request);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.HORIZONTAL);
        adapter.setOnCommonItemClickListener((v,position)->{
            if (data.get(position).getSubText().equals("未申请")) {
                new QMUIDialog.MessageDialogBuilder(getBaseFragmentActivity())
                        .setTitle("发起申请")
                        .setMessage("是否申请 " + data.get(position).getText() + " 的门禁权限？")
                        .addAction(new QMUIDialogAction(getContext(), "取消", (dialog, index) -> dialog.dismiss()))
                        .addAction(new QMUIDialogAction(getContext(), "确定", (dialog, index) -> {
                            data.get(position).setSubText("审核中");
                            adapter.setData(data);
                            dialog.dismiss();
                        })).show();
            }
        });
        recyclerView.setAdapter(adapter);
    }
}
