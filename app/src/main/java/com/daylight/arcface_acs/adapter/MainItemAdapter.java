package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.bean.MainItemData;

import java.util.List;

/**
 *
 * Created by Daylight on 2018/3/16.
 */

public class MainItemAdapter extends RecyclerView.Adapter<MainItemAdapter.ViewHolder> {
    private Context context;
    private List<MainItemData> data;
    private OnButtonClickListener mOnButtonClickListener;
    public MainItemAdapter(Context context,List<MainItemData> data){
        this.context=context;
        this.data=data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_button,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data!=null){
            holder.container.setTag(data.get(position).getText());
            holder.text.setText(data.get(position).getText());
            GlideApp.with(context).load(data.get(position).getPic()).into(holder.icon);
            holder.container.setBackgroundResource(Background.values()[position].res);
        }
        if (mOnButtonClickListener!=null){
            holder.container.setOnClickListener(v-> mOnButtonClickListener.onButtonClick(holder.container,position));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onButtonClick(View view,int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private FrameLayout container;
        private ImageView icon;
        private TextView text;
        ViewHolder(View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.container);
            icon=itemView.findViewById(R.id.icon);
            text=itemView.findViewById(R.id.text);
        }
    }
    enum Background{
        aqua(R.drawable.btn_ripple_aqua),
        bittersweet(R.drawable.btn_ripple_bittersweet),
        bluejeans(R.drawable.btn_ripple_bluejeans),
        grapefruit(R.drawable.btn_ripple_grapefruit),
        green(R.drawable.btn_ripple_green),
        lavender(R.drawable.btn_ripple_lavender),
        mint(R.drawable.btn_ripple_mint),
        pinkrose(R.drawable.btn_ripple_pinkrose),
        sunflower(R.drawable.btn_ripple_sunflower);

        int res;
        Background(int res){
            this.res=res;
        }
    }
}
