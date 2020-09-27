package com.example.puppiesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppiesapp.ChatActivity;
import com.example.puppiesapp.R;
import com.example.puppiesapp.models.ModelChat;
import com.example.puppiesapp.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder> {

    Context context;
    List<ModelUser> userList;
    private HashMap<String,String> lastMessageMap;

    public AdapterChatlist(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUid = userList.get(position).getUidemail();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.nameTv.setText(userName);
       /* if (lastMessage!=null || lastMessage.equals("default") ){
            holder.lastMessageTv.setVisibility(View.GONE);
        }else{
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);


        }*/

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_profile_black).into(holder.profileIv);

        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_profile_black).into(holder.profileIv);

        }

        if (userList.get(position).getOnlineStatus().equals("online")){
                   holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }else{
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);

        }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("hisUid",hisUid);
            context.startActivity(intent);
        }
    });


    }

    public  void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId,lastMessage);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIv,onlineStatusIv;
        TextView nameTv, lastMessageTv;


        public MyHolder (@NonNull View itemView){
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv= itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
        }
    }

}
