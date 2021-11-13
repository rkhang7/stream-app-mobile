package com.iuh.stream.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.chat.Line;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalLeftLineAdapter extends RecyclerView.Adapter<PersonalLeftLineAdapter.PersonLeftLineViewHolder>{
    private List<Line> lineList;
    private String hisImageUrl;

    public PersonalLeftLineAdapter(String hisImageUrl) {
        this.hisImageUrl = hisImageUrl;
    }

    public void setData(List<Line> lineList){
        this.lineList = lineList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonLeftLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_item_left, parent, false);
        return new PersonLeftLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonLeftLineViewHolder holder, int position) {
        Line line = lineList.get(position);
        if(line != null){
            if(position == 0){
                if(line.getType().equals("text")){
                    holder.contentTv.setText(line.getContent());
                    Picasso.get().load(hisImageUrl).into(holder.avatarIv);
                }
            }
            else{
                holder.avatarIv.setVisibility(View.INVISIBLE);
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

    public class PersonLeftLineViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView contentTv;
        public PersonLeftLineViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.chat_image);
            contentTv = itemView.findViewById(R.id.chat_content);
        }
    }
}
