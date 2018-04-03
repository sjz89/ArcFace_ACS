package com.daylight.arcface_acs.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.bean.Record;
import com.daylight.arcface_acs.bean.User;

import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 *
 * Created by Daylight on 2018/1/26.
 */
public class UserRepository {
    private UserDao userDao;
    private RecordDao recordDao;
    private ChatDao chatDao;
    private RecentDao recentDao;
    public UserRepository(Application application){
        UserDataBase dataBase=UserDataBase.getDataBase(application);
        userDao=dataBase.userDao();
        recordDao=dataBase.recordDao();
        chatDao=dataBase.chatDao();
        recentDao=dataBase.recentDao();
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

    public List<User> getNeighbors(User user){
        try {
            return new getNeighborAsyncTask(userDao).execute(user).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Record> getRecords(String account){
        try {
            return new loadRecordsAsyncTask(recordDao).execute(account).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<ChatMessage>> getMessages(String rightId,String leftId){
        return chatDao.queryMessages(rightId,leftId);
    }

    public LiveData<List<Recent>> getRecentList(String account){
        return recentDao.getRecentList(account);
    }

    public void insertRecent(Recent recent){
        new insertRecentAsyncTask(recentDao).execute(recent);
    }

    public void insertMessage(ChatMessage message){
        new insertMsgAsyncTask(chatDao).execute(message);
    }

    public void insertRecord(Record record){
        new insertRecordAsyncTask(recordDao).execute(record);
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

    private static class loadRecordsAsyncTask extends AsyncTask<String,Void,List<Record>>{
        private RecordDao mAsyncTaskDao;
        loadRecordsAsyncTask(RecordDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected List<Record> doInBackground(String... strings) {
            return mAsyncTaskDao.getRecords(strings[0]);
        }
    }

    private static class insertRecordAsyncTask extends AsyncTask<Record,Void,Void>{
        private RecordDao mAsyncTaskDao;
        insertRecordAsyncTask(RecordDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected Void doInBackground(Record... records) {
            mAsyncTaskDao.insert(records[0]);
            return null;
        }
    }

    private static class getNeighborAsyncTask extends AsyncTask<User,Void,List<User>>{

        private UserDao mAsyncTaskDao;
        getNeighborAsyncTask(UserDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected List<User> doInBackground(User... users) {
            return mAsyncTaskDao.getNeighbors(users[0].getCommunityName(),users[0].getBuildingName(),users[0].getName());
        }
    }

    private static class insertMsgAsyncTask extends AsyncTask<ChatMessage,Void,Void>{
        private ChatDao mAsyncTaskDao;
        insertMsgAsyncTask(ChatDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected Void doInBackground(ChatMessage... messages) {
            mAsyncTaskDao.insert(messages[0]);
            return null;
        }
    }

    private static class insertRecentAsyncTask extends AsyncTask<Recent,Void,Void>{
        private RecentDao mAsyncTaskDao;
        insertRecentAsyncTask(RecentDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected Void doInBackground(Recent... recents) {
            mAsyncTaskDao.insert(recents[0]);
            return null;
        }
    }
}
