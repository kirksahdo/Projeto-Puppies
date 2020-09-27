package com.example.puppiesapp.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppiesapp.R;
import com.example.puppiesapp.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context ;
    List<ModelChat>chatList;
    String imageUri;

    FirebaseUser fUser;
    public AdapterChat(Context context, List<ModelChat> chatList, String imageUri) {
        this.context = context;
        this.chatList = chatList;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType == MSG_TYPE_RIGHT){
           View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent,false);
           return new MyHolder(view);

       }else{
           View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent,false);
           return new MyHolder(view);

       }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

       // Calendar cal = Calendar.getInstance(Locale.ENGLISH);
       // cal.setTimeInMillis(Long.parseLong(timeStamp));
       // String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

    holder.messageTv.setText(message);
   // holder.timeTv.setText(dateTime);


    if(position == chatList.size() - 1){
      if (chatList.get(position).isSeen()){
          holder.isSeen.setText("Visto");

      }else{

          holder.isSeen.setText("Entregue");

      }
    }else{
         holder.isSeen.setVisibility(View.GONE);
    }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return  MSG_TYPE_RIGHT;
        }else
        {
            return MSG_TYPE_LEFT;

        }
    }

    class MyHolder extends RecyclerView.ViewHolder{
    TextView messageTv, timeTv,isSeen;


    public MyHolder(@NonNull View itemView) {
        super(itemView);

        messageTv = itemView.findViewById(R.id.messageTv);

        isSeen = itemView.findViewById(R.id.isSeenTv);

    }
}

}
