package com.daylight.arcface_acs.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bigkoo.pickerview.TimePickerView;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.view.GroupListView;
import com.daylight.arcface_acs.view.InfoItemView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NewFaceDialogBuilder extends QMUIDialog.CustomDialogBuilder {
    private Context context;
    private View view;
    private EditText editText;
    private byte[] imageData;
    private String[] types;
    private int choice;
    private String date;
    private QMUICommonListItemView validDate;
    private TimePickerView pickerView;
    private Calendar selectDate;

    public NewFaceDialogBuilder(Context context) {
        super(context);
        this.context = context;
        choice =0;
    }

    @Override
    protected void onCreateContent(QMUIDialog dialog, ViewGroup parent) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_new, parent, false);
        parent.addView(view);
        types=new String[]{"常驻","访客"};
        initCustomView();
        initGroupList();
    }

    private void initGroupList(){
        GroupListView listView=view.findViewById(R.id.new_groupList);
        InfoItemView headView=new InfoItemView(context, imageData);
        QMUICommonListItemView name=listView.createItemView("名字");
        name.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        name.addAccessoryCustomView(editText);
        QMUICommonListItemView type=listView.createItemView("类型");
        type.setDetailText(types[choice]);
        type.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        validDate=listView.createItemView("有效期");

        View.OnClickListener listener= v -> {
            if (v instanceof QMUICommonListItemView){
                String text = ((QMUICommonListItemView) v).getText().toString();
                switch (text){
                    case "类型":
                        new QMUIDialog.CheckableDialogBuilder(context)
                                .setCheckedIndex(choice)
                                .addItems(types,((dialog, which) -> {
                                    if (which==0){
                                        choice=0;
                                        listView.hideItemView(0,3);
                                    }else{
                                        choice=1;
                                        listView.showItemView(0,3);
                                    }
                                    type.setDetailText(types[choice]);
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "有效期":
                        initDatePicker();
                        if (!validDate.getDetailText().toString().equals("")){
                            String[] times=validDate.getDetailText().toString().split("-");
                            selectDate=Calendar.getInstance();
                            selectDate.set(Integer.parseInt(times[0]),Integer.parseInt(times[1])-1,Integer.parseInt(times[2]));
                            pickerView.setDate(selectDate);
                        }
                        pickerView.show();
                        break;
                }
            }
        };
        listView.setSeparatorStyle(GroupListView.SEPARATOR_STYLE_NONE);
        GroupListView.newSection(context)
                .addItemView(headView,null)
                .addItemView(name,null)
                .addItemView(type,listener)
                .addItemView(validDate,listener)
                .addTo(listView);
        listView.hideItemView(0,3);
    }

    private void initCustomView(){
        editText=new EditText(context);
        editText.setWidth(QMUIDisplayHelper.dp2px(context,120));
        editText.setGravity(Gravity.END);
        editText.setBackgroundColor(context.getResources().getColor(R.color.transparent));
    }

    private void initDatePicker() {
        if (pickerView==null) {
            Calendar startDate = Calendar.getInstance();
            startDate.setTime(new Date());
            Calendar endDate = Calendar.getInstance();
            endDate.set(2028, 11, 31);

            pickerView = new TimePickerView.Builder(context, (date, v1) -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                this.date = sdf.format(date);
                validDate.setDetailText(this.date);
            })
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("选择时间")
                    .setTitleColor(context.getResources().getColor(R.color.black))
                    .setCancelColor(context.getResources().getColor(R.color.grapefruit))
                    .setSubmitColor(context.getResources().getColor(R.color.text1))
                    .setRangDate(startDate, endDate)
                    .isDialog(true)
                    .build();
        }
    }
    public NewFaceDialogBuilder setImage(byte[] imageData) {
        this.imageData = imageData;
        return this;
    }

    public String getName() {
        return editText.getText().toString();
    }

    public String getType() {
        return types[choice];
    }

    public String getDate(){
        return date;
    }
}
