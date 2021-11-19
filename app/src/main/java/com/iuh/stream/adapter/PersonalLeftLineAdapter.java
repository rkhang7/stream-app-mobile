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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalLeftLineAdapter extends RecyclerView.Adapter<PersonalLeftLineAdapter.PersonLeftLineViewHolder> {
    private List<Line> lineList;
    private String hisImageUrl;

    public PersonalLeftLineAdapter(String hisImageUrl) {
        this.hisImageUrl = hisImageUrl;
    }

    public void setData(List<Line> lineList) {
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

        if (line != null) {
            if (line.getType().equals(Constants.TYPE_TEXT)) {
                holder.contentTv.setText(line.getContent());

                if (position == 0) {
                    Picasso.get().load(hisImageUrl).into(holder.avatarIv);
                    holder.avatarIv.setVisibility(View.VISIBLE);
                    holder.lastLineTv.setVisibility(View.GONE);
                }else if (position == lineList.size() - 1) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                    holder.lastLineTv.setVisibility(View.VISIBLE);
                    holder.lastLineTv.setText(Util.getTime(line.getCreatedAt()));
                } else {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                    holder.lastLineTv.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        if (lineList != null) {
            return lineList.size();
        }
        return 0;
    }

    public class PersonLeftLineViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView contentTv;
        private TextView lastLineTv;

        public PersonLeftLineViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.chat_image);
            contentTv = itemView.findViewById(R.id.chat_content);
            lastLineTv = itemView.findViewById(R.id.last_time_line_tv);
        }
    }


}
