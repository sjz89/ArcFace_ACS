package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.Values;
import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.bean.ChatMessage;
import com.daylight.arcface_acs.utils.DateUtil;
import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

/**
 * 对话列表适配器
 * Created by Daylight on 2018/3/21.
 */

public class ChatAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ChatMessage> data;
    private byte[] left,right;

    public ChatAdapter(Context context, byte[] right,byte[] left){
        this.context=context;
        this.right=right;
        this.left=left;
    }

    public void setData(List<ChatMessage> data){
        this.data=data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType== Values.MessageType_Send) {
            view=LayoutInflater.from(context).inflate(R.layout.item_msg_send, parent, false);
            return new SendViewHolder(view,context,right);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_msg_receive, parent, false);
            return new ReceiveViewHolder(view,context,left);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getMessageType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (data!=null){
            if (holder instanceof SendViewHolder){
                ((SendViewHolder)holder).content.setText(data.get(position).getText());
                ((SendViewHolder)holder).time.setText(DateUtil.autoTransFormat(data.get(position).getTime()));
            } else if (holder instanceof ReceiveViewHolder){
                ((ReceiveViewHolder)holder).content.setText(data.get(position).getText());
                ((ReceiveViewHolder)holder).time.setText(DateUtil.autoTransFormat(data.get(position).getTime()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    private static class ReceiveViewHolder extends RecyclerView.ViewHolder{
        private TextView time;
        private ImageView headImage;
        private BubbleTextView content;
        ReceiveViewHolder(View itemView,Context context,byte[] left) {
            super(itemView);
            time=itemView.findViewById(R.id.receive_time);
            headImage=itemView.findViewById(R.id.receive_image);
            GlideApp.with(context).load(left).error(R.drawable.icon_failure).into(headImage);
            content =itemView.findViewById(R.id.receive_content);
        }
    }

    private static class SendViewHolder extends RecyclerView.ViewHolder{
        private TextView time;
        private ImageView headImage;
        private BubbleTextView content;
        SendViewHolder(View itemView,Context context,byte[] right) {
            super(itemView);
            time=itemView.findViewById(R.id.send_time);
            headImage=itemView.findViewById(R.id.send_image);
            GlideApp.with(context).load(right).error(R.drawable.icon_failure).into(headImage);
            content =itemView.findViewById(R.id.send_content);
        }
    }
}
