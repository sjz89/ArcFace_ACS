package com.daylight.arcface_acs.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.dialog.PinLockDialog;

public class DoorOpenWidget extends AppWidgetProvider {
    public static final String CLICK_ACTION = "com.daylight.arcface_acs.CLICK";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        Intent intent = new Intent(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, R.id.widget_imageView, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_imageView, pendingIntent);

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (CLICK_ACTION.equals(intent.getAction())) {
            PinLockDialog passwordInput = new PinLockDialog(context);
            passwordInput.verifyPin();
//            new QMUIDialog.MenuDialogBuilder(context)
//                    .setTitle("请选择要开启的门禁")
//                    .addItems(new String[]{"15栋","北门(进口)","北门(出口)","南门(进口)","南门(出口)"},
//                            (dialog,position1)->{
//                                dialog.dismiss();
//                            }).show();
        }
    }
}
