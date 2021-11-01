package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FiendsViewHolder>{
    private Context mContext;
    private List<User> userList;

    public FriendsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FiendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        return new FiendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiendsViewHolder holder, int position) {
        User user = userList.get(position);
//        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
        holder.nameTv.setText(user.getFirstName() + " " + user.getLastName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class FiendsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView nameTv;
        public FiendsViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.friend_avatar_iv);
            nameTv = itemView.findViewById(R.id.friend_name_tv);
        }
    }
}
