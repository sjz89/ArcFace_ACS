package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.bean.Record;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.DateUtil;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/18.
 */

public class RecordFragment extends BaseFragment {
    private View view;
    private UserViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonAdapter adapter;
    private User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        user=viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        RxBusHelper.doOnMainThread(Integer.class, integer -> {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            if(integer== Values.RECORD_UPDATE_DONE){
                initData();
            }else if (integer==Values.REQUEST_FAIL)
                Toast.makeText(getBaseFragmentActivity(),"网络异常",Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_record,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        initData();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_history);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.record);
    }

    private void initPullRefreshLayout(){
        swipeRefreshLayout =view.findViewById(R.id.refresh_record);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.updateRecords());
    }

    private void initData(){
        List<CommonData> data=new ArrayList<>();
        List<Record> records=viewModel.getRecords(user.getPhoneNum());
        if (records!=null&&records.size()!=0) {
            for (Record record : records) {
                CommonData commonData=new CommonData(record.getText(),record.getSubText());
                commonData.setIcon(R.drawable.ic_record);
                commonData.setTime(DateUtil.longToString(record.getTime()));
                data.add(commonData);
            }
        }
        adapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_history);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL,true);
        recyclerView.setAdapter(adapter);
    }
}
