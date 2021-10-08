package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.Contact;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private Context mContext;
    private List<Contact> contactList;

    public ContactAdapter(Context mContext) {
        this.mContext = mContext;
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
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView phoneNameTv;
        private TextView streamNameTv;
        private String phoneNumber;
        private CircleImageView avatarIv;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneNameTv = itemView.findViewById(R.id.contact_name);
            streamNameTv = itemView.findViewById(R.id.contact_stream_name);
            avatarIv = itemView.findViewById(R.id.contact_avatar_iv);
        }
    }
}
