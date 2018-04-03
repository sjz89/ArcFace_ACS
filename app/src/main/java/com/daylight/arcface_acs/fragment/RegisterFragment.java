package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.Face;
import com.daylight.arcface_acs.bean.Feature;
import com.daylight.arcface_acs.dialog.AddFaceDialogBuilder;
import com.daylight.arcface_acs.dialog.NewFaceDialogBuilder;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.ImageUtil;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends QMUIFragment {
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;

    private View root;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private AFR_FSDKFace mAFR_FSDKFace;
    private FaceViewModel viewModel;
    private Face face;

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        root = LayoutInflater.from(getContext()).inflate(R.layout.fragment_register,null);
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        face=viewModel.getFace();
        initTopBar();
        initView();
        initData();
        return root;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar = root.findViewById(R.id.topbar_register);
        topBar.setTitle("添加人脸");
        topBar.addLeftBackImageButton().setOnClickListener(view -> popBackStack());
    }

    private void initView(){
        SurfaceView mSurfaceView = root.findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mSurfaceHolder = null;
            }
        });
    }
    private void initData(){
        String path = viewModel.getPath();
        mBitmap= ImageUtil.decodeImage(path);
        if (mBitmap != null) {
            src.set(0,0,mBitmap.getWidth(),mBitmap.getHeight());
        }
        QMUITipDialog loading=new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("识别中...")
                .create();
        loading.show();
        Thread thread = new Thread(() -> {
            while (mSurfaceHolder == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            byte[] data = ImageUtil.getNV21(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap);

            AFD_FSDKEngine engine = new AFD_FSDKEngine();
            List<AFD_FSDKFace> result = new ArrayList<>();
            AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(Values.appid, Values.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
            if (err.getCode() != AFD_FSDKError.MOK) {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_FD_ERROR;
                reg.arg2 = err.getCode();
                handler.sendMessage(reg);
            }
            engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
            while (mSurfaceHolder != null) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                if (canvas != null) {
                    Paint mPaint = new Paint();
                    boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height();
                    float scale;
                    if (fit_horizontal) {
                        scale = canvas.getWidth() / (float) src.width();
                        dst.left = 0;
                        dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                        dst.right = dst.left + canvas.getWidth();
                        dst.bottom = dst.top + (int) (src.height() * scale);
                    } else {
                        scale = canvas.getHeight() / (float) src.height();
                        dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                        dst.top = 0;
                        dst.right = dst.left + (int) (src.width() * scale);
                        dst.bottom = dst.top + canvas.getHeight();
                    }
                    canvas.drawBitmap(mBitmap, src, dst, mPaint);
                    canvas.save();
                    canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
                    canvas.translate(dst.left / scale, dst.top / scale);
                    for (AFD_FSDKFace face : result) {
                        mPaint.setColor(Color.GREEN);
                        mPaint.setStrokeWidth(8.0f);
                        mPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(face.getRect(), mPaint);
                    }
                    canvas.restore();
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    break;
                }
            }

            if (!result.isEmpty()) {
                AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                AFR_FSDKFace result1 = new AFR_FSDKFace();
                AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(Values.appid, Values.fr_key);
                if (error1.getCode() != AFD_FSDKError.MOK) {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_FR_ERROR;
                    reg.arg2 = error1.getCode();
                    handler.sendMessage(reg);
                }
                error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                if (error1.getCode() == AFR_FSDKError.MOK) {
                    mAFR_FSDKFace = result1.clone();
                    int width = result.get(0).getRect().width();
                    int height = result.get(0).getRect().height();
                    Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    Canvas face_canvas = new Canvas(face_bitmap);
                    face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);

                    byte[] imageDate=ImageUtil.Bitmap2Bytes(face_bitmap);

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageDate);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", "", requestFile);
                    RequestBody mid = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(viewModel.getMid()));

                    Call<String> uploadImage=viewModel.getHttpApi().uploadImage(mid,body);
                    uploadImage.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response.body());
                                if (jsonObject.getBoolean("flag")){
                                    loading.dismiss();
                                    Feature feature=new Feature();
                                    feature.setFaceId(face.getId());
                                    feature.setId(Long.parseLong(jsonObject.getString("result")));
                                    feature.setImageData(imageDate);
                                    feature.setFeatureData(mAFR_FSDKFace.getFeatureData());
                                    Message reg = Message.obtain();
                                    reg.what = MSG_CODE;
                                    reg.arg1 = MSG_EVENT_REG;
                                    reg.obj = feature;
                                    handler.sendMessage(reg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                            loading.dismiss();
//                            Feature feature=new Feature();
//                            feature.setFaceId(face.getId());
//                            feature.setImageData(imageDate);
//                            feature.setFeatureData(mAFR_FSDKFace.getFeatureData());
//                            Message reg = Message.obtain();
//                            reg.what = MSG_CODE;
//                            reg.arg1 = MSG_EVENT_REG;
//                            reg.obj = feature;
//                            handler.sendMessage(reg);
                        }
                    });
                    face_bitmap.recycle();
                } else {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_NO_FEATURE;
                    handler.sendMessage(reg);
                }
                engine1.AFR_FSDK_UninitialEngine();
            } else {
                Message reg = Message.obtain();
                reg.what = MSG_CODE;
                reg.arg1 = MSG_EVENT_NO_FACE;
                handler.sendMessage(reg);
            }
            engine.AFD_FSDK_UninitialFaceEngine();
            mBitmap.recycle();
        });
        thread.start();
    }

    private Handler handler=new Handler(msg -> {
        if (msg.what == MSG_CODE) {
            if (msg.arg1 == MSG_EVENT_REG) {
                Feature feature=(Feature)msg.obj;
                if (!viewModel.isNew()) {
                    new AddFaceDialogBuilder(getContext())
                            .setImage(feature.getImageData())
                            .setTitle("识别到人脸")
                            .addAction(new QMUIDialogAction(getContext(),"取消",((dialog, index) -> {
                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                        .setTipWord("取消添加").create();
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,1000);
                                dialog.dismiss();
                                //todo 删除照片
                                viewModel.delete(face);
                                popBackStack();
                            })))
                            .addAction(new QMUIDialogAction(getContext(),"确定",((dialog, index) -> {
                                viewModel.insertFeature(feature);
                                if (viewModel.isMe()){
                                    face.setFaceData(feature.getImageData());
                                    viewModel.update(face);
                                    viewModel.setMe(false);
                                }
                                RxBusHelper.post(feature);
                                QMUITipDialog tipDialog= new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                        .setTipWord("添加成功").create(true);
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,1000);
                                dialog.dismiss();
                                popBackStack();
                            }))).show();
                }else{
                    NewFaceDialogBuilder dialogBuilder=new NewFaceDialogBuilder(getContext());
                    dialogBuilder.setImage(feature.getImageData()).setTitle("添加新成员")
                            .addAction(new QMUIDialogAction(getContext(),"取消",((dialog1, index) -> {
                                //todo 删除照片
                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                        .setTipWord("取消添加").create();
                                tipDialog.show();
                                new Handler().postDelayed(tipDialog::dismiss,1000);
                                dialog1.dismiss();
                                popBackStack();
                            })))
                            .addAction(new QMUIDialogAction(getContext(),"确定",((dialog1, index) -> {
                                viewModel.setFeature(feature);
                                face.setName(dialogBuilder.getName());
                                face.setFaceData(feature.getImageData());
                                face.setIdNum(dialogBuilder.getIdNum());
                                face.setType(dialogBuilder.getType());
                                face.setStartDate(dialogBuilder.getStartTime());
                                face.setEndDate(dialogBuilder.getEndTime());
                                int type=100;
                                if (face.getType().equals("访客"))
                                    type=200;
                                viewModel.getHttpApi().faceRegister(SharedPreferencesUtil.getAccount(getBaseFragmentActivity()),
                                        face.getName(),type,face.getIdNum(),/*face.getStartDate(),face.getEndDate(),*/new int[]{0})
                                        .enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                                try {
                                                    JSONObject jsonObject=new JSONObject(response.body());
                                                    if(jsonObject.getBoolean("flag")){
                                                        QMUITipDialog tipDialog=new QMUITipDialog.Builder(getBaseFragmentActivity())
                                                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                                                .setTipWord("成员注册成功")
                                                                .create(true);
                                                        tipDialog.show();
                                                        new Handler().postDelayed(tipDialog::dismiss,1000);
                                                    }
                                                } catch (JSONException | NullPointerException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getBaseFragmentActivity())
                                                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                                                        .setTipWord("成员注册失败")
                                                        .create(true);
                                                tipDialog.show();
                                                new Handler().postDelayed(tipDialog::dismiss,1000);
                                            }
                                        });
                                viewModel.insert(face);
                                dialog1.dismiss();
                                popBackStack();
                            }))).show();
                }
            }else if(msg.arg1 == MSG_EVENT_NO_FEATURE ){
                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                        .setTipWord("人脸特征无法检测，请换一张图片").create(true);
                tipDialog.show();
                new Handler().postDelayed(tipDialog::dismiss,1000);
            } else if(msg.arg1 == MSG_EVENT_NO_FACE ){
                QMUITipDialog tipDialog=new QMUITipDialog.Builder(getContext()).setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                        .setTipWord("没有检测到人脸，请换一张图片").create(true);
                tipDialog.show();
                new Handler().postDelayed(tipDialog::dismiss,1000);
            } else if(msg.arg1 == MSG_EVENT_FD_ERROR ){
                Toast.makeText(getContext(), "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
            } else if(msg.arg1 == MSG_EVENT_FR_ERROR){
                Toast.makeText(getContext(), "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    });

    @Override
    protected void popBackStack() {
        super.popBackStack();
    }
}
