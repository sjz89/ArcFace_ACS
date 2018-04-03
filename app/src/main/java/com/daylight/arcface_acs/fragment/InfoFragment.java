package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.bigkoo.pickerview.OptionsPickerView;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.adapter.DetailImageAdapter;
import com.daylight.arcface_acs.adapter.FacesListAdapter;
import com.daylight.arcface_acs.app.MyApplication;
import com.daylight.arcface_acs.bean.Building;
import com.daylight.arcface_acs.bean.Estate;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_CAMERA;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_OP;


/**
 * user信息界面
 * Created by Daylight on 2018/2/4.
 */

public class InfoFragment extends BaseFragment {
    private View root;
    private User user;
    private DetailImageAdapter mAdapter;
    private QMUIListPopup mListPopup;
    private FaceViewModel viewModel;
    private UserViewModel userViewModel;
    private int mPosition;
    private Face face;
    private List<Feature> features;
    private String title;
    private EditText editName;
    private EditText editIdNum;
    private EditText editDoorNum;
    private OptionsPickerView pickerView;
    private QMUICommonListItemView address;
    private ArrayList<Estate> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private String cid, bid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        userViewModel.getUser().observe(this, user1 -> {
            if (user1 != null) {
                user = user1;
            }
        });
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        viewModel.setAccount(SharedPreferencesUtil.getAccount(getContext()));
        viewModel.getAllFaces().observe(this,faces -> {
            if (faces != null&&faces.size()!=0) {
                face=faces.get(0);
                features=viewModel.getFeatures(face.getId());
            }
        });
        user = userViewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        if (user.getStatus() == Values.AVAILABLE) {
            title = "用户信息";
        } else {
            title = "完善信息";
        }
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_info, null);
        initEstateInfo();
        initTopBar();
        initRecyclerView();
        initCustomView();
        initGroupListView();
        return root;
    }

    private void initTopBar() {
        QMUITopBarLayout topBar = root.findViewById(R.id.topbar_info);
        topBar.setTitle(title);

        topBar.addRightImageButton(R.drawable.ic_check, R.id.topbar_done).setOnClickListener(v -> {
            //todo 提交用户信息
            int[] ids = new int[features.size()];
            for (int i = 0; i < features.size(); i++) {
                ids[i] = i;
            }
            Call<String> uploadInfo = viewModel.getHttpApi().uploadUserInfo(editName.getText().toString(),
                    editIdNum.getText().toString(), user.getPhoneNum(), cid, bid, editDoorNum.getText().toString(), ids);
            uploadInfo.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body());
                        if (jsonObject.getBoolean("flag"))
                            new QMUIDialog.MessageDialogBuilder(getContext())
                                    .setMessage("注册成功，请等待审核通过")
                                    .addAction("确定", (dialog, index) -> {
                                        user.setName(editName.getText().toString());
                                        user.setIdNum(editIdNum.getText().toString());
                                        user.setDoorNum(editDoorNum.getText().toString());
                                        user.setStatus(Values.EXAMINE);
                                        userViewModel.update(user);
                                        face.setName(user.getName());
                                        face.setIdNum(user.getIdNum());
                                        viewModel.update(face);
                                        dialog.dismiss();
                                        popBackStack();
                                    }).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                }
            });
        });
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
    }

    private void initRecyclerView() {
        face = viewModel.loadFace(user.getPhoneNum());
        if (face==null){
            face = new Face();
            face.setType("常驻");
            face.setAccount(user.getPhoneNum());
            viewModel.insert(face);
        }
        features = viewModel.getFeatures(face.getId());
        RxBusHelper.doOnMainThread(Feature.class, feature -> {
            features = viewModel.getFeatures(face.getId());
            mAdapter.setData(features);
            mAdapter.notifyItemInserted(features.size() - 1);
        });
        RecyclerView recyclerView = root.findViewById(R.id.info_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DetailImageAdapter(getContext());
        mAdapter.setData(features);
        mAdapter.setOnItemClickListener(new FacesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition = position;
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

    @SuppressWarnings("unchecked")
    private void initGroupListView() {
        GroupListView listView = root.findViewById(R.id.groupList_info);
        QMUICommonListItemView phone = listView.createItemView("手机号");
        phone.setDetailText(user.getPhoneNum());

        QMUICommonListItemView name = listView.createItemView("姓名");
        name.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        name.addAccessoryCustomView(editName);

        QMUICommonListItemView idNum = listView.createItemView("身份证号");
        idNum.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        idNum.addAccessoryCustomView(editIdNum);

        address = listView.createItemView("小区楼栋");
        address.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        if (user.getCommunityName()!=null&&user.getBuildingName()!=null)
            address.setDetailText(user.getCommunityName()+user.getBuildingName());
        else
            address.setDetailText("请选择");

        QMUICommonListItemView doorNumber = listView.createItemView("门牌号");
        doorNumber.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        doorNumber.addAccessoryCustomView(editDoorNum);

        View.OnClickListener listener = v -> {
            initOptionPicker();
            pickerView.setPicker(options1Items, options2Items);
            pickerView.show();
        };

        GroupListView.newSection(getContext())
                .addItemView(phone, null)
                .addItemView(name, null)
                .addItemView(idNum, null)
                .addItemView(address, listener)
                .addItemView(doorNumber, null)
                .addTo(listView);
    }

    private void initCustomView() {
        editName = new EditText(getContext());
        editName.setWidth(QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 150));
        editName.setGravity(Gravity.END);
        editName.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.transparent));
        editName.setText(user.getName());

        editIdNum = new EditText(getContext());
        editIdNum.setWidth(QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 180));
        editIdNum.setGravity(Gravity.END);
        editIdNum.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.transparent));
        editIdNum.setText(user.getIdNum());

        editDoorNum = new EditText(getContext());
        editDoorNum.setWidth(QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 160));
        editDoorNum.setGravity(Gravity.END);
        editDoorNum.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.transparent));
        editDoorNum.setText(user.getDoorNum());
    }

    private void initEstateInfo() {
        options1Items = new ArrayList<>();

        Call<String> getEstate = viewModel.getHttpApi().getEstate();
        getEstate.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    JSONArray jsonArray = new JSONArray(response.body());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        options1Items.add(new Estate(jsonObject.getInt("cid"),jsonObject.getString("name")));
                    }
                    for (Estate estate : options1Items) {
                        viewModel.getHttpApi().getBuilding(estate.getCid()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                try {
                                    JSONArray jsonArray = new JSONArray(response.body());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        estate.addBuilding(new Building(jsonObject.getInt("bid"),
                                                jsonObject.getString("location")));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }
        });
    }

    private void initOptionPicker() {
        if (pickerView == null) {
            for (int i = 0; i < options1Items.size(); i++) {
                ArrayList<String> BuildingList = new ArrayList<>();
                for (int c = 0; c < options1Items.get(i).getBuildings().size(); c++) {
                    String BuildingName = options1Items.get(i).getBuildings().get(c).getName();
                    BuildingList.add(BuildingName);
                }
                options2Items.add(BuildingList);
            }
            pickerView = new OptionsPickerView.Builder(getContext(), (options1, options2, options3, v) -> {
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2);
                address.getDetailTextView().setText(tx);
                cid = String.valueOf(options1Items.get(options1).getCid());
                bid = String.valueOf(options1Items.get(options1).getBuildings().get(options2).getBid());
                user.setCommunityName(options1Items.get(options1).getPickerViewText());
                user.setBuildingName(options2Items.get(options1).get(options2));
            })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("选择小区楼栋")
                    .setTitleColor(getResources().getColor(R.color.black))
                    .setCancelColor(getResources().getColor(R.color.grapefruit))
                    .setSubmitColor(getResources().getColor(R.color.text1))
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
                        switch (i) {
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


    private void showDialog() {
        final String[] choices = new String[]{"相机", "图库"};
        new QMUIDialog.MenuDialogBuilder(getContext())
                .setTitle("请选择添加方式")
                .addItems(choices, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            ContentValues values = new ContentValues(1);
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            Uri uri = getBaseFragmentActivity().getContentResolver()
                                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            ((MyApplication) (getBaseFragmentActivity().getApplicationContext())).setCaptureImage(uri);
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
            String file = ((MyApplication) getBaseFragmentActivity().getApplication()).getPath(mPath);
            startRegister(file);
        } else if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            Uri mPath = ((MyApplication) (getBaseFragmentActivity().getApplicationContext())).getCaptureImage();
            String file = ((MyApplication) getBaseFragmentActivity().getApplication()).getPath(mPath);
            startRegister(file);
        }
    }

    private void startRegister(String path) {
        viewModel.setPath(path);
        viewModel.setMe(true);
        viewModel.setMid(features.size());
        viewModel.setFace(viewModel.loadFace(user.getPhoneNum()));
        startFragment(new RegisterFragment());
    }
}
