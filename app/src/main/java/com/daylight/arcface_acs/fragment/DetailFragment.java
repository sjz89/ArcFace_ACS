package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.bigkoo.pickerview.TimePickerView;
import com.daylight.arcface_acs.app.MyApplication;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.adapter.DetailImageAdapter;
import com.daylight.arcface_acs.adapter.FacesListAdapter;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_CAMERA;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_OP;

public class DetailFragment extends QMUIFragment {
    private View root;
    private QMUIListPopup mListPopup;
    private FaceViewModel viewModel;
    private DetailImageAdapter mAdapter;
    private int mPosition;
    private List<Feature> features;
    private Face face;
    private String date;
    private QMUICommonListItemView validDate;
    private int choice;
    private Calendar selectDate;
    private TimePickerView pickerView;

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root= LayoutInflater.from(getBaseFragmentActivity()).inflate(R.layout.fragment_detail,null);
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        face=viewModel.getFace();
        features=viewModel.getFeatures(face.getId());
        RxBusHelper.doOnMainThread(Feature.class,feature -> {
            features=viewModel.getFeatures(face.getId());
            mAdapter.setData(features);
            mAdapter.notifyItemInserted(features.size()-1);
        });
        initTopBar();
        initRecyclerView();
        initGroupList();
        return root;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=root.findViewById(R.id.topbar_detail);
        topBar.setTitle("成员详情");
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
    }
    private void initRecyclerView(){
        RecyclerView recyclerView = root.findViewById(R.id.detail_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter =new DetailImageAdapter(getContext());
        mAdapter.setData(features);
        mAdapter.setOnItemClickListener(new FacesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition=position;
                initListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.show(view);
            }
            @Override
            public void onFootClick(View view) {
                viewModel.setNew(false);
                showDialog();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void initGroupList(){
        GroupListView groupListView=root.findViewById(R.id.groupList_detail);
        QMUICommonListItemView name=groupListView.createItemView("姓名");
        name.setDetailText(face.getName());
        QMUICommonListItemView type=groupListView.createItemView("类别");
        type.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        type.setDetailText(face.getType());
        validDate=groupListView.createItemView("有效期");
        validDate.setDetailText(face.getValidDate());
        View.OnClickListener listener= v -> {
            if (v instanceof QMUICommonListItemView){
                switch (((QMUICommonListItemView) v).getText().toString()){
                    case "姓名":
                        QMUIDialog.EditTextDialogBuilder builder=new QMUIDialog.EditTextDialogBuilder(getContext());
                        builder.setTitle("更改姓名")
                                .setPlaceholder("在此输入姓名")
                                .setInputType(InputType.TYPE_CLASS_TEXT)
                                .addAction("取消",((dialog, index) -> dialog.dismiss()))
                                .addAction("确定",((dialog, index) -> {
                                    face.setName(builder.getEditText().getText().toString());
                                    name.setDetailText(face.getName());
                                    viewModel.update(face);
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "类别":
                        String[] types=new String[]{"常驻","访客"};
                        new QMUIDialog.CheckableDialogBuilder(getContext())
                                .setCheckedIndex(choice)
                                .addItems(types,((dialog, which) -> {
                                    if (which==0){
                                        choice=0;
                                        groupListView.hideItemView(0,2);
                                        face.setType(types[0]);
                                        face.setValidDate(null);
                                        viewModel.update(face);
                                    }else{
                                        choice=1;
                                        groupListView.showItemView(0,2);
                                        face.setType(types[1]);
                                        viewModel.update(face);
                                    }
                                    type.setDetailText(types[choice]);
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "有效期":
                        initDatePicker();
                        if (face.getValidDate()!=null){
                            selectDate=Calendar.getInstance();
                            String[] times=face.getValidDate().split("-");
                            selectDate.set(Integer.parseInt(times[0]),Integer.parseInt(times[1])-1,Integer.parseInt(times[2]));
                            pickerView.setDate(selectDate);
                        }
                        pickerView.show();
                        break;
                }
            }
        };
        GroupListView.newSection(getContext())
                .addItemView(name,listener)
                .addItemView(type,listener)
                .addItemView(validDate,listener)
                .addTo(groupListView);
        if (face.getType().equals("常驻")) {
            choice=0;
            groupListView.hideItemView(0, 2);
        }else
            choice=1;
    }

    private void initDatePicker() {
        if (pickerView==null) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(new Date());
            Calendar endDate = Calendar.getInstance();
            endDate.set(2028, 11, 31);

            pickerView = new TimePickerView.Builder(getContext(), (date, v1) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                this.date = sdf.format(date);
                validDate.setDetailText(this.date);
                face.setValidDate(this.date);
                viewModel.update(face);
            })
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("选择时间")
                    .setTitleColor(getResources().getColor(R.color.black))
                    .setCancelColor(getResources().getColor(R.color.grapefruit))
                    .setSubmitColor(getResources().getColor(R.color.text1))
                    .setRangDate(startDate, endDate)
                    .build();
        }
    }

    private void initListPopupIfNeed() {
        if (mListPopup == null) {
            String[] listItems = new String[]{
                    "设为头像",
                    "删除照片"
            };
            List<String> data = new ArrayList<>();
            Collections.addAll(data, listItems);
            ArrayAdapter adapter = new ArrayAdapter<>(getBaseFragmentActivity(), R.layout.item_menu, data);
            mListPopup = new QMUIListPopup(getBaseFragmentActivity(), QMUIPopup.DIRECTION_BOTTOM, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 100),
                    QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 200), (adapterView, view, i, l) -> {
                        switch (i){
                            case 0:
                                mListPopup.dismiss();
                                face.setFaceData(features.get(mPosition).getImageData());
                                viewModel.update(face);
                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                    .setTipWord("设置成功")
                                    .create(true);
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,1000);
                                break;
                            case 1:
                                viewModel.deleteFeature(features.get(mPosition));
                                features.remove(mPosition);
                                mAdapter.setData(features);
                                mAdapter.notifyItemRemoved(mPosition);
                                mListPopup.dismiss();
                                break;
                        }
                    });
        }
    }

    private void showDialog(){
        final String[] choices=new String[]{"相机","图库"};
        new QMUIDialog.MenuDialogBuilder(getContext())
                .setTitle("请选择添加方式")
                .addItems(choices, (dialog, which) -> {
                    switch (which){
                        case 0:
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            ContentValues values = new ContentValues(1);
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            Uri uri = getBaseFragmentActivity().getContentResolver()
                                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            ((MyApplication)(getBaseFragmentActivity().getApplicationContext())).setCaptureImage(uri);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, REQUEST_CODE_IMAGE_CAMERA);
                            break;
                        case 1:
                            Intent getImageByalbum = new Intent(Intent.ACTION_GET_CONTENT);
                            getImageByalbum.addCategory(Intent.CATEGORY_OPENABLE);
                            getImageByalbum.setType("image/jpeg");
                            startActivityForResult(getImageByalbum, REQUEST_CODE_IMAGE_OP);
                            break;
                    }
                    dialog.dismiss();
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_OP && resultCode == RESULT_OK) {
            Uri mPath = data.getData();
            String file = ((MyApplication)getBaseFragmentActivity().getApplication()).getPath(mPath);
            startRegister(file);
        }else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            Uri mPath = ((MyApplication)(getBaseFragmentActivity().getApplicationContext())).getCaptureImage();
            String file = ((MyApplication)getBaseFragmentActivity().getApplication()).getPath(mPath);
            startRegister(file);
        }
    }

    private void startRegister(String path){
        viewModel.setPath(path);
        startFragment(new RegisterFragment());
    }
}
