package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.utils.EncryptUtil;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.bean.User;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册界面
 * Created by Daylight on 2018/1/26.
 */

public class SignUpFragment extends QMUIFragment {
    private View root;
    private UserViewModel viewModel;
    private MaterialEditText account;
    private MaterialEditText password;
    private MaterialEditText checkPwd;
    private MaterialEditText msgCode;
    private TextInputLayout accountInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout codeInputLayout;
    private TextInputLayout checkPwdInputLayout;
    private Button getCode;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        getBaseFragmentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root= LayoutInflater.from(getContext()).inflate(R.layout.fragment_signup,null);
        initView();
        return root;
    }
    private void initView() {
        account = root.findViewById(R.id.sign_up_account);
        password = root.findViewById(R.id.sign_up_password);
        checkPwd = root.findViewById(R.id.sign_up_password_check);
        msgCode = root.findViewById(R.id.sign_up_msg_code);
        account.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        checkPwd.addTextChangedListener(textWatcher);
        msgCode.addTextChangedListener(textWatcher);
        accountInputLayout=root.findViewById(R.id.account_input_layout);
        passwordInputLayout=root.findViewById(R.id.password_input_layout);
        checkPwdInputLayout=root.findViewById(R.id.check_pwd_input_layout);
        codeInputLayout=root.findViewById(R.id.code_input_layout);
        (root.findViewById(R.id.btn_sign_up)).setOnClickListener(v -> {
            if (validInput()) {
                QMUITipDialog loadDialog=new QMUITipDialog.Builder(getContext())
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("注册中")
                        .create(true);
                loadDialog.show();
                Call<String> call=viewModel.getHttpApi().register(msgCode.getText().toString()
                    ,account.getText().toString(), EncryptUtil.shaEncrypt(password.getText().toString()));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.getBoolean("flag")) {
                                loadDialog.dismiss();
                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                        .setTipWord("注册成功")
                                        .create(true);
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,2000);
                                User user = new User();
                                user.setPhoneNum(account.getText().toString());
                                user.setPassword(EncryptUtil.shaEncrypt(password.getText().toString()));
                                viewModel.insert(user);
                                popBackStack();
                            }else{
                                loadDialog.dismiss();
                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                        .setTipWord("注册失败")
                                        .create(true);
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,2000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        loadDialog.dismiss();
                        QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                .setTipWord("注册失败")
                                .create(true);
                        tipDialog.show();
                        new Handler().postDelayed(tipDialog::dismiss,2000);
                    }
                });
            }
        });
        (getCode = root.findViewById(R.id.sign_up_getCode)).setOnClickListener(v -> {
            if (validAccount()&&viewModel.loadUser(account.getText().toString()) == null) {
                getCode.setClickable(false);
                getCode.setTextColor(getResources().getColor(R.color.text_second_level));
                countDownTimer.start();
                Call<String> call=viewModel.getHttpApi().getIdenCode();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            if (jsonObject.getString("msg").equals("执行成功")) {
                                msgCode.setText(String.valueOf(jsonObject.getInt("result")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                .setTipWord("验证码获取失败")
                                .create(true);
                        tipDialog.show();
                        new Handler().postDelayed(tipDialog::dismiss,1000);
                    }
                });
            }
        });
    }

    private CountDownTimer countDownTimer=new CountDownTimer(60*1000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            long time=millisUntilFinished/1000;
            getCode.setText(String.valueOf(time+"S"));
        }

        @Override
        public void onFinish() {
            getCode.setText(R.string.get_msg_code);
            getCode.setClickable(true);
            getCode.setTextColor(getResources().getColor(R.color.black));
        }
    };

    private void showError(TextInputLayout textInputLayout, MaterialEditText editText, String error){
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(error);
        editText.requestFocus();
    }

    private boolean validInput(){
        if (!validAccount())
            return false;
        if (msgCode.getText().length()!=5){
            showError(codeInputLayout,msgCode,"验证码有误");
            return false;
        }
        if (password.getText().length()==0){
            showError(passwordInputLayout,password,"密码不能为空");
            return false;
        }
        if (!checkPwd.getText().toString().equals(password.getText().toString())){
            showError(checkPwdInputLayout,checkPwd,"密码不一致");
            return false;
        }
        return true;
    }

    private boolean validAccount(){
        if (account.getText().length()!=11) {
            showError(accountInputLayout,account,"请输入正确的手机号码");
            return false;
        }
        return true;
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            accountInputLayout.setErrorEnabled(false);
            passwordInputLayout.setErrorEnabled(false);
            checkPwdInputLayout.setErrorEnabled(false);
            codeInputLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
        getBaseFragmentActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onDestroy();
    }
}
