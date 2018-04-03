package com.daylight.arcface_acs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.bean.Record;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.database.UserRepository;
import com.daylight.arcface_acs.http.HttpApi;
import com.daylight.arcface_acs.http.RetrofitInstance;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 *
 * Created by Daylight on 2018/1/26.
 */

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<User> user;
    private LiveData<List<String>> accounts;
    private LiveData<List<ChatMessage>> messages;
    private LiveData<List<Recent>> recent;
    private Retrofit retrofit;
    private User neighbor;

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

    public void setNeighbor(User neighbor) {
        this.neighbor = neighbor;
        if (user.getValue()!=null)
            messages=userRepository.getMessages(user.getValue().getPhoneNum(),neighbor.getPhoneNum());
    }

    public User getNeighbor() {
        return neighbor;
    }

    public LiveData<List<ChatMessage>> getMessages(){
        return messages;
    }

    public void setRecent(){
        if (user.getValue()!=null)
            recent=userRepository.getRecentList(user.getValue().getPhoneNum());
    }

    public LiveData<List<Recent>> getRecentList(){
        return recent;
    }

    public void insertRecent(Recent recent){
        userRepository.insertRecent(recent);
    }

    public void insertMessage(ChatMessage message){
        userRepository.insertMessage(message);
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

    public List<User> getNeighbors(User user){
        return userRepository.getNeighbors(user);
    }

    public void updateRecords(){
        getHttpApi().getRecord().enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                try {
                    JSONArray jsonArray=new JSONArray(response.body());
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        Record record=new Record();
                        record.setAccount(jsonObject.getString("phone"));
                        record.setTime(jsonObject.getLong("time"));
                        record.setRid(jsonObject.getInt("rid"));
                        record.setText(jsonObject.getString("ownName"));
                        record.setSubText(jsonObject.getString("building"));
                        userRepository.insertRecord(record);
                    }
                    RxBusHelper.post(Values.RECORD_UPDATE_DONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                RxBusHelper.post(Values.REQUEST_FAIL);
            }
        });
    }

    public List<Record> getRecords(String account){
        return userRepository.getRecords(account);
    }
}
