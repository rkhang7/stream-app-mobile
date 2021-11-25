package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddFriendActivity;
import com.iuh.stream.activity.ChatActivity;
import com.iuh.stream.activity.FriendProfileActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FiendsViewHolder>{
    private final Context mContext;
    private List<User> userList;
    private final FirebaseAuth mAuth;


    public FriendsAdapter(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
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
        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
        holder.nameTv.setText(user.getFirstName() + " " + user.getLastName());


        if(user.isOnline()){
            holder.onlineIv.setVisibility(View.VISIBLE);
            holder.offlineIv.setVisibility(View.INVISIBLE);
        }
        else{
            holder.onlineIv.setVisibility(View.INVISIBLE);
            holder.offlineIv.setVisibility(View.VISIBLE);
        }

        SocketClient.getInstance().on("offline user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.onlineIv.setVisibility(View.INVISIBLE);
                        holder.offlineIv.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        SocketClient.getInstance().on("online user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.onlineIv.setVisibility(View.VISIBLE);
                        holder.offlineIv.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class FiendsViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView avatarIv;
        private final TextView nameTv;
        private final ImageView onlineIv;
        private final ImageView offlineIv;
        public FiendsViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.friend_avatar_iv);
            nameTv = itemView.findViewById(R.id.friend_name_tv);
            onlineIv = itemView.findViewById(R.id.online_iv);
            offlineIv = itemView.findViewById(R.id.offline_iv);

            itemView.setOnLongClickListener(v -> {
                openDialog(getAdapterPosition());
                return false;
            });

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, ChatActivity.class);
                User user = userList.get(getAdapterPosition());
                intent.putExtra(MyConstant.USER_KEY, user);
                mContext.startActivity(intent);
            });
        }
    }

    private void openDialog(int position) {
        User user = userList.get(position);
        String senderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String receiverId = user.get_id();
        String accessToken = DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN);

        // set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        String name = user.getFirstName() + " " + user.getLastName();
        builder.setTitle(name);
        String[] options = {"Xem thông tin", "Chăn người này", "Xóa bạn"};
        builder.setItems(options, (dialog, which) -> {
            switch (which){
                case 0:
                    viewInfo(receiverId, accessToken);
                    break;
                case 1:
                    CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tính năng này chưa được phát triển");
                    break;
                case 2:
                    openConfirmDialog(name, senderId, receiverId, accessToken, position);
                    break;

            }
        });
        builder.create().show();
    }

    private void viewInfo(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            viewInfo(id, accessToken);
                        }
                        else{
                            User user = response.body();
                            if (user != null) {
                                if(user.isDeleted()){
                                    CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tài khoản đã bị xóa");
                                }
                                else{
                                    Intent intent = new Intent(mContext, FriendProfileActivity.class);
                                    intent.putExtra(AddFriendActivity.USER_KEY, user);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mContext.startActivity(intent);
                                }
                            }

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void openConfirmDialog(String name, String senderId, String receiverId, String accessToken, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa bạn với " + name + " ?");
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Xóa", (dialog, which) -> deleteFriend(senderId, receiverId, "friend", accessToken, position));

        builder.create().show();
    }


    private void deleteFriend(String senderId, String receiverId, String option, String accessToken, int position) {
        RetrofitService.getInstance.deleteUserIDByOption(senderId, receiverId, option, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            SocketClient.getInstance().emit(MyConstant.CANCEL_FRIEND_REQUEST, receiverId);
                            userList.remove(position);
                            notifyItemRemoved(position);
                        } else if (response.code() == 500) {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, "Đã xảy ra lỗi");
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            deleteFriend(senderId, receiverId, option, accessToken, position);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }


}
