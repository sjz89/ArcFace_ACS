package com.daylight.arcface_acs.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.database.UserRepository;
import com.daylight.arcface_acs.rxbus.RxBusHelper;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * 后台服务
 * Created by Daylight on 2018/3/22.
 */

public class MessageService extends Service {
    private WebSocketClient webSocketClient;
    private UserRepository userRepository;
    @Override
    public void onCreate() {
        super.onCreate();
        userRepository=new UserRepository(getApplication());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        connect();
        return new MsgBinder();
    }

    public class MsgBinder extends Binder {
        public MessageService getService(){
            return MessageService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        webSocketClient.close();
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    public void connect(){
        try {
            webSocketClient=new WebSocketClient(new URI("ws://192.168.2.220:8881/websocket"),new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake serverHandshakeData) {
                    JSONObject jsonObject=new JSONObject();
                    try {
                        jsonObject.put("msgType",101);
                        jsonObject.put("id", SharedPreferencesUtil.getAccount(getApplicationContext()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webSocketClient.send(jsonObject.toString());
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JSONObject jsonObject=new JSONObject(message);
                        switch (jsonObject.getInt("msgType")){
                            case 102:
                                Gson gson=new Gson();
                                ChatMessage chatMessage=gson.fromJson(jsonObject.getString("msg"),ChatMessage.class);
                                String id=chatMessage.getLeftId();
                                chatMessage.setLeftId(chatMessage.getRightId());
                                chatMessage.setRightId(id);
                                chatMessage.setMessageType(Values.MessageType_Receive);
                                userRepository.insertMessage(chatMessage);
                                Recent recent=new Recent();
                                recent.setAccount(chatMessage.getRightId());
                                recent.setNeighbor(chatMessage.getLeftId());
                                recent.setUniqueId(chatMessage.getRightId()+"to"+chatMessage.getLeftId());
                                recent.setTime(chatMessage.getTime());
                                recent.setLastMsg(chatMessage.getText());
                                userRepository.insertRecent(recent);
                                break;
                            case 201:
                                RxBusHelper.post(jsonObject.getString("msg"));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                }
                @Override
                public void onError(Exception e) {

                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg){
        if (webSocketClient.isOpen())
            webSocketClient.send(msg);
    }
}
