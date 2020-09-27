package com.example.puppiesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.puppiesapp.ChatActivity;
import com.example.puppiesapp.R;
import com.example.puppiesapp.ThereProfileActivity;
import com.example.puppiesapp.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
     Context context;
     List<ModelPost> postList;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = postList.get(position).getUid();
        final String uid = postList.get(position).getUid();
        String uEmail = postList.get(position).getuEmail();
        String uName = postList.get(position).getuName();
        String uDp = postList.get(position).getuDp();
        String pId = postList.get(position).getpId();
        String pNome_do_animal = postList.get(position).getpNome_do_animal();
        String uRaca = postList.get(position).getuRaca();
        String pImage = postList.get(position).getpImage();
        String pTimeStamp= postList.get(position).getpTime();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();


        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pNome_do_animalTv.setText(pNome_do_animal);
        holder.pRacaTv.setText("Raça:"+uRaca);




        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_launcher_foreground).into(holder.uPictureIv);

        }catch (Exception e){

        }
             if(pImage.equals("noImage")){

                 holder.pImageIv.setVisibility(View.GONE);

             }else{
                 try {
                     Picasso.get().load(pImage).into(holder.pImageIv);

                 }catch (Exception e){

                 }
             }


        holder.adotarBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUID);
                context.startActivity(intent);
            }
        });

holder.profileLayout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {



                Intent intent  = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uidemail",uid);
                context.startActivity(intent);


    }
});

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class  MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pNome_do_animalTv, pRacaTv;
        Button adotarBnt;
        LinearLayout profileLayout;

        public  MyHolder(@NonNull View itemView){
            super(itemView);
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIV);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pNome_do_animalTv = itemView.findViewById(R.id.pNome_animalTV);
           pRacaTv = itemView.findViewById(R.id.pRacaTV);
            adotarBnt =itemView.findViewById(R.id.quero_adotarBtn);
            profileLayout =itemView.findViewById(R.id.profileLayout);


        }
    }
}
