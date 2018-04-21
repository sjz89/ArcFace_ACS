package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.PresetAdapter;
import com.daylight.arcface_acs.adapter.TemporaryImageAdapter;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.PresetData;
import com.daylight.arcface_acs.bean.TempData;
import com.daylight.arcface_acs.utils.DateUtil;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TemporaryFragment extends BaseFragment {
    private View view;
    private TemporaryImageAdapter mTemporaryImageAdapter;
    private PresetAdapter mPresetAdapter;
    private List<TempData> data;
    private QMUICommonListItemView type,time,date,door;
    private int checkedData;
    private int checkedType;
    private int checkedDate;
    private int checkedTime;
    private String[] types;
    private String[] doors;
    private String[] times;
    private String[] dates_once;
    private String[] dates_loop;
    private int[] checkedDoors;
    private int[] checkedDates;
    private List<Face> faces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FaceViewModel faceViewModel= ViewModelProviders.of(this).get(FaceViewModel.class);
        faces=faceViewModel.getFaces();
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_temporary,null);
        initTopBar();
        initRecyclerView();
        initData();
        initPresetData();
        initValues();
        initGroupListView();
        initAuthorizeBtn();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_temp);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.temporary);
    }

    private void initData(){
        data=new ArrayList<>();
        data.add(new TempData(faces.get(faces.size()-1).getFaceData(),true));
        data.add(new TempData(faces.get(faces.size()-2).getFaceData(),false));
        data.add(new TempData(faces.get(faces.size()-3).getFaceData(),false));
        mTemporaryImageAdapter.setData(data);
    }

    private void initRecyclerView(){
        RecyclerView recyclerView=view.findViewById(R.id.temp_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        mTemporaryImageAdapter =new TemporaryImageAdapter(getBaseFragmentActivity());
        mTemporaryImageAdapter.setOnTempFaceClickListener((v, position)->{
            for (TempData tempData:data){
                tempData.setChecked(false);
            }
            data.get(position).setChecked(true);
            checkedData=position;
            mTemporaryImageAdapter.setData(data);
        });
        recyclerView.setAdapter(mTemporaryImageAdapter);
    }

    private void initPresetData(){
        RecyclerView recyclerView=view.findViewById(R.id.temp_preset);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mPresetAdapter=new PresetAdapter(getContext());

        List<PresetData> data=new ArrayList<>();
        data.add(new PresetData("临时访客",false));
        data.add(new PresetData("外卖人员",false));
        data.add(new PresetData("快递人员",false));
        data.add(new PresetData("保洁人员",false));
        data.add(new PresetData("送水人员",false));
        data.add(new PresetData("自定义",true));
        mPresetAdapter.setData(data);
        mPresetAdapter.setOnPresetItemClickListener((v,position)->{
            for (PresetData presetData:data)
                presetData.setChecked(false);
            data.get(position).setChecked(true);
            mPresetAdapter.setData(data);

            switch (position){
                case 0:
                    setGroupListItem(0,6,new int[]{1});
                    break;
                case 1:
                    setGroupListItem(0,1,new int[]{0});
                    break;
                case 2:
                    setGroupListItem(0,2,new int[]{0});
                    break;
                case 3:
                    setGroupListItem(1,5,new int[]{DateUtil.getWeek(new Date())-1});
                    break;
                case 4:
                    setGroupListItem(1,3,new int[]{DateUtil.getWeek(new Date())-1});
                    break;
            }
        });
        recyclerView.setAdapter(mPresetAdapter);
    }

    private void initValues(){
        checkedType=0;
        checkedTime=0;
        checkedDate=0;

        types=new String[]{"一次性人员","定期人员"};
        doors=new String[]{"东门","西门","南门","北门","15栋","13栋","10栋","9栋","8栋","5栋","2栋"};
        times=new String[]{"10分钟","30分钟","1小时","2小时","6小时","12小时","1天"};
        dates_once =new String[]{"一天","一周","一个月"};
        dates_loop=new String[]{"周日","周一","周二","周三","周四","周五","周六"};
        checkedDoors =new int[]{};
        checkedDates=new int[]{};
    }

    private void initGroupListView(){
        GroupListView groupListView=view.findViewById(R.id.groupList_temp);

        type=groupListView.createItemView(null,"人员类型",types[checkedType],
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        time=groupListView.createItemView(null,"通行时间",times[checkedTime],
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        date=groupListView.createItemView(null,"有效日期", dates_once[checkedDate],
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        door=groupListView.createItemView(null,"授权门禁","请选择",
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        View.OnClickListener listener=v->{
            if (v instanceof QMUICommonListItemView){
                switch (((QMUICommonListItemView) v).getText().toString()){
                    case "人员类型":
                        new QMUIDialog.CheckableDialogBuilder(getContext())
                                .setCheckedIndex(checkedType)
                                .addItems(types,((dialog, which) -> {
                                    checkedType=which;
                                    type.setDetailText(types[checkedType]);
                                    mPresetAdapter.setLastItemCheck();
                                    if (which==0){
                                        checkedDate=0;
                                        date.setDetailText(dates_once[0]);
                                    }else{
                                        checkedDates=new int[]{DateUtil.getWeek(new Date())-1};
                                        date.setDetailText(dates_loop[checkedDates[0]]);
                                    }
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "通行时间":
                        new QMUIDialog.CheckableDialogBuilder(getContext())
                                .setCheckedIndex(checkedTime)
                                .addItems(times,((dialog, which) -> {
                                    checkedTime=which;
                                    time.setDetailText(times[checkedTime]);
                                    mPresetAdapter.setLastItemCheck();
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "有效日期":
                        if (checkedType==0) {
                            new QMUIDialog.CheckableDialogBuilder(getContext())
                                    .setCheckedIndex(checkedDate)
                                    .addItems(dates_once, ((dialog, which) -> {
                                        checkedDate = which;
                                        date.setDetailText(dates_once[checkedDate]);
                                        mPresetAdapter.setLastItemCheck();
                                        dialog.dismiss();
                                    })).show();
                        }else{
                            StringBuilder stringBuilder=new StringBuilder();
                            QMUIDialog.MultiCheckableDialogBuilder builder=new QMUIDialog.MultiCheckableDialogBuilder(getContext())
                                    .setCheckedItems(checkedDates)
                                    .addItems(dates_loop,((dialog, which) -> {}));
                            builder.addAction("取消",((dialog, index) -> dialog.dismiss()))
                                    .addAction("确定",((dialog, index) -> {
                                        checkedDates=builder.getCheckedItemIndexes();
                                        int i;
                                        for (i=0;i<checkedDates.length-1;i++){
                                            stringBuilder.append(dates_loop[checkedDates[i]]).append(",");
                                        }
                                        stringBuilder.append(dates_loop[checkedDates[i]]);
                                        date.setDetailText(stringBuilder.toString());
                                        dialog.dismiss();
                                    })).show();
                        }
                        break;
                    case "授权门禁":
                        StringBuilder stringBuilder=new StringBuilder();
                        QMUIDialog.MultiCheckableDialogBuilder builder=new QMUIDialog.MultiCheckableDialogBuilder(getContext())
                                .setCheckedItems(checkedDoors)
                                .addItems(doors,((dialog, which) -> { }));
                        builder.addAction("取消",((dialog, index) -> dialog.dismiss()))
                                .addAction("确定",((dialog, index) -> {
                                    checkedDoors =builder.getCheckedItemIndexes();
                                    int i;
                                    for (i=0; i< checkedDoors.length-1; i++)
                                        stringBuilder.append(doors[checkedDoors[i]]).append(",");
                                    stringBuilder.append(checkedDoors.length!=0?doors[checkedDoors[i]]:"请选择");
                                    door.setDetailText(stringBuilder.toString());
                                    dialog.dismiss();
                                })).show();
                        break;
                }
            }
        };

        GroupListView.newSection(getContext())
                .setTitle("详细选项")
                .addItemView(type,listener)
                .addItemView(time,listener)
                .addItemView(date,listener)
                .addItemView(door,listener)
                .addTo(groupListView);
    }

    private void setGroupListItem(int type,int time,int[] date){
        checkedType=type;
        checkedTime=time;
        if (type==0)
            checkedDate=date[0];
        else
            checkedDates=date;
        this.type.setDetailText(types[checkedType]);
        this.time.setDetailText(times[checkedTime]);
        this.date.setDetailText(checkedType==0?dates_once[checkedDate]:dates_loop[checkedDates[0]]);
    }

    private void initAuthorizeBtn(){
        Button button = view.findViewById(R.id.temp_btn);
        button.setText("授权");
        button.setOnClickListener(v -> {
            if (checkedDoors.length==0)
                Toast.makeText(getContext(),"请选择授权门禁",Toast.LENGTH_LONG).show();
            else {
                data.remove(checkedData);
                mTemporaryImageAdapter.setData(data);
                mPresetAdapter.setLastItemCheck();
                setGroupListItem(0, 0, new int[]{0});
                door.setDetailText("请选择");
                checkedDoors = new int[]{};
            }
        });
    }
}
