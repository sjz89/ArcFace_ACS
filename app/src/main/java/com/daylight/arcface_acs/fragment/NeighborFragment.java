package com.daylight.arcface_acs.fragment;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.CommonAdapter;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.DateUtil;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeighborFragment extends BaseFragment{
    private View view;
    private UserViewModel viewModel;
    private FaceViewModel faceViewModel;
    private User user;
    private QMUITabSegment mTabSegment;
    private Map<ContentPage, View> mPageMap = new HashMap<>();
    private ContentPage mDestPage = ContentPage.Item1;
    private CommonAdapter recentAdapter;
    private List<Recent> recentList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        faceViewModel=ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        user=viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        viewModel.setRecent();
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_neighbor,null);
        initTopBar();
        initTabAndPager();
        viewModel.getRecentList().observe(this,recents -> {
            if (recents!=null&&recents.size()!=0){
                List<CommonData> data=new ArrayList<>();
                recentList=recents;
                for (Recent recent:recents){
                    User neighbor=viewModel.loadUser(recent.getNeighbor());
                    Face face=faceViewModel.loadFace(recent.getNeighbor());
                    CommonData commonData=new CommonData(neighbor.getName(),recent.getLastMsg());
                    commonData.setTime(DateUtil.autoTransFormat(recent.getTime()));
                    commonData.setImage(face.getFaceData());
                    data.add(commonData);
                }
                recentAdapter.setData(data);
            }
        });
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_neighbor);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.neighbor);
    }

    private void initTabAndPager() {
        mTabSegment=view.findViewById(R.id.tabSegment_neighbor);
        ViewPager mContentViewPager = view.findViewById(R.id.contentViewPager_neighbor);
        mContentViewPager.setAdapter(mPagerAdapter);
        mContentViewPager.setCurrentItem(mDestPage.getPosition(), false);
        mTabSegment.setHasIndicator(true);
        mTabSegment.setIndicatorPosition(false);
        mTabSegment.setIndicatorWidthAdjustContent(true);
        mTabSegment.addTab(new QMUITabSegment.Tab("邻居列表"));
        mTabSegment.addTab(new QMUITabSegment.Tab("聊天记录"));
        mTabSegment.setupWithViewPager(mContentViewPager, false);
        mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
        mTabSegment.addOnTabSelectedListener(new QMUITabSegment.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onTabUnselected(int index) {}

            @Override
            public void onTabReselected(int index) {
                mTabSegment.hideSignCountView(index);
            }

            @Override
            public void onDoubleTap(int index) {}
        });
    }

    @SuppressLint("InflateParams")
    private View getPageView(ContentPage page) {
        View view = mPageMap.get(page);
        if (view == null) {
            view=LayoutInflater.from(getContext()).inflate(R.layout.layout_recyclerview,null);
            RecyclerView recyclerView=view.findViewById(R.id.recycler_page);
            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getBaseFragmentActivity(),DividerItemDecoration.VERTICAL));
            if (page==ContentPage.Item1) {
                CommonAdapter adapter1 = new CommonAdapter(getContext(), QMUICommonListItemView.HORIZONTAL);
                recyclerView.setAdapter(adapter1);
                List<CommonData> data=new ArrayList<>();
                List<User> neighbor=viewModel.getNeighbors(user);
                for (User user:neighbor){
                    CommonData commonData=new CommonData(user.getName(),user.getDoorNum());
                    commonData.setImage(faceViewModel.loadFace(user.getPhoneNum()).getFaceData());
                    data.add(commonData);
                }
                adapter1.setData(data);
                adapter1.setOnCommonItemClickListener((v,position)->{
                    viewModel.setNeighbor(neighbor.get(position));
                    startFragment(new ChatFragment());
                });
            }else{
                recentAdapter = new CommonAdapter(getBaseFragmentActivity(), QMUICommonListItemView.VERTICAL,true);
                recentAdapter.setOnCommonItemClickListener((v,position)->{
                    viewModel.setNeighbor(viewModel.loadUser(recentList.get(position).getNeighbor()));
                    startFragment(new ChatFragment());
                });
                recyclerView.setAdapter(recentAdapter);
            }
            mPageMap.put(page, view);
        }
        return view;
    }

    private PagerAdapter mPagerAdapter = new PagerAdapter() {
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return ContentPage.SIZE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, int position) {
            ContentPage page = ContentPage.getPage(position);
            View view = getPageView(page);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

    };

    public enum ContentPage {
        Item1(0),
        Item2(1);
        public static final int SIZE = 2;
        private final int position;

        ContentPage(int pos) {
            position = pos;
        }

        public static ContentPage getPage(int position) {
            switch (position) {
                case 0:
                    return Item1;
                case 1:
                    return Item2;
                default:
                    return Item1;
            }
        }

        public int getPosition() {
            return position;
        }
    }
}
