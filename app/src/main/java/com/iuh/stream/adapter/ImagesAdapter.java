package com.iuh.stream.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.activity.ViewImageMessageActivity;
import com.iuh.stream.models.response.ImageContentResponse;
import com.iuh.stream.models.response.ImageUrlResponse;
import com.iuh.stream.utils.MyConstant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>{
    private Context mContext;
    private List<ImageContentResponse> imageUrlList;

    public ImagesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<ImageContentResponse> imageUrlList){
        this.imageUrlList = imageUrlList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        String imageUrl = imageUrlList.get(position).getContent();
        if(imageUrl != null){
            Picasso.get().load(imageUrl).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageContentResponse imageContentResponse = imageUrlList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ViewImageMessageActivity.class);
                    intent.putExtra(MyConstant.CONTENT_KEY, imageContentResponse.getContent());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
