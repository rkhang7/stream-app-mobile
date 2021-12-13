package com.iuh.stream.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iuh.stream.R;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.chat.Message;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.emitter.Emitter;

public class PersonalMessageAdapter extends RecyclerView.Adapter<PersonalMessageAdapter.PersonalMessageAdapterViewHolder>{
    private Context mContext;
    private List<Message> messageList;
    private String myId;
    private static final int LEFT_LINE = 1;
    private static final int RIGHT_LINE = 2;
    private String hisImageUrl;
    private String chatId;

    public PersonalMessageAdapter(Context mContext, String myId, String hisImageUrl) {
        this.mContext = mContext;
        this.myId = myId;
        this.hisImageUrl = hisImageUrl;

        // người khác đọc tin nhắn
    }

    public void setData(List<Message> messageList){
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public void setChatId(String chatId){
        this.chatId = chatId;
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
            holder.sentMessageTv.setVisibility(View.GONE);
            holder.receiveMessageTv.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setFocusable(false);

            // adapter
            PersonalLeftLineAdapter personalLeftLineAdapter = new PersonalLeftLineAdapter(mContext, hisImageUrl, message);
            personalLeftLineAdapter.setData(message.getLines());
            holder.recyclerView.setAdapter(personalLeftLineAdapter);

        }
        else{
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(linearLayoutManager);
            holder.recyclerView.setFocusable(false);
            // adapter
            PersonalRightLineAdapter personalRightLineAdapter = new PersonalRightLineAdapter(mContext, message.get_id(), chatId);
            personalRightLineAdapter.setData(message.getLines());
            holder.recyclerView.setAdapter(personalRightLineAdapter);
            if(position == messageList.size()-1){
                if(message.getSender().equals(myId)){
                    List<Line> lineList = message.getLines();
                    Line lastLine = lineList.get(lineList.size() - 1);
                    // is receiver
                    if(lastLine.isReceived()){
                        holder.receiveMessageTv.setVisibility(View.VISIBLE);
                        holder.sentMessageTv.setVisibility(View.GONE);
                    }
                    else{
                        holder.sentMessageTv.setVisibility(View.VISIBLE);
                        holder.receiveMessageTv.setVisibility(View.GONE);
                    }

                    if(lastLine.getReadedUsers() != null){
                        if(lastLine.getReadedUsers().size() > 0){
                            holder.sentMessageTv.setVisibility(View.GONE);
                            holder.receiveMessageTv.setVisibility(View.GONE);
                            holder.readMessageTv.setVisibility(View.VISIBLE);
                        }
                    }

                }
                else{
                    holder.sentMessageTv.setVisibility(View.GONE);
                    holder.receiveMessageTv.setVisibility(View.GONE);
                    holder.readMessageTv.setVisibility(View.VISIBLE);
                }

            }
            else {
                holder.sentMessageTv.setVisibility(View.GONE);
                holder.receiveMessageTv.setVisibility(View.GONE);
                holder.readMessageTv.setVisibility(View.GONE);
            }
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
        private TextView sentMessageTv, receiveMessageTv, readMessageTv;
        public PersonalMessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.message_rcv);
            sentMessageTv = itemView.findViewById(R.id.sent_message_tv);
            receiveMessageTv = itemView.findViewById(R.id.receive_message_tv);
            readMessageTv = itemView.findViewById(R.id.readed_message_tv);
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
