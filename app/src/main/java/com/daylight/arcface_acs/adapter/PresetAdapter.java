package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.bean.PresetData;

import java.util.List;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.ViewHolder> {
    private Context context;
    private List<PresetData> data;
    private OnPresetItemClickListener onPresetItemClickListener;
    public PresetAdapter(Context context){
        this.context=context;
    }
    public void setData(List<PresetData> data){
        this.data=data;
        notifyDataSetChanged();
    }
    public void setOnPresetItemClickListener(OnPresetItemClickListener listener){
        this.onPresetItemClickListener=listener;
    }
    public void setLastItemCheck(){
        for (PresetData presetData:data)
            presetData.setChecked(false);
        data.get(data.size()-1).setChecked(true);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_preset,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data!=null&&data.size()!=0){
            holder.type.setText(data.get(position).getType());
            holder.bg.setBackgroundResource(data.get(position).isChecked()?R.drawable.item_image_checked:R.drawable.item_image_bg);
        }
        if (onPresetItemClickListener!=null){
            holder.type.setOnClickListener(v-> onPresetItemClickListener.onClick(v,position));
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public interface OnPresetItemClickListener{
        void onClick(View view,int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private FrameLayout bg;
        private TextView type;
        ViewHolder(View itemView) {
            super(itemView);
            bg=itemView.findViewById(R.id.preset_bg);
            type=itemView.findViewById(R.id.preset_type);
        }
    }
}
