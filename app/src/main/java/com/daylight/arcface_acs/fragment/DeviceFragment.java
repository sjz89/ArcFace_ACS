package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.utils.DeviceInfoUtil;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

/**
 * 登陆设备
 * Created by Daylight on 2018/1/20.
 */

public class DeviceFragment extends BaseFragment {
    private View view;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_device,null);
        initTopBar();
        initGroupList();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_device);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.title_device);
    }

    private void initGroupList(){
        QMUIGroupListView mGroupListView=view.findViewById(R.id.groupList_device);
        QMUICommonListItemView currentDevice=mGroupListView.createItemView(DeviceInfoUtil.getDeviceBrand()+DeviceInfoUtil.getDeviceModel());
        currentDevice.setDetailText("本机");

        QMUIGroupListView.newSection(getContext())
                .addItemView(currentDevice,null)
                .addTo(mGroupListView);
    }
}
