package com.example.puppiesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.regex.Pattern;

public class Cadastro extends AppCompatActivity {
    EditText mtxt_emailc, mtxt_senhac, mtxt_usuarioc, mtxt_phonec,mtxt_tipodeuserc;
    Button mbtn_Cadastrar;

    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        mtxt_emailc= findViewById(R.id.txt_emailc);
        mtxt_senhac = findViewById(R.id.txt_senhac);
        mtxt_usuarioc= findViewById(R.id.txt_usuarioc);
        mtxt_phonec = findViewById(R.id.txt_phonec);
        mtxt_tipodeuserc = findViewById(R.id.txt_tipodeuserc);
        mbtn_Cadastrar = findViewById(R.id.btn_Cadastrar);
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cadastrando...");

       mbtn_Cadastrar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String email = mtxt_emailc.getText().toString().trim();
               String tipodeuser = mtxt_tipodeuserc.getText().toString().trim();
               String password= mtxt_senhac.getText().toString().trim();
               String name = mtxt_usuarioc.getText().toString().trim();
               String phone = mtxt_phonec.getText().toString().trim();
               if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                     mtxt_emailc.setError("Email inválido");
                     mtxt_emailc.setFocusable(true);
               }
               else if (password.length()<6)
               {
                   mtxt_senhac.setError("A senha precisa ser maior que 6 caracteres");
                   mtxt_senhac.setFocusable(true);
               }

               else {
                   registerUser (email,password,name,phone,tipodeuser);
               }
           }


       });
    }
    private void registerUser(String email, String password, final String name, final String phone, final String tipodeuser) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                     progressDialog.dismiss();

                    FirebaseUser user = mAuth.getCurrentUser();

                    String email = user.getEmail();
                    String uid = user.getUid();

                    HashMap<Object,String>hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uidemail", uid);
                    hashMap.put("name",name);
                    hashMap.put("onlineStatus","online");
                    hashMap.put("phone", phone);
                    hashMap.put("image","" );
                    hashMap.put("tipo_de_user", tipodeuser);
                    hashMap.put("cover","" );
                    // firebase database instance
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference reference = database.getReference("Users");

                      reference.child(uid).setValue(hashMap);

                    Toast.makeText(Cadastro.this,"Registrando...\n"+user.getEmail(),Toast.LENGTH_SHORT);
                    startActivity(new Intent(Cadastro.this,LoginActivity.class));
                    finish();

                } else {
                    // If sign in fails, display a message to the user.
progressDialog.dismiss();
                    Toast.makeText(Cadastro.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Cadastro.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Voltar (View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}