package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddFriendActivity;
import com.iuh.stream.activity.FriendProfileActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.Contact;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final Context mContext;
    private List<Contact> contactList;
    private Socket mSocket;
    private FirebaseAuth mAuth;
    private static final String EVENT_REQUEST = "add-friend";
    private static final String EVENT_RESPONSE = "add-friend-res";
    private Button addFriendBtn, cancelFriend, acceptFriendBtn;
    private User currentUser;
    private TextView madeFriendTv;

    public ContactAdapter(Context mContext) {
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
        try {
            IO.Options mOptions = new IO.Options();
            mOptions.query = "uid=" + mAuth.getCurrentUser().getUid();
            mSocket = IO.socket(Constants.BASE_URL, mOptions);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.on(EVENT_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String result;
                        try {
                            result = data.getString("result");
                        } catch (JSONException e) {
                            return;
                        }
                        if(result.equals("Success")){
                            addFriendBtn.setVisibility(View.INVISIBLE);
                            cancelFriend.setVisibility(View.VISIBLE);
                        }
                        else {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                        }
                    }
                });
            }
        });


    }

    private void updateStatusFriendRequest(String id) {
        RetrofitService.getInstance.getMeInfo(DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(Constants.REFRESH_TOKEN);
                            updateStatusFriendRequest(id);
                        }
                        else{
                            currentUser = response.body();
                            List<String> listFriendInvitations = currentUser.getFriendInvitations();
                            List<String> listContact = currentUser.getContacts();
                            List<String> listFriendRequest = currentUser.getFriendRequests();
                            if(listFriendInvitations.contains(id)){
                                addFriendBtn.setVisibility(View.INVISIBLE);
                                cancelFriend.setVisibility(View.VISIBLE);
                                madeFriendTv.setVisibility(View.INVISIBLE);
                                acceptFriendBtn.setVisibility(View.INVISIBLE);
                            }
                            else if(listContact.contains(id)){
                                addFriendBtn.setVisibility(View.INVISIBLE);
                                cancelFriend.setVisibility(View.INVISIBLE);
                                madeFriendTv.setVisibility(View.VISIBLE);
                                acceptFriendBtn.setVisibility(View.INVISIBLE);
                            }
                            else if(listContact.contains(id)){
                                addFriendBtn.setVisibility(View.INVISIBLE);
                                cancelFriend.setVisibility(View.INVISIBLE);
                                madeFriendTv.setVisibility(View.INVISIBLE);
                                acceptFriendBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        CustomAlert.showToast((Activity)mContext , CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    public void setData(List<Contact> contactList){
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        if(contact != null){
            holder.phoneNameTv.setText(contact.getPhoneName());
            holder.streamNameTv.setText("Tên stream: " + contact.getFirstName() + " " + contact.getLastName());
            Glide.with(mContext).load(contact.getAvatar()).into(holder.avatarIv);
        }
        updateStatusFriendRequest(contact.getId());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView phoneNameTv;
        private final TextView streamNameTv;
        private String phoneNumber;
        private final CircleImageView avatarIv;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.contact_avatar_iv);
            phoneNameTv = itemView.findViewById(R.id.contact_name);
            streamNameTv = itemView.findViewById(R.id.contact_stream_name);
            addFriendBtn = itemView.findViewById(R.id.add_friend_btn);
            cancelFriend = itemView.findViewById(R.id.cancel_friend_request_btn);
            madeFriendTv = itemView.findViewById(R.id.made_friend_tv);
            acceptFriendBtn = itemView.findViewById(R.id.accept_friend_btn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = contactList.get(getAdapterPosition()).getId();
                    getUserForContact(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN));
                }
            });

            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // send event
                    String senderId = mAuth.getCurrentUser().getUid();
                    String receiverId = contactList.get(getAdapterPosition()).getId();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("senderID", senderId);
                        jsonObject.put("receiverID", receiverId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                    mSocket.emit(EVENT_REQUEST, jsonObject);
                }
            });

            cancelFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String senderId = mAuth.getCurrentUser().getUid();
                    String receiverId = contactList.get(getAdapterPosition()).getId();
                    final String OPTION = "friendInvitation";
                    String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                    cancelFriendInvitation(senderId, receiverId, OPTION, accessToken);
                }
            });

            acceptFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String accessToken = DataLocalManager.getStringValue(Constants.ACCESS_TOKEN);
                    String receiverId = contactList.get(getAdapterPosition()).getId();
                    acceptFriend(receiverId, accessToken);
                }
            });
        }
    }

    private void acceptFriend(String receiverId, String accessToken) {
        RetrofitService.getInstance.acceptFriendRequest(receiverId, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            acceptFriend(receiverId, accessToken);
                        }
                        else if(response.code() == 404){
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, "Không tìm thấy người dùng");
                        }
                        else if(response.code() == 500){
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                        }
                        else {
                            addFriendBtn.setVisibility(View.INVISIBLE);
                            cancelFriend.setVisibility(View.INVISIBLE);
                            madeFriendTv.setVisibility(View.VISIBLE);
                            acceptFriendBtn.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast((Activity)mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void cancelFriendInvitation(String senderId, String receiverId, String option, String accessToken) {
        RetrofitService.getInstance.deleteUserIDByOption(senderId, receiverId, option, accessToken)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            addFriendBtn.setVisibility(View.VISIBLE);
                            cancelFriend.setVisibility(View.INVISIBLE);
                        } else if (response.code() == 500) {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                        } else if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                            cancelFriendInvitation(senderId, receiverId, option, accessToken);
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    private void getUserForContact(String id, String accessToken) {
        RetrofitService.getInstance.getUserById(id, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(Constants.REFRESH_TOKEN));
                           getUserForContact(id, accessToken);
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
}
