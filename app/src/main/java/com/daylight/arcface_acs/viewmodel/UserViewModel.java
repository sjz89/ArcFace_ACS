package com.daylight.arcface_acs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.database.UserRepository;
import com.daylight.arcface_acs.http.HttpApi;
import com.daylight.arcface_acs.http.RetrofitInstance;

import java.util.List;

import retrofit2.Retrofit;

/**
 *
 * Created by Daylight on 2018/1/26.
 */

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<User> user;
    private LiveData<List<String>> accounts;
    private Retrofit retrofit;
    public UserViewModel(Application application){
        super(application);
        userRepository=new UserRepository(application);
        accounts =userRepository.getAccounts();
        retrofit= RetrofitInstance.getInstance(application);
    }

    public HttpApi getHttpApi(){
        return retrofit.create(HttpApi.class);
    }

    public LiveData<List<String>> getAccounts(){
        return accounts;
    }

    public LiveData<User> getUser(){
        return user;
    }

    public void setUser(String account){
        if (this.user!=null)
            return;
        user=userRepository.getUser(account);
    }
    public void update(User user){
        userRepository.update(user);
    }
    public void insert(User user){
        userRepository.insert(user);
    }
    public User loadUser(String account){
        return userRepository.loadUser(account);
    }
}
