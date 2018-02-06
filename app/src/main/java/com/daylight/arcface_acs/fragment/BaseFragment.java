package com.daylight.arcface_acs.fragment;


import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

/**
 *
 * Created by Daylight on 2018/1/21.
 */

public abstract class BaseFragment extends QMUIFragment {
    public BaseFragment(){
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }

}
