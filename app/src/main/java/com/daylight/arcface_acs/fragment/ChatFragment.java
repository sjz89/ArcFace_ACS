package com.daylight.arcface_acs.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.activity.MainActivity;
import com.daylight.arcface_acs.adapter.ChatAdapter;
import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.bean.Recent;
import com.daylight.arcface_acs.bean.User;
import com.daylight.arcface_acs.utils.SharedPreferencesUtil;
import com.daylight.arcface_acs.viewmodel.FaceViewModel;
import com.daylight.arcface_acs.viewmodel.UserViewModel;
import com.google.gson.Gson;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 聊天界面
 * Created by Daylight on 2018/3/20.
 */

public class ChatFragment extends BaseFragment {
    private View view;
    private UserViewModel viewModel;
    private FaceViewModel faceViewModel;
    private User neighbor,user;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel= ViewModelProviders.of(getBaseFragmentActivity()).get(UserViewModel.class);
        faceViewModel=ViewModelProviders.of(getBaseFragmentActivity()).get(FaceViewModel.class);
        neighbor=viewModel.getNeighbor();
        user=viewModel.loadUser(SharedPreferencesUtil.getAccount(getContext()));
        viewModel.getMessages().observe(this,messages -> {
            if (messages!=null&&messages.size()!=0){
                adapter.setData(messages);
                recyclerView.scrollToPosition(messages.size()-1);
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    protected View onCreateView() {
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_chat,null);
        initTopBar();
        initRecyclerView();
        initSend();
        return view;
    }

    private void initTopBar(){
        QMUITopBarLayout topBar=view.findViewById(R.id.topbar_chat);
        topBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        topBar.setTitle(neighbor.getName());
    }

    private void initSend(){
        TextView send=view.findViewById(R.id.chat_send);
        EditText editor=view.findViewById(R.id.chat_editor);
        send.setOnClickListener(v -> {
            ChatMessage message=new ChatMessage();
            message.setRightId(user.getPhoneNum());
            message.setLeftId(neighbor.getPhoneNum());
            message.setText(editor.getText().toString());
            message.setMessageType(Values.MessageType_Send);
            message.setTime((new Date()).getTime());
            viewModel.insertMessage(message);
            Gson gson=new Gson();
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("msgType",102);
                jsonObject.put("msg",gson.toJson(message));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((MainActivity)getBaseFragmentActivity()).getMsgBinder().getService().sendMsg(jsonObject.toString());
            Recent recent=new Recent();
            recent.setAccount(user.getPhoneNum());
            recent.setNeighbor(neighbor.getPhoneNum());
            recent.setUniqueId(user.getPhoneNum()+"to"+neighbor.getPhoneNum());
            recent.setLastMsg(message.getText());
            recent.setTime(message.getTime());
            viewModel.insertRecent(recent);
            editor.setText("");
        });
    }

    private void initRecyclerView(){
        recyclerView=view.findViewById(R.id.recycler_chat);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ChatAdapter(getContext(),faceViewModel.loadFace(user.getPhoneNum()).getFaceData(),
                faceViewModel.loadFace(neighbor.getPhoneNum()).getFaceData());
        recyclerView.setAdapter(adapter);
    }
}
