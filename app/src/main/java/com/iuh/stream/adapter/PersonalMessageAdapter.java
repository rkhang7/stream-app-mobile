package com.iuh.stream.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.chat.Message;

import java.util.List;

public class PersonalMessageAdapter extends RecyclerView.Adapter<PersonalMessageAdapter.PersonalMessageAdapterViewHolder>{
    private Context mContext;
    private List<Message> messageList;
    private String myId;
    private static final int LEFT_LINE = 1;
    private static final int RIGHT_LINE = 2;
    private String hisImageUrl;

    public PersonalMessageAdapter(Context mContext, String myId, String hisImageUrl) {
        this.mContext = mContext;
        this.myId = myId;
        this.hisImageUrl = hisImageUrl;
    }

    public void setData(List<Message> messageList){
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonalMessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new PersonalMessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalMessageAdapterViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(holder.getItemViewType() == LEFT_LINE){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setFocusable(false);

            // adapter
            PersonalLeftLineAdapter personalLeftLineAdapter = new PersonalLeftLineAdapter(hisImageUrl);
            personalLeftLineAdapter.setData(message.getLines());
            holder.recyclerView.setAdapter(personalLeftLineAdapter);
        }
        else{
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setFocusable(false);

            // adapter
            PersonalRightLineAdapter personalRightLineAdapter = new PersonalRightLineAdapter();
            personalRightLineAdapter.setData(message.getLines());
            holder.recyclerView.setAdapter(personalRightLineAdapter);
        }
    }

    @Override
    public int getItemCount() {
        if(messageList != null){
            return messageList.size();
        }
        return 0;
    }

    public class PersonalMessageAdapterViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        public PersonalMessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.message_rcv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(myId.equals(messageList.get(position).getSender())){
            return RIGHT_LINE;
        }
        else {
            return LEFT_LINE;
        }
    }
}
