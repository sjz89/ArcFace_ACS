package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.bean.Feature;

import java.util.List;


public class DetailImageAdapter extends RecyclerView.Adapter {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private Context context;
    private LayoutInflater mInflater;
    private List<Feature> mData;
    private FacesListAdapter.OnItemClickListener mOnItemClickListener;

    public DetailImageAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1)
            return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==TYPE_NORMAL) {
            View view = mInflater.inflate(R.layout.item_detail, parent, false);
            return new ImageViewHolder(view);
        }else{
            View view=mInflater.inflate(R.layout.item_foot,parent,false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder){
            if (mData!=null){
                GlideApp.with(context).asBitmap().load(mData.get(position).getImageData())
                        .into(((ImageViewHolder) holder).imageView);

            }
            if (mOnItemClickListener!=null)
                ((ImageViewHolder) holder).imageView.setOnClickListener(v ->
                    mOnItemClickListener.onItemClick(holder.itemView,position));
        }else{
            if (mOnItemClickListener!=null)
                ((FooterViewHolder)holder).add.setOnClickListener(v ->
                    mOnItemClickListener.onFootClick(holder.itemView));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 1 : mData.size()+1;
    }

    public void setData(List<Feature> data){
        mData=data;
    }

    public void setOnItemClickListener(FacesListAdapter.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.detail_image);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{
        private ImageView add;
        FooterViewHolder(View itemView) {
            super(itemView);
            add=itemView.findViewById(R.id.item_foot_add);
        }
    }
}
