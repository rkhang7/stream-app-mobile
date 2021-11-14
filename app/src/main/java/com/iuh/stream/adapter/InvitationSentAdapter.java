package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
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
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationSentAdapter extends RecyclerView.Adapter<InvitationSentAdapter.InvitationViewHolder> {
    private final Context mContext;
    private List<User> userList;
    private final FirebaseAuth mAuth;

    public InvitationSentAdapter(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    public void setData(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_sent, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        User user = userList.get(position);
        Picasso.get().load(user.getImageURL()).into(holder.avatarIv);
        holder.name_tv.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class InvitationViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView avatarIv;
        private final TextView name_tv;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.avatar_iv);
            name_tv = itemView.findViewById(R.id.name_tv);
            Button removeBtn = itemView.findViewById(R.id.remove_sent_btn);

            removeBtn.setOnClickListener(v -> {
                String senderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                String receiverId = userList.get(getAdapterPosition()).get_id();
                final String OPTION = "friendInvitation";
                String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                cancelFriendInvitation(senderId, receiverId, OPTION, accessToken, getAdapterPosition());
            });
        }


    }

    private void cancelFriendInvitation(String senderId, String receiverId, String option, String accessToken, int position) {
        RetrofitService.getInstance.deleteUserIDByOption(senderId, receiverId, option, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            SocketClient.getInstance().emit(Constants.CANCEL_FRIEND_INV_REQUEST, receiverId);
                            userList.remove(position);
                            notifyItemRemoved(position);
                        } else if (response.code() == 500) {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, "Đã xảy ra lỗi");
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            cancelFriendInvitation(senderId, receiverId, option, accessToken, position);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}


