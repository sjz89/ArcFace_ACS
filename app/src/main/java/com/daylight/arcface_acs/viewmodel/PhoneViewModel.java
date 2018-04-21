package com.daylight.arcface_acs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.daylight.arcface_acs.bean.CommonData;

public class PhoneViewModel extends AndroidViewModel {
    private CommonData data;
    public PhoneViewModel(@NonNull Application application) {
        super(application);
    }

    public void setData(CommonData data){
        this.data=data;
    }

    public CommonData getData() {
        return data;
    }
}
