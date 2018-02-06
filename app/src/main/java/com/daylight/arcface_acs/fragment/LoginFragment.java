package com.daylight.arcface_acs.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.EncryptUtil;
import com.daylight.arcface_acs.utils.JellyInterpolator;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.activity.MainActivity;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登陆界面
 * Created by Daylight on 2018/1/26.
 */

public class LoginFragment extends BaseFragment implements View.OnClickListener {
    private View root;
    private MaterialAutoCompleteTextView account;
    private MaterialEditText password;
    private TextInputLayout accountInputLayout;
    private TextInputLayout passwordInputLayout;
    private Button mBtnLogin;
    private View progress;
    private View mInputLayout;
    private LinearLayout mName, mPsw;
    private UserViewModel viewModel;
    private List<String> accounts;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accounts = new ArrayList<>();
        viewModel = ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        viewModel.getAccounts().observe(getBaseFragmentActivity(), stringList -> {
            accounts = stringList;
            arrayAdapter = new ArrayAdapter<>(getBaseFragmentActivity(), R.layout.item_menu, accounts);
            account.setAdapter(arrayAdapter);
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login, null);
        initView();
        return root;
    }

    private void initView() {
        mBtnLogin = root.findViewById(R.id.main_btn_login);

        progress = root.findViewById(R.id.layout_progress);
        mInputLayout = root.findViewById(R.id.input_layout);
        mName = root.findViewById(R.id.input_layout_name);
        mPsw = root.findViewById(R.id.input_layout_psw);
        account = root.findViewById(R.id.account);
        password = root.findViewById(R.id.password);
        account.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        accountInputLayout = root.findViewById(R.id.account_input_layout);
        passwordInputLayout = root.findViewById(R.id.password_input_layout);

        account.setText(SharedPreferencesUtil.getAccount(getContext()));
        password.setText(SharedPreferencesUtil.getPassword(getContext()));

        mBtnLogin.setOnClickListener(this);
        (root.findViewById(R.id.forget_password)).setOnClickListener(this);
        (root.findViewById(R.id.sign_up)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_login:
                if (mBtnLogin.getText().equals(getResources().getString(R.string.login))) {
                    if (validInput()) {
                        loginAnim();
                        new Handler().postDelayed(() -> {
                            Call<String> login=viewModel.getHttpApi().login(account.getText().toString(),
                                    EncryptUtil.shaEncrypt(password.getText().toString()),true);
                            login.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(response.body());
                                        if (jsonObject.getBoolean("flag")){
                                            //todo 获取账号信息
                                            Call<String> getUserInfo=viewModel.getHttpApi().getUserInfo();
                                            getUserInfo.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                    try {
                                                        JSONObject jsonObject1 = new JSONObject(response.body());
                                                        if (jsonObject1.getBoolean("flag")) {
                                                            User user = viewModel.loadUser(account.getText().toString());
                                                            if (user == null) {
                                                                user = new User();
                                                                user.setPhoneNum(account.getText().toString());
                                                                user.setPassword(EncryptUtil.shaEncrypt(password.getText().toString()));
                                                            }
                                                            user.setCommunityName(jsonObject1.getString("communityName"));
                                                            user.setBuildingName(jsonObject1.getString("buildingName"));
                                                            user.setDoorNum(jsonObject1.getString("num"));
                                                            user.setIdNum(jsonObject1.getString("idnumber"));
                                                            user.setStatus(jsonObject1.getInt("status"));
                                                            viewModel.insert(user);
                                                        }
                                                    } catch (JSONException e) {
                                                        User user = viewModel.loadUser(account.getText().toString());
                                                        if (user == null) {
                                                            user = new User();
                                                            user.setPhoneNum(account.getText().toString());
                                                            user.setPassword(EncryptUtil.shaEncrypt(password.getText().toString()));
                                                        }
                                                        viewModel.insert(user);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                                                }
                                            });
                                            SharedPreferencesUtil.setAccount(getContext(),account.getText().toString());
                                            SharedPreferencesUtil.setPassword(getContext(),password.getText().toString());
                                            Intent intent = new Intent(getBaseFragmentActivity(), MainActivity.class);
                                            startActivity(intent);
                                            getBaseFragmentActivity().finish();
                                        }else{
                                            recovery();
                                            mBtnLogin.setText(R.string.login);
                                            mBtnLogin.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha));
                                            QMUITipDialog tipDialog=new QMUITipDialog.Builder(getBaseFragmentActivity())
                                                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                                    .setTipWord("登陆失败")
                                                    .create(true);
                                            tipDialog.show();
                                            new Handler().postDelayed(tipDialog::dismiss,1000);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    recovery();
                                    mBtnLogin.setText(R.string.login);
                                    mBtnLogin.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha));
                                    QMUITipDialog tipDialog=new QMUITipDialog.Builder(getBaseFragmentActivity())
                                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                            .setTipWord("登陆失败")
                                            .create(true);
                                    tipDialog.show();
                                    new Handler().postDelayed(tipDialog::dismiss,1000);
                                }
                            });
                        }, 2000);
                    }
                } else if (mBtnLogin.getText().equals(getResources().getString(R.string.cancel))) {
                    recovery();
                    mBtnLogin.setText(R.string.login);
                    mBtnLogin.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha));
                }
                break;
            case R.id.sign_up:
                startFragment(new SignUpFragment());
                break;
        }
    }

    private void showError(TextInputLayout textInputLayout, EditText editText, String error) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(error);
        editText.requestFocus();
    }

    private boolean validInput() {
        if (account.getText().length() != 11) {
            showError(accountInputLayout, account, "请输入正确的账号");
            return false;
        }
        if (password.getText().length() == 0) {
            showError(passwordInputLayout, password, "请输入密码");
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
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 输入框的动画效果
     */
    private void inputAnimator(final View view, float w) {

        AnimatorSet set = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, w);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                    .getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            view.setLayoutParams(params);
        });

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout,
                "scaleX", 1f, 0.5f);
        set.setDuration(500);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.playTogether(animator, animator2);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /*
                  动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                mInputLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

    }

    /**
     * 出现进度动画
     */
    private void progressAnimator(final View view) {
        PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                0.5f, 1f);
        PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                0.5f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                animator, animator2);
        animator3.setDuration(1000);
        animator3.setInterpolator(new JellyInterpolator());
        animator3.start();

    }

    private void loginAnim() {
        // 计算出控件的高与宽
        float mWidth = mBtnLogin.getMeasuredWidth();
        // 隐藏输入框
        mName.setVisibility(View.INVISIBLE);
        mPsw.setVisibility(View.INVISIBLE);
        inputAnimator(mInputLayout, mWidth);
        mBtnLogin.setText(R.string.cancel);
        mBtnLogin.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.alpha));
    }

    /**
     * 恢复初始状态
     */
    private void recovery() {
        progress.setVisibility(View.GONE);
        mInputLayout.setVisibility(View.VISIBLE);
        mName.setVisibility(View.VISIBLE);
        mPsw.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mInputLayout.getLayoutParams();
        params.leftMargin = 0;
        params.rightMargin = 0;
        mInputLayout.setLayoutParams(params);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mInputLayout, "scaleX", 0.5f, 1f);
        animator2.setDuration(500);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());
        animator2.start();
    }

    @Override
    protected boolean canDragBack() {
        return false;
    }
}
