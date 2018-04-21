package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.view.InfoItemView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

public class SecurityFragment extends BaseFragment {
    private View view;
    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_security,null);
        initTopBar();
        initRecyclerView();
        return view;
    }
    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_security);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(R.string.security);
    }
    private void initRecyclerView() {
        InfoItemView highLevel =view.findViewById(R.id.high_security);
        highLevel.setText("高级别");
        highLevel.setDetailText("进门验证，出门验证，不允许随行人员");
        highLevel.setOrientation(QMUICommonListItemView.VERTICAL);
        highLevel.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        highLevel.setImageDrawable(R.drawable.ic_shield_on_blue,36,36);

        InfoItemView middleLevel =view.findViewById(R.id.middle_security);
        middleLevel.setText("中级别");
        middleLevel.setDetailText("进门备份，出门验证，允许随行人员");
        middleLevel.setOrientation(QMUICommonListItemView.VERTICAL);
        middleLevel.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        middleLevel.setImageDrawable(R.drawable.ic_shield_off_blue,36,36);

        if (SharedPreferencesUtil.getLevel(getBaseFragmentActivity()) == 0) {
            highLevel.getSwitch().setChecked(false);
            middleLevel.getSwitch().setChecked(true);
        } else {
            middleLevel.getSwitch().setChecked(false);
            highLevel.getSwitch().setChecked(true);
        }

        View.OnClickListener listener = v -> {
            if (v instanceof QMUICommonListItemView) {
                String text = ((QMUICommonListItemView) v).getText().toString();
                switch (text) {
                    case "高级别":
                        middleLevel.getSwitch().setChecked(false);
                        highLevel.getSwitch().setChecked(true);
                        break;
                    case "中级别":
                        highLevel.getSwitch().setChecked(false);
                        middleLevel.getSwitch().setChecked(true);
                        break;
                }
            }
        };

        highLevel.setOnClickListener(listener);
        middleLevel.setOnClickListener(listener);
    }
}
