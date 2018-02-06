package com.daylight.arcface_acs.http;

import android.content.Context;

import com.daylight.arcface_acs.Values;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 *
 * Created by Daylight on 2018/2/3.
 */

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    public static Retrofit getInstance(Context context){
        if (retrofit==null){
            synchronized (RetrofitInstance.class){
                if (retrofit==null){
                    ClearableCookieJar cookieJar =
                            new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
                    okHttpClient = new OkHttpClient.Builder()
                            .cookieJar(cookieJar)
                            .build();
                    retrofit=new Retrofit.Builder()
                            .client(okHttpClient)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .baseUrl(Values.ServiceUrl)
                            .build();
                }
            }
        }
        return retrofit;
    }
    public static CookieJar getCookie(){
        return okHttpClient.cookieJar();
    }
}
