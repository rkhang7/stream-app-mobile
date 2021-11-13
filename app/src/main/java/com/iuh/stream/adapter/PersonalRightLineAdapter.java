package com.iuh.stream.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.chat.Line;

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
            if(line.getType().equals("text")){
                holder.contentTv.setText(line.getContent());
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
        public PersonRightLineViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTv = itemView.findViewById(R.id.chat_content);
        }
    }
}
