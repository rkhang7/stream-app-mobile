package com.iuh.stream.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddFriendActivity;
import com.iuh.stream.activity.FriendProfileActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FiendsViewHolder>{
    private Context mContext;
    private List<User> userList;
    private FirebaseAuth mAuth;

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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    openDialog(getAdapterPosition());
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText((Activity)mContext, "Click", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void openDialog(int position) {
        User user = userList.get(position);
        String senderId = mAuth.getCurrentUser().getUid();
        String receiverId = user.get_id();
        String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
        final String OPTION = "friend";

        // set up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        String name = user.getFirstName() + " " + user.getLastName();
        builder.setTitle(name);
        String[] options = {"Xem thông tin", "Chăn người này", "Xóa bạn"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        viewInfo(receiverId, accessToken);
                        break;
                    case 1:
                        CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tính năng này chưa được phát triển");
                        break;
                    case 2:
                        openConfirmDialog(name, senderId, receiverId, OPTION, accessToken, position);
                        break;

                }
            }
        });
        builder.create().show();
    }

    private void viewInfo(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            viewInfo(id, accessToken);
                        }
                        else{
                            User user = response.body();
                            Intent intent = new Intent(mContext, FriendProfileActivity.class);
                            intent.putExtra(AddFriendActivity.USER_KEY, user);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            mContext.startActivity(intent);
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void openConfirmDialog(String name, String senderId, String receiverId, String option, String accessToken, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa bạn với " + name + " ?");
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend(senderId, receiverId, option, accessToken, position);
            }
        });

        builder.create().show();
    }


    private void deleteFriend(String senderId, String receiverId, String option, String accessToken, int position) {
        RetrofitService.getInstance.deleteUserIDByOption(senderId, receiverId, option, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            userList.remove(position);
                            notifyItemRemoved(position);
                        } else if (response.code() == 500) {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, "Đã xảy ra lỗi");
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            deleteFriend(senderId, receiverId, option, accessToken, position);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }


}
