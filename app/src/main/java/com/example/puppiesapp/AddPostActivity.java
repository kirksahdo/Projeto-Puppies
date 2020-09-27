package com.example.puppiesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
     DatabaseReference userDbRef;

    private  static  final  int CAMERA_REQUEST_CODE = 100;
    private  static  final  int STORAGE_REQUEST_CODE = 200;
    private  static  final  int  IMAGE_PICK_CAMERA_CODE = 300;
    private  static  final  int  IMAGE_PICK_GALLERY_CODE = 400;


    String[] cameraPermissions;
    String[]  storagePermissions;

    EditText nome_do_animalEt, racaEt;
    RadioGroup radioGrouptipo,radioGroupsexo, radioGroupcor;
    RadioButton radioButton,caoRb, gatoRb, femeaRb, machoRb, claroRb, escuroRb, mesticoRb;
    ImageButton uploadBtn;
    ImageView pimageIv;
    ImageButton voltarbtn;

    String name , email, uid, dp;
    Uri image_uri = null;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();

          userDbRef = FirebaseDatabase.getInstance().getReference("Users");
          Query query = userDbRef.orderByChild("email").equalTo(email);
          query.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for(DataSnapshot ds: snapshot.getChildren()){
                           name = ""+ ds.child("name").getValue();
                           email = ""+ ds.child("email").getValue();
                           dp= ""+ ds.child("image").getValue();

                       }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
        nome_do_animalEt = findViewById(R.id.nome_animalET);
        racaEt = findViewById(R.id.racaET);
        radioGrouptipo = findViewById(R.id.radioGrouptipo);
        radioGroupcor = findViewById(R.id.radioGroupcor);
        radioGroupsexo = findViewById(R.id.radioGroupsexo);

        caoRb = findViewById(R.id.caoRB);
        gatoRb = findViewById(R.id.gatoRB);
        femeaRb = findViewById(R.id.femeaRB);
        machoRb = findViewById(R.id.machoRB);
        claroRb = findViewById(R.id.claroRB);
        escuroRb = findViewById(R.id.escuroRB);
        mesticoRb = findViewById(R.id.mesticaRB);
        uploadBtn = findViewById(R.id.btn_postar);
        pimageIv = findViewById(R.id.pImageIV);
        voltarbtn = findViewById(R.id.pbtn_voltar);

        pimageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

voltarbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(AddPostActivity.this, DashboardActivity.class));
        finish();
    }
});


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome_do_animal = nome_do_animalEt.getText().toString().trim();
                String raca = racaEt.getText().toString().trim();
                if(TextUtils.isEmpty(nome_do_animal)){
                    Toast.makeText(AddPostActivity.this, "Enter nome ...", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(TextUtils.isEmpty(raca)){
                    Toast.makeText(AddPostActivity.this, "Inserir raça ...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(image_uri==null)
                {
                    uploadData(nome_do_animal,raca,"noImage");
                }else{
                    uploadData(nome_do_animal,raca,String.valueOf(image_uri));


                }

            }


        });

    }
    private void uploadData(final String nome_do_animal, final String raca, String uri) {
           pd.setMessage("Publicando animal");
           pd.show();
           final String  timeStamp = String.valueOf(System.currentTimeMillis());
           String filePathAndName = "Posts/" +"post_" + timeStamp;

           if(!uri.equals("noImage")){
               StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
               ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isSuccessful());
                       String downloadUri = uriTask.getResult().toString();
                       if(uriTask.isSuccessful()){
                           HashMap<Object,String> hashMap = new HashMap<>();

                           hashMap.put("uid",uid);
                           hashMap.put("uName",name);
                           hashMap.put("uEmail",email);
                           hashMap.put("uDp",dp);
                           hashMap.put("pId",timeStamp);
                           hashMap.put("pNome_do_animal",nome_do_animal);
                           hashMap.put("uRaca",raca);
                           hashMap.put("pImage",downloadUri);
                           hashMap.put("pTime",timeStamp);

                           DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                           ref.child(timeStamp).setValue(hashMap)
                                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           pd.dismiss();
                                           Toast.makeText(AddPostActivity.this, "Post publicado", Toast.LENGTH_SHORT).show();

                                           nome_do_animalEt.setText("");
                                           racaEt.setText("");
                                           pimageIv.setImageURI(null);
                                           image_uri = null;
                                       }
                                   })
                                   .addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           pd.dismiss();
                                           Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                       }
                                   });






                       }

                   }
               })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {

                               pd.dismiss();
                               Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                           }
                       });
           }
           else{
               HashMap<Object,String> hashMap = new HashMap<>();

               hashMap.put("uid",uid);
               hashMap.put("uName",name);
               hashMap.put("uEmail",email);
               hashMap.put("uDp",dp);
               hashMap.put("pId",timeStamp);
               hashMap.put("pNome_do_animal",nome_do_animal);
               hashMap.put("pRaca",raca);
               hashMap.put("pImage","noImage");
               hashMap.put("pTime",timeStamp);

               DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
               ref.child(timeStamp).setValue(hashMap)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               pd.dismiss();
                               Toast.makeText(AddPostActivity.this, "Post publicado", Toast.LENGTH_SHORT).show();
                               nome_do_animalEt.setText("");
                               racaEt.setText("");
                               pimageIv.setImageURI(null);
                               image_uri = null;
                           }
                       })
                       .addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               pd.dismiss();
                               Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                           }
                       });
           }

    }
    private void showImagePickDialog() {
        String[] option = {"Camera","Galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha imagem da:");

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0)
                {
                   if(!checkCameraePermission()){
                       requestCameraPermission();
                   }else{
                       pickFromCamera();
                   }
                }if(i==1){
                       if(!checkStoragePermission()){
                           requestStoragePermission();
                       }else{
                           pickFromGallery();
                       }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");

        cv.put(MediaStore.Images.Media.TITLE,"Temp Descr");
         image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);


    }
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);


    }

    private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void  requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private  boolean checkCameraePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean resultl = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultl;
    }

    private void  requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // mperfilTv.setText(user.getEmail());
            email = user.getEmail();
            uid = user.getUid();



        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();

                    }else{

                        Toast.makeText(this, "Camera && Storage both permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }

                }else{

                }
            }break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFromGallery();

                    }else{

                        Toast.makeText(this, "Storage permissions are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }

            }
             break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                pimageIv.setImageURI(image_uri);

            }
              else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                  pimageIv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}