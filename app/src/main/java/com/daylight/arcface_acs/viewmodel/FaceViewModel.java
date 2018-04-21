package com.daylight.arcface_acs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.database.FaceRepository;
import com.daylight.arcface_acs.http.HttpApi;
import com.daylight.arcface_acs.http.RetrofitInstance;

import java.util.List;

import retrofit2.Retrofit;


public class FaceViewModel extends AndroidViewModel {
    private FaceRepository mRepository;
    private LiveData<List<Face>> mAllFaces;
    private String path;
    private Face face;
    private Feature feature;
    private boolean isNew;
    private boolean isMe;
    private Retrofit retrofit;
    private int mid;
    public FaceViewModel(@NonNull Application application) {
        super(application);
        mRepository=new FaceRepository(application);
        retrofit= RetrofitInstance.getInstance(application);
    }
    public void setAccount(String account){
        mRepository.setAccount(account);
        mAllFaces=mRepository.getAllFaces();
    }

    public void setMe(boolean me) {
        isMe = me;
    }

    public boolean isMe() {
        return isMe;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public HttpApi getHttpApi(){
        return retrofit.create(HttpApi.class);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFace(Face face){
        this.face=face;
    }

    public Face getFace(){
        return face;
    }

    public Face loadFace(String account){
        return mRepository.query(account);
    }

    public LiveData<List<Face>> getAllFaces(){
        return mAllFaces;
    }

    public List<Face> getFaces(){
        return mRepository.getFaces();
    }

    public void insert(Face face){
        mRepository.insert(face);
    }
    public void delete(Face face) {
        mRepository.delete(face);
    }
    public void update(Face face) {
        mRepository.update(face);
    }
    public void insertFeature(Feature feature){
        mRepository.insertFeature(feature);
    }
    public void deleteFeature(Feature feature){
        mRepository.deleteFeature(feature);
    }
    public List<Feature> getFeatures(Long faceId){
        return mRepository.getFeatures(faceId);
    }
}
