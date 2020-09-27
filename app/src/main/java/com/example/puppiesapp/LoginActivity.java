package com.example.puppiesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
EditText mtxt_emaill, mtxt_senhal;
TextView tv_esqueceuSenha;

Button mbtn_Logar;

    private FirebaseAuth mAuth;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mtxt_emaill = findViewById(R.id.txt_emaill);
        mtxt_senhal = findViewById(R.id.txt_senhal);
        mbtn_Logar = findViewById(R.id.btn_Logar);
         tv_esqueceuSenha= findViewById(R.id.esqueceuSenhaTv);
        mbtn_Logar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mtxt_emaill.getText().toString();
                String passw = mtxt_senhal.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mtxt_emaill.setError("Email Invalido");
                    mtxt_emaill. setFocusable(true);
                }else{
                    loginUser(email, passw);
                }
            }


        });

         tv_esqueceuSenha.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showEsqueceuSenha();
             }


         });

       pd =  new ProgressDialog(this);

    }

    private void showEsqueceuSenha() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //builder.setTitle("Recupere sua Senha");
       // LinearLayout linearLayout = new LinearLayout(this);
       View mView = getLayoutInflater().inflate(R.layout.dialog,null);


       final EditText emailEt = (EditText) mView.findViewById(R.id.dEmailEt) ;
       Button btnConfirma = (Button) mView.findViewById(R.id.recuperarSenhaBtn);
       emailEt.setHint("Email");
       emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

       emailEt.setMinEms(16);

       //linearLayout.addView(emailEt);
       //linearLayout.setPadding(10,10,10,10);

        btnConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                beginRecuperar(email);
            }
        });
       builder.setView(mView);
       /*builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
            String email = emailEt.getText().toString().trim();
            beginRecuperar(email);
           }


       });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/


        builder.create().show();
    }


    private void beginRecuperar(String email) {

        pd.setMessage("Enviando email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            pd.dismiss();
             if(task.isSuccessful()){
                 Toast.makeText(LoginActivity.this, "Email enviado", Toast.LENGTH_SHORT).show();
             }else{
                 Toast.makeText(LoginActivity.this, "Falhou", Toast.LENGTH_SHORT).show();
             }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String passw) {
        pd.setMessage("Entrando...");
      pd.show();
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                              if(task.getResult().getAdditionalUserInfo().isNewUser()){

                                  String email = user.getEmail();
                                  String uid = user.getUid();

                                  HashMap<Object,String> hashMap = new HashMap<>();
                                  hashMap.put("email", email);
                                  hashMap.put("uidemail", uid);
                                  hashMap.put("name", "");
                                  hashMap.put("onlineStatus","online");
                                  hashMap.put("phone", "");
                                  hashMap.put("image", "");
                                  hashMap.put("tipo_de_user","");
                                  // firebase database instance
                                  FirebaseDatabase database = FirebaseDatabase.getInstance();

                                  DatabaseReference reference = database.getReference("Users");

                                  reference.child(uid).setValue(hashMap);


                              }


                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();


                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void Voltar (View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}