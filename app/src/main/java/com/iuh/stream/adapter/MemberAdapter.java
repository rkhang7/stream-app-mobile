package com.iuh.stream.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.User;
import com.iuh.stream.models.request.RemoveMemberRequest;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder>{
    private Context mContext;
    private List<User> memberList;
    private List<String> adminIdList;
    private String chatId;
    private FirebaseAuth mAuth;

    public MemberAdapter(Context mContext, List<User> memberList, List<String> adminIdList, String chatId) {
        this.mContext = mContext;
        this.memberList = memberList;
        this.adminIdList = adminIdList;
        this.chatId  = chatId;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_in_group_row, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = memberList.get(position);
        if(member != null){
            Picasso.get().load(member.getImageURL()).into(holder.memberAvatarIv);
            holder.memberNameTv.setText(member.getFirstName() + " " + member.getLastName());

            // is admin
            if(checkIsAdmin(member.get_id())){
                holder.adminTv.setVisibility(View.VISIBLE);
            }
            else{
                holder.adminTv.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if(memberList != null){
            return memberList.size();
        }
        return 0;
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView memberAvatarIv;
        private TextView memberNameTv;
        private TextView adminTv;
        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberAvatarIv = itemView.findViewById(R.id.member_avatar_iv);
            memberNameTv = itemView.findViewById(R.id.member_name_tv);
            adminTv = itemView.findViewById(R.id.admin_tv);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String myId = mAuth.getCurrentUser().getUid();
                    User member = memberList.get(getAdapterPosition());
                    // admin list include me
                    if(adminIdList.contains(myId)){
                        // do not allow remove me
                        if (!member.get_id().equals(myId)){
                            RemoveMemberRequest removeMemberRequest = new RemoveMemberRequest(chatId, member.get_id());
                            showBottomSheetDialog(removeMemberRequest, getAdapterPosition());
                        }
                    }


                    return false;
                }
            });
        }
    }

    private void showBottomSheetDialog(RemoveMemberRequest removeMemberRequest, int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.modal_bottom_sheet);


        // init views;
        Button deletePostBtn = bottomSheetDialog.findViewById(R.id.delete_member_btn);

        // handle button delete clicked
        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConfirmDialog(removeMemberRequest, position, bottomSheetDialog);
            }
        });

        bottomSheetDialog.show();
    }

    private void openConfirmDialog(RemoveMemberRequest removeMemberRequest, int position, BottomSheetDialog bottomSheetDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Xóa thành viên này ? ");
        builder.setIcon(R.drawable.icons8_warning_64px);
        builder.setMessage("Bạn có chắc muốn xóa thành viên này khỏi nhóm ?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeMember(removeMemberRequest, position, bottomSheetDialog);
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });




        builder.create().show();
    }

    private void removeMember(RemoveMemberRequest removeMemberRequest, int position, BottomSheetDialog bottomSheetDialog) {
        RetrofitService.getInstance.removeMember(removeMemberRequest, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            removeMember(removeMemberRequest, position, bottomSheetDialog);
                        }
                        else if(response.code() == 200){
                            bottomSheetDialog.dismiss();
                            memberList.remove(position);
                            notifyItemRemoved(position);
                            SocketClient.getInstance().emit(MyConstant.GROUP_NOTIFICATION);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private boolean checkIsAdmin(String id){
        for (String s: adminIdList){
            if(id.equals(s)){
                return true;
            }
        }
        return false;
    }


}
