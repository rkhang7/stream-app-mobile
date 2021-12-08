package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.User;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder>{
    private Context mContext;
    private List<User> memberList;
    private List<String> adminIdList;

    public MemberAdapter(Context mContext, List<User> memberList, List<String> adminIdList) {
        this.mContext = mContext;
        this.memberList = memberList;
        this.adminIdList = adminIdList;
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
        }
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
