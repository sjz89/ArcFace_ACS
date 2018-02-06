package com.daylight.arcface_acs.dialog;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.daylight.arcface_acs.R;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;


public class ChangePasswordDialogBuilder extends QMUIDialog.CustomDialogBuilder {
    private Context context;
    private View view;
    private EditText password;
    private EditText checkPassword;
    private TextInputLayout pwdInputLayout;
    private TextInputLayout checkPwdInputLayout;
    public ChangePasswordDialogBuilder(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent) {
        view= LayoutInflater.from(context).inflate(R.layout.dialog_change_pwd,parent);
        initView();
    }

    private void initView(){
        password=view.findViewById(R.id.dialog_password);
        checkPassword=view.findViewById(R.id.dialog_check_password);
        pwdInputLayout=view.findViewById(R.id.pwd_input_layout);
        checkPwdInputLayout=view.findViewById(R.id.check_pwd_input_layout);
        password.setHintTextColor(QMUIResHelper.getAttrColor(mContext, com.qmuiteam.qmui.R.attr.qmui_config_color_gray_3));
        password.setTextColor(QMUIResHelper.getAttrColor(mContext, com.qmuiteam.qmui.R.attr.qmui_config_color_black));
        password.setTextSize(TypedValue.COMPLEX_UNIT_PX, QMUIResHelper.getAttrDimen(mContext, com.qmuiteam.qmui.R.attr.qmui_dialog_content_message_text_size));
        password.setFocusable(true);
        password.setFocusableInTouchMode(true);
        password.setImeOptions(EditorInfo.IME_ACTION_GO);
        password.setGravity(Gravity.CENTER_VERTICAL);
        checkPassword.setHintTextColor(QMUIResHelper.getAttrColor(mContext, com.qmuiteam.qmui.R.attr.qmui_config_color_gray_3));
        checkPassword.setTextColor(QMUIResHelper.getAttrColor(mContext, com.qmuiteam.qmui.R.attr.qmui_config_color_black));
        checkPassword.setTextSize(TypedValue.COMPLEX_UNIT_PX, QMUIResHelper.getAttrDimen(mContext, com.qmuiteam.qmui.R.attr.qmui_dialog_content_message_text_size));
        checkPassword.setFocusable(true);
        checkPassword.setFocusableInTouchMode(true);
        checkPassword.setImeOptions(EditorInfo.IME_ACTION_GO);
        checkPassword.setGravity(Gravity.CENTER_VERTICAL);
        password.addTextChangedListener(textWatcher);
        checkPassword.addTextChangedListener(textWatcher);
    }
    public String getPassword(){
        return password.getText().toString();
    }

    private void showError(TextInputLayout textInputLayout,EditText editText,String error){
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(error);
        editText.requestFocus();
    }

    public boolean validPassword(){
        if (password.getText().toString().equals("")){
            showError(pwdInputLayout,password,"密码不能为空");
            return false;
        }
        if (!password.getText().toString().equals(checkPassword.getText().toString())){
            showError(checkPwdInputLayout,checkPassword,"密码不一致");
            return false;
        }
        return true;
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            pwdInputLayout.setErrorEnabled(false);
            checkPwdInputLayout.setErrorEnabled(false);
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
