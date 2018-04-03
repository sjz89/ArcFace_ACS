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
    private EditText editName;
    private EditText editIdNum;
    private byte[] imageData;
    private String[] types;
    private int choice;
    private String startTime,endTime;
    private QMUICommonListItemView startDate;
    private QMUICommonListItemView endDate;
    private TimePickerView pickerView;
    private Calendar selectStartDate;
    private Calendar selectEndDate;

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
        name.addAccessoryCustomView(editName);
        QMUICommonListItemView idNum=listView.createItemView("身份证号");
        idNum.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        idNum.addAccessoryCustomView(editIdNum);
        QMUICommonListItemView type=listView.createItemView("类型");
        type.setDetailText(types[choice]);
        type.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        startDate=listView.createItemView("起始日期");
        endDate=listView.createItemView("终止日期");

        Calendar beginDate = Calendar.getInstance();
        beginDate.setTime(new Date());

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
                                        listView.hideItemView(0,4);
                                    }else{
                                        choice=1;
                                        listView.showItemView(0,3);
                                        listView.showItemView(0,4);
                                    }
                                    type.setDetailText(types[choice]);
                                    dialog.dismiss();
                                })).show();
                        break;
                    case "起始日期":
                        initDatePicker(beginDate,true);
                        if (!startDate.getDetailText().toString().equals("")){
                            String[] times= startDate.getDetailText().toString().split("-");
                            selectStartDate =Calendar.getInstance();
                            selectStartDate.set(Integer.parseInt(times[0]),Integer.parseInt(times[1])-1,Integer.parseInt(times[2]));
                            pickerView.setDate(selectStartDate);
                        }
                        pickerView.show();
                        break;
                    case "终止日期":
                        if (!startDate.getDetailText().toString().equals("")){
                            String[] times= startDate.getDetailText().toString().split("-");
                            beginDate.set(Integer.parseInt(times[0]),Integer.parseInt(times[1])-1,Integer.parseInt(times[2]));
                        }
                        initDatePicker(beginDate,false);
                        if (!endDate.getDetailText().toString().equals("")){
                            String[] times= endDate.getDetailText().toString().split("-");
                            selectEndDate =Calendar.getInstance();
                            selectEndDate.set(Integer.parseInt(times[0]),Integer.parseInt(times[1])-1,Integer.parseInt(times[2]));
                            pickerView.setDate(selectStartDate);
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
                .addItemView(startDate,listener)
                .addItemView(endDate,listener)
                .addTo(listView);
        listView.hideItemView(0,3);
        listView.hideItemView(0,4);
    }

    private void initCustomView(){
        editName =new EditText(context);
        editName.setWidth(QMUIDisplayHelper.dp2px(context,120));
        editName.setGravity(Gravity.END);
        editName.setBackgroundColor(context.getResources().getColor(R.color.transparent));

        editIdNum =new EditText(context);
        editIdNum.setWidth(QMUIDisplayHelper.dp2px(context,180));
        editIdNum.setGravity(Gravity.END);
        editIdNum.setBackgroundColor(context.getResources().getColor(R.color.transparent));

    }

    private void initDatePicker(Calendar beginDate,boolean startOrEnd) {
        Calendar endDate = Calendar.getInstance();
        endDate.set(2028, 11, 31);

        pickerView = new TimePickerView.Builder(context, (date, v1) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            if (startOrEnd) {
                this.startTime = sdf.format(date);
                this.startDate.setDetailText(this.startTime);
            }else{
                this.endTime=sdf.format(date);
                this.endDate.setDetailText(this.endTime);
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("选择时间")
                .setTitleColor(context.getResources().getColor(R.color.black))
                .setCancelColor(context.getResources().getColor(R.color.grapefruit))
                .setSubmitColor(context.getResources().getColor(R.color.text1))
                .setRangDate(beginDate, endDate)
                .isDialog(true)
                .build();
    }

    public NewFaceDialogBuilder setImage(byte[] imageData) {
        this.imageData = imageData;
        return this;
    }

    public String getName() {
        return editName.getText().toString();
    }

    public String getIdNum(){
        return editIdNum.getText().toString();
    }

    public String getType() {
        return types[choice];
    }

    public String getStartTime(){
        return startTime;
    }

    public String getEndTime(){
        return endTime;
    }
}
