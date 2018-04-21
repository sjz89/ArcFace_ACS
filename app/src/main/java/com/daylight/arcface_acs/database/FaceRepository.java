package com.daylight.arcface_acs.database;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FaceRepository {
    private FaceDao faceDao;
    private FeatureDao featureDao;
    private LiveData<List<Face>> mAllFaces;

    public FaceRepository(Application application) {
        UserDataBase dataBase = UserDataBase.getDataBase(application);
        faceDao = dataBase.faceDao();
        featureDao = dataBase.featureDao();
    }

    public void setAccount(String account) {
        mAllFaces = faceDao.getAllFacesOrderById(account);
    }

    public LiveData<List<Face>> getAllFaces() {
        return mAllFaces;
    }

    public void insertFeature(Feature feature) {
        new featureInsert(featureDao).execute(feature);
    }

    public void deleteFeature(Feature feature){
        new featureDelete(featureDao).execute(feature);
    }

    public void delete(Face face) {
        new deleteAsyncTask(faceDao,featureDao).execute(face);
    }

    public void insert(Face face) {
        new insertAsyncTask(faceDao).execute(face);
    }

    public void update(Face face) {
        new updateAsyncTask(faceDao).execute(face);
    }

    public Face query(String account){
        try {
            return new queryAsyncTask(faceDao).execute(account).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Face> getFaces(){
        try {
            return new loadFacesAsyncTask(faceDao).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Feature> getFeatures(Long faceId){
        try {
            return new featureQuery(featureDao).execute(faceId).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class featureInsert extends AsyncTask<Feature, Void, Void> {

        private FeatureDao mAsyncTaskDao;

        featureInsert(FeatureDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Feature... features) {
            mAsyncTaskDao.insert(features[0]);
            return null;
        }
    }

    private static class featureDelete extends AsyncTask<Feature, Void, Void> {

        private FeatureDao featureDao;

        featureDelete(FeatureDao dao) {
            featureDao=dao;
        }

        @Override
        protected Void doInBackground(Feature... features) {
            featureDao.delete(features[0]);
            return null;
        }
    }

    private static class featureQuery extends AsyncTask<Long,Void,List<Feature>>{

        private FeatureDao mAsyncTaskDao;
        featureQuery(FeatureDao dao){
            mAsyncTaskDao=dao;
        }
        @Override
        protected List<Feature> doInBackground(Long... longs) {
            return mAsyncTaskDao.getFeatures(longs[0]);
        }
    }

    private static class queryAsyncTask extends AsyncTask<String,Void,Face>{

        private FaceDao mAsyncTaskDao;
        queryAsyncTask(FaceDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected Face doInBackground(String... strings) {
            return mAsyncTaskDao.queryFace(strings[0]);
        }
    }

    private static class insertAsyncTask extends AsyncTask<Face, Void, Void> {

        private FaceDao mAsyncTaskDao;

        insertAsyncTask(FaceDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Face... faces) {
            mAsyncTaskDao.insertFaces(faces[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Face, Void, Void> {

        private FaceDao faceDao;
        private FeatureDao featureDao;

        deleteAsyncTask(FaceDao dao1,FeatureDao dao2) {
            faceDao = dao1;
            featureDao=dao2;
        }

        @Override
        protected Void doInBackground(final Face... faces) {
            featureDao.deleteFeatures(faces[0].getId());
            faceDao.deleteFaces(faces[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Face, Void, Void> {

        private FaceDao mAsyncTaskDao;

        updateAsyncTask(FaceDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Face... faces) {
            mAsyncTaskDao.updateFaces(faces[0]);
            return null;
        }
    }

    private static class loadFacesAsyncTask extends AsyncTask<Void,Void,List<Face>>{
        private FaceDao mAsyncTaskDao;
        loadFacesAsyncTask(FaceDao dao){
            mAsyncTaskDao=dao;
        }

        @Override
        protected List<Face> doInBackground(Void... voids) {
            return mAsyncTaskDao.getFaces();
        }
    }
}
