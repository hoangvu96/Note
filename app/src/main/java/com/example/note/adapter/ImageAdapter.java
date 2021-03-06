package com.example.note.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.note.R;
import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private OnImageAdapter onImageAdapter;
    private List<ImagePath> temps;

    public ImageAdapter(List<ImagePath> imagePaths, Context context, OnImageAdapter onImageAdapter) {
        this.context = context;
        this.onImageAdapter = onImageAdapter;
        temps = new ArrayList<>();
        temps.addAll(imagePaths);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri uri = Uri.parse(temps.get(position).getImagePath());
        Glide.with(context).load(uri).apply(new RequestOptions().centerCrop()).into(holder.imvImage);
    }

    @Override
    public int getItemCount() {
        return temps != null ? temps.size() : 0;
    }

    public void removeItem(int pos){
        temps.remove(temps.get(pos));
        notifyItemRemoved(pos);
    }

    public void addItem(ImagePath imagePath){
        temps.add(imagePath);
        notifyItemChanged(temps.size()-1);
    }

    public List<ImagePath> getImagePaths() {
        return temps;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.imv_image)
        ImageView imvImage;
        @BindView(R.id.imv_close)
        ImageView imvClose;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            imvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageAdapter.onRemove(getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onImageAdapter.onClick(getLayoutPosition());
                }
            });
        }
    }

    public interface OnImageAdapter{
        void onRemove(int pos);
        void onClick(int pos);
    }
}
