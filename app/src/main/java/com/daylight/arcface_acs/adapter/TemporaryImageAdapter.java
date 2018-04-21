package com.daylight.arcface_acs.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.bean.TempData;

import java.util.List;

public class TemporaryImageAdapter extends RecyclerView.Adapter<TemporaryImageAdapter.ViewHolder> {
    private Context context;
    private List<TempData> data;
    private OnTempFaceClickListener onTempFaceClickListener;

    public TemporaryImageAdapter(Context context){
        this.context=context;
    }

    public void setData(List<TempData> data){
        this.data=data;
        notifyDataSetChanged();
    }

    public void setOnTempFaceClickListener(OnTempFaceClickListener listener){
        this.onTempFaceClickListener=listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_temp_faces,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data!=null&&data.size()!=0){
            GlideApp.with(context).load(data.get(position).getFace()).centerCrop().into(holder.imageView);
            holder.bg.setBackgroundResource(data.get(position).isChecked()?R.drawable.item_image_checked:R.drawable.item_image_bg);
        }
        if (onTempFaceClickListener!=null){
            holder.imageView.setOnClickListener(v -> onTempFaceClickListener.onClick(v,position));
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public interface OnTempFaceClickListener{
        void onClick(View view,int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        FrameLayout bg;
        ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.temp_face);
            bg=itemView.findViewById(R.id.temp_bg);
        }
    }
}
