package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.view.InfoItemView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

/**
 * 物业服务
 * Created by Daylight on 2018/3/18.
 */

public class PropertyFragment extends BaseFragment {
    private View view;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_property,null);
        initTopBar();
        initGroupListView();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_property);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.property);
    }

    private void initGroupListView(){
        GroupListView groupListView=view.findViewById(R.id.groupList_property);
        QMUICommonListItemView call=new InfoItemView(getContext(),null,"呼叫物业",null,
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        ((InfoItemView)call).setImageDrawable(R.drawable.ic_call_blue,36,36);

        QMUICommonListItemView payment=new InfoItemView(getContext(),null,"物业缴费",null,
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        ((InfoItemView)payment).setImageDrawable(R.drawable.ic_pay,36,36);

        QMUICommonListItemView waterSending=new InfoItemView(getContext(),null,"送水上门",null,
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        ((InfoItemView)waterSending).setImageDrawable(R.drawable.ic_water3,36,36);

        QMUICommonListItemView feedback=new InfoItemView(getContext(),null,"意见反馈",null,
                QMUICommonListItemView.HORIZONTAL,QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON );
        ((InfoItemView)feedback).setImageDrawable(R.drawable.ic_feedback,36,36);

        View.OnClickListener listener=v -> {
            if (v instanceof QMUICommonListItemView){
                switch (((QMUICommonListItemView) v).getText().toString()){
                    case "呼叫物业":
                        startFragment(new ContactsFragment());
                        break;
                }
            }
        };
        GroupListView.newSection(getContext())
                .addItemView(call,listener)
                .addItemView(payment,listener)
                .addItemView(waterSending,listener)
                .addItemView(feedback,listener)
                .addTo(groupListView);
    }
}
