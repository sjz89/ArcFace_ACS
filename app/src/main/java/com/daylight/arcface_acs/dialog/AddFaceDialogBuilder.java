package com.daylight.arcface_acs.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.R;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

public class AddFaceDialogBuilder extends QMUIDialog.CustomDialogBuilder {
    private Context context;
    private byte[] imageData;

    public AddFaceDialogBuilder(Context context) {
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.dialog_add, parent, false);
        parent.addView(view);
        ImageView imageView = view.findViewById(R.id.add_face);
        GlideApp.with(context).asBitmap().load(imageData).into(imageView);
    }
    public AddFaceDialogBuilder setImage(byte[] imageData){
        this.imageData =imageData;
        return this;
    }
}
