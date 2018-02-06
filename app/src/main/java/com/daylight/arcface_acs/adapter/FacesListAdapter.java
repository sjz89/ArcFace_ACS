package com.daylight.arcface_acs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daylight.arcface_acs.app.GlideApp;
import com.daylight.arcface_acs.R;
import com.daylight.arcface_acs.bean.Face;

import java.util.List;


/**
 *
 * Created by Daylight on 2018/1/27.
 */

public class FacesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_NORMAL = 2;
    private final LayoutInflater mInflater;
    private List<Face> mFaces;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    public FacesListAdapter(Context context){
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
            View view = mInflater.inflate(R.layout.item_faces, parent, false);
            return new FaceViewHolder(view);
        }else{
            View view=mInflater.inflate(R.layout.item_foot,parent,false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FaceViewHolder) {
            FaceViewHolder faceViewHolder=(FaceViewHolder)holder;
            if (mFaces != null) {
                Face current = mFaces.get(position);
                GlideApp.with(context).asBitmap().load(current.getFaceData())
                            .into(faceViewHolder.face);
                faceViewHolder.name.setText(current.getName());
            }
            if (mOnItemClickListener != null) {
                faceViewHolder.face.setOnClickListener(v ->
                        mOnItemClickListener.onItemClick(faceViewHolder.itemView, position));
            }
        }else{
            FooterViewHolder footerViewHolder=(FooterViewHolder)holder;
            if (mOnItemClickListener!=null)
                footerViewHolder.add.setOnClickListener(v ->
                        mOnItemClickListener.onFootClick(footerViewHolder.itemView));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setFaces(List<Face> faces){
        mFaces=faces;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFaces == null ? 1 : mFaces.size()+1;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onFootClick(View view);
    }

    class FaceViewHolder extends RecyclerView.ViewHolder{
        private final ImageView face;
        private final TextView name;
        FaceViewHolder(View itemView) {
            super(itemView);
            face=itemView.findViewById(R.id.item_face);
            name=itemView.findViewById(R.id.item_name);
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
