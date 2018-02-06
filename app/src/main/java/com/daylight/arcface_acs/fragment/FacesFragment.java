package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.adapter.FacesListAdapter;
import com.daylight.arcface_acs.app.MyApplication;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_CAMERA;
import static com.daylight.arcface_acs.Values.REQUEST_CODE_IMAGE_OP;

/**
 *人脸注册界面
 * Created by Daylight on 2018/1/20.
 */

public class FacesFragment extends BaseFragment{

    private View view;
    private FaceViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FacesListAdapter mAdapter;
    private QMUIListPopup mListPopup;
    private List<Face> mFaces;
    private int mPosition;
    private QMUITopBarLayout topBar;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(){
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_faces,null);
        initTopBar();
        initPullRefreshLayout();
        initRecyclerView();
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        viewModel.setAccount(SharedPreferencesUtil.getAccount(getContext()));
        viewModel.getAllFaces().observe(this,faces -> {
            mAdapter.setFaces(faces);
            mFaces=faces;
            if (mFaces!=null&&mFaces.size()!=0&&viewModel.getFeature()!=null) {
                Feature feature = viewModel.getFeature();
                feature.setFaceId(mFaces.get(mFaces.size() - 1).getId());
                viewModel.insertFeature(feature);
                viewModel.setFeature(null);
            }
        });
        RxBusHelper.doOnMainThread(Uri.class,uri -> {
            Face face=new Face();
            face.setAccount(SharedPreferencesUtil.getAccount(getContext()));
            viewModel.setFace(face);
            viewModel.setNew(true);
            String file=uri.getPath();
            startRegister(file);
        });
        return view;
    }
    private void initTopBar(){
        topBar=view.findViewById(R.id.topbar_faces);
        topBar.setTitle(R.string.title_faces);
        ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class).getUser().observe(getBaseFragmentActivity(),user -> {
            if (user!=null){
                if (user.getStatus()== Values.EXAMINE){
                    topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.grapefruit));
                    topBar.setSubTitle(R.string.account_examine);
                }else{
                    topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.app_color_blue_2));
                    topBar.setSubTitle(null);
                }
            }
        });
    }

    private void initPullRefreshLayout(){
        swipeRefreshLayout =view.findViewById(R.id.pull_to_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.app_color_blue,R.color.text1,R.color.grapefruit);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(()->swipeRefreshLayout.setRefreshing(false),2000));
    }

    private void initRecyclerView(){
        RecyclerView facesList = view.findViewById(R.id.faces_list);
        mAdapter =new FacesListAdapter(getContext());
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),4);
        facesList.setLayoutManager(layoutManager);
        mAdapter.setOnItemClickListener(new FacesListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPosition=position;
                initListPopupIfNeed();
                mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                if (position>11&&position/4>(layoutManager.findLastCompletelyVisibleItemPosition()/4)-2)
                    mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                else
                    mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);
                mListPopup.show(view);
            }
            @Override
            public void onFootClick(View view) {
                Face face=new Face();
                face.setAccount(SharedPreferencesUtil.getAccount(getContext()));
                viewModel.setFace(face);
                viewModel.setNew(true);
                viewModel.setMid(0);
                showDialog();
            }
        });
        facesList.setAdapter(mAdapter);
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
    private void initListPopupIfNeed() {
        if (mListPopup == null) {
            String[] listItems = new String[]{
                    "添加照片",
                    "查看详情",
                    "删除成员"
            };
            List<String> data = new ArrayList<>();
            Collections.addAll(data, listItems);
            ArrayAdapter adapter = new ArrayAdapter<>(getBaseFragmentActivity(), R.layout.item_menu, data);
            mListPopup = new QMUIListPopup(getBaseFragmentActivity(), QMUIPopup.DIRECTION_BOTTOM, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 100),
                    QMUIDisplayHelper.dp2px(getBaseFragmentActivity(), 200), (adapterView, view, i, l) -> {
                        switch (i){
                            case 0:
                                viewModel.setFace(mFaces.get(mPosition));
                                viewModel.setNew(false);
                                showDialog();
                                break;
                            case 2:
                                if (mPosition==0){
                                    new QMUIDialog.MessageDialogBuilder(getContext())
                                            .setTitle("删除失败")
                                            .setMessage("不能删除自己哦^o^!")
                                            .addAction("知道了",(dialog, index) -> dialog.dismiss())
                                            .show();
                                }else {
                                    viewModel.delete(mFaces.get(mPosition));
                                }
                                break;
                            case 1:
                                if (mPosition==0)
                                    startFragment(new InfoFragment());
                                else {
                                    viewModel.setFace(mFaces.get(mPosition));
                                    startFragment(new DetailFragment());
                                }
                                break;
                        }
                        mListPopup.dismiss();
                    });
        }
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
    @Override
    protected boolean canDragBack() {
        return false;
    }
}
