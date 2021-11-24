package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.ChatActivity;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.PersonalChat;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
    private List<PersonalChat> personalChatList;
    private Context mContext;
    private FirebaseAuth mAuth;

    public ChatListAdapter(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setData(List<PersonalChat> personalChatList){
        this.personalChatList = personalChatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        PersonalChat personalChat = personalChatList.get(position);
        if(personalChat != null){
            // personal chat
            if(personalChat.getUsers().size() == 2){
                for(User user: personalChat.getUsers()){
                    if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
                        holder.nameTv.setText(user.getFirstName() + " " + user.getLastName());
                    }
                }

                if(personalChat.getLatestLine().getSenderId().equals(mAuth.getCurrentUser().getUid())){
                    holder.lastLineTv.setText("Bạn: " + personalChat.getLatestLine().getLine().getContent());
                }
                else{
                    holder.lastLineTv.setText(personalChat.getLatestLine().getLine().getContent());
                }

                holder.lastTimeLineTv.setText(Util.getTime(personalChat.getLatestLine().getLine().getCreatedAt()));
            }
        }
    }

    @Override
    public int getItemCount() {
        if(personalChatList != null){
            return personalChatList.size();
        }
        return 0;
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView lastLineTv, lastTimeLineTv, nameTv;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            lastLineTv = itemView.findViewById(R.id.last_line_tv);
            nameTv = itemView.findViewById(R.id.name_tv);
            lastTimeLineTv = itemView.findViewById(R.id.last_date_line_tv);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start chat activity
                    PersonalChat personalChat = personalChatList.get(getAdapterPosition());

                    for(User user: personalChat.getUsers()){
                        if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(FriendsAdapter.USER, user);
                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openDialog(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    private void openDialog(int position) {
        PersonalChat personalChat = personalChatList.get(position);
        User tempUser = new User();
        for(User user: personalChat.getUsers()){
            if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
               tempUser = user;
            }
        }

        // set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        String name = tempUser.getFirstName() + " " + tempUser.getLastName();
        builder.setTitle(name);
        String[] options = {"Xóa trò chuyện", "Bật chế độ Mini chat"};
        builder.setItems(options, (dialog, which) -> {
            switch (which){
                case 0:
                    CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tính năng này chưa được phát triển");
                    break;
                case 1:
                    CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tính năng này chưa được phát triển");
                    break;

            }
        });


        builder.create().show();
    }
}
