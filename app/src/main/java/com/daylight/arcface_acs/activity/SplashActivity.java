package com.daylight.arcface_acs.activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.utils.EncryptUtil;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    public static int PERMISSION_REQ = 0x123456;

    private String[] mPermission = new String[] {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<String> mRequestPermission = new ArrayList<>();
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ImageView imageView=findViewById(R.id.splash_View);
        GlideApp.with(this).load(R.drawable.splash).centerCrop().into(imageView);
        imageView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.alpha_splash));
        SQLiteStudioService.instance().start(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (String one : mPermission) {
                if (PackageManager.PERMISSION_GRANTED != this.checkPermission(one, Process.myPid(), Process.myUid())) {
                    mRequestPermission.add(one);
                }
            }
            if (!mRequestPermission.isEmpty()) {
                this.requestPermissions(mRequestPermission.toArray(new String[mRequestPermission.size()]), PERMISSION_REQ);
                return ;
            }
        }
        startActivity();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 版本兼容
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        if (requestCode == PERMISSION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                for (String one : mPermission) {
                    if (permissions[i].equals(one) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        mRequestPermission.remove(one);
                    }
                }
            }
            startActivity();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQ) {
            if (resultCode == 0) {
                this.finish();
            }
        }
    }
    private void startActivity(){
        viewModel= ViewModelProviders.of(this).get(UserViewModel.class);
        User user=viewModel.loadUser(SharedPreferencesUtil.getAccount(this));
        new Handler().postDelayed(() -> {
            if (user==null){
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            } else{
                if (user.getPassword().equals(EncryptUtil.shaEncrypt(SharedPreferencesUtil.getPassword(SplashActivity.this)))) {
                    Call<String> login = viewModel.getHttpApi().login(user.getPhoneNum(),
                            user.getPassword(),true);
                    login.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.getBoolean("flag")) {
                                    //todo 获取账号信息
                                    Call<String> getUserInfo=viewModel.getHttpApi().getUserInfo();
                                    getUserInfo.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                            try {
                                                JSONObject jsonObject1=new JSONObject(response.body());
                                                user.setName(jsonObject1.getString("name"));
                                                user.setCommunityName(jsonObject1.getString("communityName"));
                                                user.setBuildingName(jsonObject1.getString("buildingName"));
                                                user.setDoorNum(jsonObject1.getString("num"));
                                                user.setIdNum(jsonObject1.getString("idnumber"));
                                                user.setStatus(jsonObject1.getInt("status"));
                                                viewModel.update(user);
                                                viewModel.updateRecords();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                                        }
                                    });
                                    if (user.isHasPatternLock()) {
                                        Intent intent=new Intent(SplashActivity.this,LockActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    onFailedShow();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            onFailedShow();
                        }
                    });
                }else{
                    onFailedShow();
                }
            }
        },1500);
    }
    private void onFailedShow(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
