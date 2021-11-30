package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonalRightLineAdapter extends RecyclerView.Adapter<PersonalRightLineAdapter.PersonRightLineViewHolder>{
    private Context mContext;
    private List<Line> lineList;

    public PersonalRightLineAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<Line> lineList){
        this.lineList = lineList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonRightLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_item_right, parent, false);
        return new PersonRightLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonRightLineViewHolder holder, int position) {
        Line line = lineList.get(position);
        if(line != null){
            // text type
            if(line.getType().equals(MyConstant.TEXT_TYPE)){
                holder.textLayout.setVisibility(View.VISIBLE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.textContentTv.setText(line.getContent());
               if(position == lineList.size() - 1){
                   holder.textLastTimeTv.setVisibility(View.VISIBLE);
                   holder.textLastTimeTv.setText(Util.getTime(line.getCreatedAt()));

                }
               else{
                   holder.textLastTimeTv.setVisibility(View.GONE);
               }
            }
            // image type
            else if(line.getType().equals(MyConstant.IMAGE_TYPE)){
                holder.textLayout.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(line.getContent()).into(holder.imageContentIv);
                if(position == lineList.size() - 1){
                    holder.imageLastTimeTv.setVisibility(View.VISIBLE);
                    holder.imageLastTimeTv.setText(Util.getTime(line.getCreatedAt()));

                }
                else{
                    holder.imageLastTimeTv.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(lineList != null){
            return lineList.size();
        }
       return 0;
    }

    public class PersonRightLineViewHolder extends RecyclerView.ViewHolder {
        private TextView textContentTv;
        private TextView textLastTimeTv, imageLastTimeTv;
        private LinearLayout textLayout, imageLayout;
        private ImageView imageContentIv;
        public PersonRightLineViewHolder(@NonNull View itemView) {
            super(itemView);

            textContentTv = itemView.findViewById(R.id.chat_text_content);
            textLastTimeTv = itemView.findViewById(R.id.text_last_time_line_tv);
            textLayout = itemView.findViewById(R.id.text_layout);
            imageLayout = itemView.findViewById(R.id.image_layout);
            imageContentIv = itemView.findViewById(R.id.chat_image_content);
            imageLastTimeTv = itemView.findViewById(R.id.image_last_time_line_tv);
        }
    }
}
