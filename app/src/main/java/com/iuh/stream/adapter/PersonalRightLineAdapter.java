package com.iuh.stream.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalRightLineAdapter extends RecyclerView.Adapter<PersonalRightLineAdapter.PersonRightLineViewHolder>{
    private List<Line> lineList;

    public PersonalRightLineAdapter() {
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
            if(line.getType().equals(Constants.TYPE_TEXT)){
                holder.contentTv.setText(line.getContent());

               if(position == lineList.size() - 1){
                   holder.lastTineLineTv.setVisibility(View.VISIBLE);
                   holder.lastTineLineTv.setText(Util.getTime(line.getCreatedAt()));
                }
               else{
                   holder.lastTineLineTv.setVisibility(View.GONE);
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
        private TextView contentTv;
        private TextView lastTineLineTv;
        public PersonRightLineViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.chat_content);
            lastTineLineTv = itemView.findViewById(R.id.last_time_line_tv);
        }
    }
}
