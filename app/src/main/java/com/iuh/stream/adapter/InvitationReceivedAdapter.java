package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
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

public class InvitationReceivedAdapter extends RecyclerView.Adapter<InvitationReceivedAdapter.InvitationViewHolder> {
    private Context mContext;
    private List<User> userList;
    private FirebaseAuth mAuth;

    public InvitationReceivedAdapter(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_received,parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        User user = userList.get(position);
        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
        holder.name_tv.setText(user.getFirstName() + " " + user.getLastName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView name_tv;
        private Button removeBtn, acceptBtn;
        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            name_tv = itemView.findViewById(R.id.name_tv);
            removeBtn = itemView.findViewById(R.id.remove_btn);
            acceptBtn = itemView.findViewById(R.id.accept_btn);

            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String senderId = mAuth.getCurrentUser().getUid();
                    String receiverId =  userList.get(getAdapterPosition()).get_id();
                    final String OPTION = "friendRequest";
                    String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                    cancelFriendRequest(senderId, receiverId, OPTION, accessToken, getAdapterPosition());
                }
            });

            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Tính năng này chưa được phát triển");
                    Log.e("TAG", "addControls: " + DataLocalManager.getStringValue(Constants.ACCESS_TOKEN) );
                    Log.e("TAG", "sender: " + mAuth.getCurrentUser().getUid());
                    Log.e("TAG", "receiver: " + userList.get(getAdapterPosition()).get_id() );
                }
            });


        }
    }
    private void cancelFriendRequest(String senderId, String receiverId, String option, String accessToken, int position) {
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
                            cancelFriendRequest(senderId, receiverId, option, accessToken, position);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}


