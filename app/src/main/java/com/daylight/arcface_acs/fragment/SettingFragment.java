package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.dialog.ChangePasswordDialogBuilder;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.activity.LoginActivity;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.dialog.PatternLockDialog;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.view.InfoItemView;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.dialog.PinLockDialog;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

/**
 *设置界面
 * Created by Daylight on 2018/1/20.
 */

public class SettingFragment extends BaseFragment{
    private View view;
    private UserViewModel viewModel;
    private FaceViewModel faceViewModel;
    private User user;
    private QMUICommonListItemView info;
    private QMUITopBarLayout topBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        user=viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        faceViewModel=ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        faceViewModel.getAllFaces().observe(getBaseFragmentActivity(),faces -> {
            if (faces!=null&&info!=null)
                ((InfoItemView)info).setImageDrawable(faces.get(0).getFaceData());
        });
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView() {
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting,null);
        initTopBar();
        initGroupListView();
        return view;
    }

    private void initTopBar(){
        topBar=view.findViewById(R.id.topbar_setting);
        topBar.setTitle(R.string.title_setting);
    }

    private void initGroupListView() {
        GroupListView mGroupListView=view.findViewById(R.id.groupList_setting);

        info = new InfoItemView(getContext(),null,
                user.getName(),user.getCommunityName()+user.getBuildingName()+user.getDoorNum(),
                QMUICommonListItemView.VERTICAL,QMUICommonListItemView.ACCESSORY_TYPE_NONE);

        if (faceViewModel.loadFace(user.getPhoneNum()).getFaceData()!=null)
            ((InfoItemView)info).setImageDrawable(faceViewModel.loadFace(user.getPhoneNum()).getFaceData());

        QMUICommonListItemView patternPassword = mGroupListView.createItemView("手势密码");
        patternPassword.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        patternPassword.getSwitch().setChecked(user.isHasPatternLock());

        QMUICommonListItemView changePattern=mGroupListView.createItemView("修改手势密码");
        changePattern.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        changePattern.getTextView().setTextColor(getResources().getColor(R.color.text_second_level));
        changePattern.getTextView().setPadding(16,0,0,0);

        QMUICommonListItemView pinPassword = mGroupListView.createItemView("修改开门密码");

        if (user.getPin()==null)
            pinPassword.setText("设置开门密码");

        QMUICommonListItemView loginPassword = mGroupListView.createItemView("修改账号密码");

        QMUICommonListItemView checkDevices = mGroupListView.createItemView("登陆设备");
        checkDevices.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView logout = mGroupListView.createItemView("退出登录");

        View.OnClickListener onClickListener = v -> {
            if (v instanceof QMUICommonListItemView) {
                String text = ((QMUICommonListItemView) v).getText().toString();
                switch (text){
                    case "业主信息":
                        break;
                    case "手势密码":
                        if (patternPassword.getSwitch().isChecked()) {
                            user.setHasPatternLock(false);
                            viewModel.update(user);
                        } else {
                            if (user.getPattern()==null) {
                                PatternLockDialog patternLockDialog = new PatternLockDialog(getContext(),user);
                                patternLockDialog.setPattern();
                            }else {
                                user.setHasPatternLock(true);
                                viewModel.update(user);
                            }
                        }
                        break;
                    case "修改手势密码":
                        QMUIDialog.EditTextDialogBuilder builder= new QMUIDialog.EditTextDialogBuilder(getContext());
                        builder.setTitle("验证密码")
                                .setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .setPlaceholder("请输入登陆密码")
                                .addAction("取消",(dialog, index) -> dialog.dismiss())
                                .addAction("确定",((dialog, index) -> {
                                    if (builder.getEditText().getText().toString().equals(user.getPassword())) {
                                        dialog.dismiss();
                                        new Handler().postDelayed(()->{
                                            PatternLockDialog patternLockDialog = new PatternLockDialog(getContext(), user);
                                            patternLockDialog.setPattern();
                                        },300);
                                    }else{
                                        showTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL,"密码错误");
                                    }
                                })).show();
                        break;
                    case "设置开门密码":
                        PinLockDialog pinLockDialog = new PinLockDialog(getContext(),user);
                        pinLockDialog.setPin();
                        break;
                    case "修改开门密码":
                        QMUIDialog.EditTextDialogBuilder builder1= new QMUIDialog.EditTextDialogBuilder(getContext());
                        builder1.setTitle("验证密码")
                                .setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .setPlaceholder("请输入登陆密码")
                                .addAction("取消",(dialog, index) -> dialog.dismiss())
                                .addAction("确定",((dialog, index) -> {
                                    if (builder1.getEditText().getText().toString().equals(user.getPassword())) {
                                        dialog.dismiss();
                                        new Handler().postDelayed(()->{
                                            PinLockDialog pinLockDialog1=new PinLockDialog(getContext(),user);
                                            pinLockDialog1.setPin();
                                        },300);
                                    }else{
                                        showTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL,"密码错误");
                                    }
                                })).show();
                        break;
                    case "修改账号密码":
                        QMUIDialog.EditTextDialogBuilder builder2=new QMUIDialog.EditTextDialogBuilder(getContext());
                        builder2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                .setTitle("验证密码")
                                .setPlaceholder("请输入当前密码")
                                .addAction("取消",(dialog, index) -> dialog.dismiss())
                                .addAction("确定",(dialog, index) -> {
                                    dialog.dismiss();
                                    if (builder2.getEditText().getText().toString().equals(user.getPassword())){
                                        ChangePasswordDialogBuilder dialogBuilder=new ChangePasswordDialogBuilder(getContext());
                                        dialogBuilder.setTitle("更改密码")
                                                .addAction("取消",(dialog1,index1)->dialog1.dismiss())
                                                .addAction("确定",((dialog1, index1) -> {
                                                    if (dialogBuilder.validPassword()){
                                                        user.setPassword(dialogBuilder.getPassword());
                                                        SharedPreferencesUtil.setPassword(getContext(),
                                                                dialogBuilder.getPassword());
                                                        viewModel.update(user);
                                                        dialog1.dismiss();
                                                        showTipDialog(QMUITipDialog.Builder.ICON_TYPE_SUCCESS,"密码修改成功");
                                                    }
                                                })).show();
                                    }else{
                                        showTipDialog(QMUITipDialog.Builder.ICON_TYPE_FAIL,"密码错误");
                                    }
                                }).show();
                        break;
                    case "登陆设备":
                        BaseFragment deviceFragment=new DeviceFragment();
                        startFragment(deviceFragment);
                        break;
                    case "退出登录":
                        user.setHasPatternLock(false);
                        viewModel.update(user);
                        SharedPreferencesUtil.setPassword(getContext(),"");
                        getBaseFragmentActivity().startActivity(new Intent(getBaseFragmentActivity(), LoginActivity.class));
                        getBaseFragmentActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        };

        GroupListView.newSection(getContext())
                .addItemView(info,v -> startFragment(new InfoFragment()))
                .addTo(mGroupListView);

        GroupListView.newSection(getContext())
                .addItemView(patternPassword,onClickListener)
                .addItemView(changePattern,onClickListener)
                .addItemView(pinPassword, onClickListener)
                .addItemView(loginPassword, onClickListener)
                .addTo(mGroupListView);

        GroupListView.newSection(getContext())
                .addItemView(checkDevices, onClickListener)
                .addTo(mGroupListView);

        GroupListView.newSection(getContext())
                .addItemView(logout,onClickListener)
                .addTo(mGroupListView);

        if (!user.isHasPatternLock())
            mGroupListView.hideItemView(1,1);

        RxBusHelper.doOnChildThread(User.class,user1 -> viewModel.update(user1));

        viewModel.getUser().observe(this, user -> {
            if (user!=null) {
                this.user=user;
                if (user.getStatus()== Values.EXAMINE){
                    topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.grapefruit));
                    topBar.setSubTitle(R.string.account_examine);
                }else{
                    topBar.setBackgroundColor(getBaseFragmentActivity().getResources().getColor(R.color.app_color_blue_2));
                    topBar.setSubTitle(null);
                }
                if (user.getPin() == null)
                    pinPassword.setText("设置开门密码");
                else
                    pinPassword.setText("修改开门密码");
                if (!user.isHasPatternLock()){
                    patternPassword.getSwitch().setChecked(false);
                    mGroupListView.hideItemView(1,1);
                }else{
                    patternPassword.getSwitch().setChecked(true);
                    mGroupListView.showItemView(1,1);
                }
            }
        });
    }

    private void showTipDialog(int iconType,String tipWord){
        QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                .setIconType(iconType)
                .setTipWord(tipWord)
                .create(true);
        tipDialog.show();
        new Handler().postDelayed(tipDialog::dismiss,1000);
    }
    @Override
    protected boolean canDragBack() {
        return false;
    }
}
