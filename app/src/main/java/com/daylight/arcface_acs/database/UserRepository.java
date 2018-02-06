package com.daylight.arcface_acs.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.daylight.arcface_acs.bean.Building;
import com.daylight.arcface_acs.bean.Estate;
import com.daylight.arcface_acs.bean.User;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 *
 * Created by Daylight on 2018/1/26.
 */
public class UserRepository {
    private UserDao userDao;
    public UserRepository(Application application){
        UserDataBase dataBase=UserDataBase.getDataBase(application);
        userDao=dataBase.userDao();
    }

    public LiveData<List<String>> getAccounts(){
        return userDao.getAccounts();
    }

    public LiveData<User> getUser(String account){
        return userDao.getUser(account);
    }

    public User loadUser(String account) {
        try {
            return new loadAsyncTask(userDao).execute(account).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(User user){
        new insertAsyncTask(userDao).execute(user);
    }

    public void update(User user){
        new updateAsyncTask(userDao).execute(user);
    }

    private static class loadAsyncTask extends AsyncTask<String,Void,User>{

        private UserDao mAsyncTaskDao;
        loadAsyncTask(UserDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected User doInBackground(final String... strings) {
            return mAsyncTaskDao.loadUser(strings[0]);
        }
    }

    private static class updateAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        updateAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.updateUsers(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.insertUsers(params[0]);
            return null;
        }
    }
}
