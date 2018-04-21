package com.daylight.arcface_acs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

/**
 *
 * Created by Daylight on 2018/1/21.
 */

@SuppressWarnings("SameParameterValue")
public class InfoItemView extends QMUICommonListItemView {
    public InfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoItemView(Context context) {
        super(context);
    }

    public InfoItemView(Context context,byte[] drawable,CharSequence titleText, String detailText, int orientation, int accessoryType){
        super(context);
        int height;
        if (orientation == QMUICommonListItemView.VERTICAL) {
            height = QMUIResHelper.getAttrDimen(getContext(), com.qmuiteam.qmui.R.attr.qmui_list_item_height_higher);
        } else {
            height = QMUIResHelper.getAttrDimen(getContext(), com.qmuiteam.qmui.R.attr.qmui_list_item_height);
        }
        setOrientation(orientation);
        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        if (drawable==null)
            super.setImageDrawable(null);
        else
            setImageDrawable(drawable);
        setText(titleText);
        setDetailText(detailText);
        setAccessoryType(accessoryType);
    }

    public InfoItemView(Context context,byte[] drawable){
        super(context);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mImageView.setLayoutParams(layoutParams);

        setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (drawable==null)
            super.setImageDrawable(null);
        else
            setImageDrawable(drawable);
        setText(null);
        setDetailText(null);
        setAccessoryType(ACCESSORY_TYPE_NONE);
    }

    public void setImageDrawable(byte[] imagePath){
        GlideApp.with(this).asBitmap().load(imagePath).override(240,240).into(mImageView);
        mImageView.setVisibility(VISIBLE);
    }

    public void setImageDrawable(byte[] imagePath,int width,int height){
        GlideApp.with(this).asBitmap().load(imagePath).override(QMUIDisplayHelper.dpToPx(width),
                QMUIDisplayHelper.dpToPx(height)).into(mImageView);
        mImageView.setVisibility(VISIBLE);
    }

    public void setImageDrawable(String imagePath,int width,int height){
        GlideApp.with(this).asBitmap().load(imagePath).override(QMUIDisplayHelper.dpToPx(width),
                QMUIDisplayHelper.dpToPx(height)).into(mImageView);
        mImageView.setVisibility(VISIBLE);
    }

    public void setImageDrawable(int imagePath,int width,int height){
        GlideApp.with(this).asBitmap().load(imagePath).override(QMUIDisplayHelper.dpToPx(width),
                QMUIDisplayHelper.dpToPx(height)).into(mImageView);
        mImageView.setVisibility(VISIBLE);
    }
}
