package com.iuh.stream.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.activity.ChatActivity;
import com.iuh.stream.activity.GroupChatActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.models.chatlist.Group;
import com.iuh.stream.models.chatlist.LastLine;
import com.iuh.stream.models.chatlist.Chats;
import com.iuh.stream.service.FloatingViewService;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{
    private List<Chats> chatsList;
    private Context mContext;
    private FirebaseAuth mAuth;

    public ChatListAdapter(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setData(List<Chats> chatsList){
        this.chatsList = chatsList;
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
        Chats chats = chatsList.get(position);
        if(chats != null){
            // chat 1:1
            if(chats.getGroup() == null){
                for(User user: chats.getUsers()){
                    if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
                        holder.nameTv.setText(user.getFirstName() + " " + user.getLastName());
                    }
                }

                LastLine lastLine = chats.getLatestLine();
                // text type
                if(lastLine.getLine().getType().equals(MyConstant.TEXT_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: " + lastLine.getLine().getContent());
                    }
                    else{

                        holder.lastLineTv.setText(lastLine.getLine().getContent());
                    }
                }
                // image type
                else if(lastLine.getLine().getType().equals(MyConstant.IMAGE_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: [Hình ảnh]");
                    }
                    else{
                        holder.lastLineTv.setText("[Hình ảnh]");
                    }
                }

                // file type
                else if(lastLine.getLine().getType().equals(MyConstant.FILE_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: " + lastLine.getLine().getContent());
                    }
                    else{
                        holder.lastLineTv.setText(lastLine.getLine().getContent());
                    }
                }
                holder.lastTimeLineTv.setText(Util.getTime(lastLine.getLine().getCreatedAt()));


            }
            // group chat
            else{
                Group group = chats.getGroup();
                holder.avatarIv.setImageResource(R.drawable.group_chat);
                holder.nameTv.setText(group.getName());

                LastLine lastLine = chats.getLatestLine();
                // text type
                if(lastLine.getLine().getType().equals(MyConstant.TEXT_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: " + lastLine.getLine().getContent());
                    }
                    else{
                        for(User user: chats.getUsers()){
                            if(user.get_id().equals(lastLine.getSenderId())){
                                holder.lastLineTv.setText(user.getFirstName() + " " + user.getLastName() + ": " + lastLine.getLine().getContent());
                            }
                        }

                    }
                }
                // image type
                else if(lastLine.getLine().getType().equals(MyConstant.IMAGE_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: [Hình ảnh]");
                    }
                    else{
                        for(User user: chats.getUsers()){
                            if(user.get_id().equals(lastLine.getSenderId())){
                                holder.lastLineTv.setText(user.getFirstName() + " " + user.getLastName() + ": " + "[Hình ảnh]");
                            }
                        }
                    }
                }

                // file type
                else if(lastLine.getLine().getType().equals(MyConstant.FILE_TYPE)){
                    if(lastLine.getSenderId().equals(mAuth.getCurrentUser().getUid())){
                        holder.lastLineTv.setText("Bạn: " + lastLine.getLine().getContent());
                    }
                    else{
                        for(User user: chats.getUsers()){
                            if(user.get_id().equals(lastLine.getSenderId())){
                                holder.lastLineTv.setText(user.getFirstName() + " " + user.getLastName() + ": " + lastLine.getLine().getContent());
                            }
                        }
                    }
                }
                holder.lastTimeLineTv.setText(Util.getTime(lastLine.getLine().getCreatedAt()));

            }





            if(chats.getUnreadMessagesCount() <= 0){
                holder.unreadMessageTv.setVisibility(View.GONE);
            }
            else if(chats.getUnreadMessagesCount() > 5){
                holder.unreadMessageTv.setText("5+");
            }
            else{
                holder.unreadMessageTv.setText(chats.getUnreadMessagesCount() + "");
            }

        }
    }

    @Override
    public int getItemCount() {
        if(chatsList != null){
            return chatsList.size();
        }
        return 0;
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView lastLineTv, lastTimeLineTv, nameTv, unreadMessageTv;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            lastLineTv = itemView.findViewById(R.id.last_line_tv);
            nameTv = itemView.findViewById(R.id.name_tv);
            lastTimeLineTv = itemView.findViewById(R.id.last_date_line_tv);
            unreadMessageTv = itemView.findViewById(R.id.unread_message_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start chat activity
                    Chats chats = chatsList.get(getAdapterPosition());
                    // chat 1:1
                    if(chats.getGroup() == null){
                        for(User user: chats.getUsers()){
                            if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                                Intent intent = new Intent(mContext, ChatActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(MyConstant.USER_KEY, user);
                                mContext.startActivity(intent);
                            }
                        }
                    }
                    else {
                        Group group = chats.getGroup();
                        Intent intent = new Intent(mContext, GroupChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MyConstant.GROUP_KEY, group);
                        bundle.putInt(MyConstant.NUMBER_MEMBER, chats.getUsers().size());
                        bundle.putString(MyConstant.GROUP_CHAT_ID, chats.get_id());
                        bundle.putSerializable(MyConstant.LIST_MEMBER_KEY, (Serializable) chats.getUsers());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
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
        Chats chats = chatsList.get(position);
        User tempUser = new User();
        for(User user: chats.getUsers()){
            if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
               tempUser = user;
            }
        }

        // set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        String name = tempUser.getFirstName() + " " + tempUser.getLastName();
        builder.setTitle(name);
        String[] options = {"Xóa cuộc trò chuyện", "Bật chế độ Mini chat"};
        builder.setItems(options, (dialog, which) -> {
            switch (which){
                case 0:
                    openDialogDeleteChat(position);
                    break;
                case 1:
                    checkPermission(position);
                    break;

            }
        });


        builder.create().show();
    }

    private void openDialogDeleteChat(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa cuộc trò chuyện");
        builder.setIcon(R.drawable.icons8_warning_64px);
        builder.setMessage("Bạn có chắc muốn xóa cuộc trò chuyện này?");
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String id = chatsList.get(position).get_id();
                deleteChat(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), position);
            }
        });

        builder.create().show();
    }

    private void deleteChat(String id, String accessToken, int position) {
        RetrofitService.getInstance.deleteChatById(id, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            deleteChat(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN), position);
                        }
                        else if (response.code() == 200){
                            chatsList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,getItemCount());
                        }
                        else {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void checkPermission(int position) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Chats chats = chatsList.get(position);

                for(User user: chats.getUsers()){
                    if(!user.get_id().equals(mAuth.getCurrentUser().getUid())){
                        Intent intent = new Intent(mContext, FloatingViewService.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(MyConstant.USER_KEY, user);
                        mContext.startService(intent);
                    }
                }


            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(mContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .check();
    }
}
