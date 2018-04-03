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

/**
 * 公告界面
 * Created by Daylight on 2018/3/21.
 */

public class BoardFragment extends BaseFragment {
    private View view;
    private CommonAdapter adapter;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_board,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        initData();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_board);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.board);
    }

    private void initPullRefreshLayout(){
        SwipeRefreshLayout swipeRefreshLayout =view.findViewById(R.id.refresh_board);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() ->
                new Handler().postDelayed(()->swipeRefreshLayout.setRefreshing(false),2000));
    }

    private void initData(){
        List<CommonData> data=new ArrayList<>();
        CommonData commonData1=new CommonData("清明节祭祖防火安全通知","2018年03月22日");
        commonData1.setPic("http://imgsrc.baidu.com/imgad/pic/item/4b90f603738da97734fa5c06bb51f8198718e3c2.jpg");
        data.add(commonData1);
        CommonData commonData3=new CommonData("小区是我家，和谐靠大家","2018年03月20日");
        commonData3.setPic("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1521465414314&di=30bff1fb7c930b3ccfe059daab1774b7&imgtype=0&src=http%3A%2F%2Fpic27.nipic.com%2F20130220%2F4499633_175555660355_2.jpg");
        data.add(commonData3);
        CommonData commonData2=new CommonData("关于业主委员会投票的通知","2018年03月18日");
        commonData2.setPic("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1521465486388&di=feb117aaa6b75df40a16c9a611256f45&imgtype=0&src=http%3A%2F%2Fimgs.focus.cn%2Fupload%2Fbj%2F6830%2Fb_68290470.jpg");
        data.add(commonData2);
        adapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.recycler_board);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
        adapter=new CommonAdapter(getContext(), QMUICommonListItemView.VERTICAL);
        adapter.setOnCommonItemClickListener((v,position)-> startFragment(new NewsFragment()));
        recyclerView.setAdapter(adapter);
    }
}
