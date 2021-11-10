package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.interfaces.FriendListener;
import com.iuh.stream.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendCheckBoxAdapter extends RecyclerView.Adapter<FriendCheckBoxAdapter.FriendCheckBoxViewHolder>{
    private Context mContext;
    private List<User> userList;
    private FriendListener friendListener;
    private List<String> friendSelectedId;

    public FriendCheckBoxAdapter(Context mContext, FriendListener friendListener) {
        this.mContext = mContext;
        this.friendListener = friendListener;
        friendSelectedId =  new ArrayList<>();
    }

    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendCheckBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_checkbox, parent, false);
        return new FriendCheckBoxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendCheckBoxViewHolder holder, int position) {
        User user = userList.get(position);
        if(user != null){
            Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
            holder.nameTv.setText(user.getFirstName() + " " + user.getLastName());
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.checkBox.isChecked()){
                        friendSelectedId.add(user.get_id());
                    }
                    else{
                        friendSelectedId.remove(user.get_id());
                    }
                    friendListener.onFriendChange(friendSelectedId);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if(userList.size() > 0){
            return userList.size();
        }
        return 0;
    }

    public class FriendCheckBoxViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView nameTv;
        private CheckBox checkBox;
        public FriendCheckBoxViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            nameTv = itemView.findViewById(R.id.name_tv);
            checkBox = itemView.findViewById(R.id.friend_checkbox);
        }
    }
}
