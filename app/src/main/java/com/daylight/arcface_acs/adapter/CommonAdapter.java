package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.bean.CommonData;
import com.daylight.arcface_acs.view.InfoItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/18.
 */

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder> {
    private Context context;
    private List<CommonData> data;
    private int orientation;
    private int ACCESSORY_TYPE;
    private OnCommonItemClickListener onCommonItemClickListener;
    public CommonAdapter(Context context, int orientation){
        this.context=context;
        this.orientation=orientation;
        this.ACCESSORY_TYPE =QMUICommonListItemView.ACCESSORY_TYPE_NONE;
    }
    public CommonAdapter(Context context,int orientation,int ACCESSORY_TYPE){
        this.context=context;
        this.orientation=orientation;
        this.ACCESSORY_TYPE =ACCESSORY_TYPE;
    }
    public void setData(List<CommonData> data){
        this.data=data;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_commom,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data!=null){
            holder.item.setOrientation(orientation);
            holder.item.setText(data.get(position).getText());
            holder.item.setDetailText(data.get(position).getSubText());
            if (data.get(position).getImage()!=null)
                holder.item.setImageDrawable(data.get(position).getImage(),52,52);
            else if (data.get(position).getPic()!=null)
                holder.item.setImageDrawable(data.get(position).getPic(),64,52);
            else if (data.get(position).getIcon()!=0)
                holder.item.setImageDrawable(data.get(position).getIcon(),36,36);
            holder.item.setAccessoryType(ACCESSORY_TYPE);
            if (ACCESSORY_TYPE==QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM) {
                holder.initTextView();
                holder.item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                holder.item.addAccessoryCustomView(holder.getTextView());
                holder.getTextView().setText(data.get(position).getTime());
            }
        }
        if (onCommonItemClickListener!=null){
            holder.item.setOnClickListener(v -> onCommonItemClickListener.onItemClick(holder.item,position));
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public void setOnCommonItemClickListener(OnCommonItemClickListener listener){
        this.onCommonItemClickListener=listener;
    }

    public interface OnCommonItemClickListener {
        void onItemClick(View view,int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private InfoItemView item;
        private Context context;
        private TextView textView;
        ViewHolder(View itemView,Context context) {
            super(itemView);
            this.context=context;
            item=itemView.findViewById(R.id.common_item);
        }
        void initTextView(){
            textView=new TextView(context);
            textView.setTextSize(13);
            textView.setLineSpacing(0,1.2f);
            textView.setGravity(Gravity.END);
            textView.setTextColor(context.getResources().getColor(R.color.text_time));
        }
        TextView getTextView(){
            return textView;
        }
    }
}
