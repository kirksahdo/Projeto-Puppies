package com.example.puppiesapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puppiesapp.adapters.AdapterPosts;
import com.example.puppiesapp.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
   FirebaseAuth firebaseAuth;
   FirebaseUser user;
   FirebaseDatabase firebaseDatabase;
   DatabaseReference databaseReference;

   StorageReference storageReference;

   String storagePath = "Users_Profile_Imgs/";

   ImageView avatarTv,coverIv;
   TextView userTv, emailTv,tipouserTv,phoneTv;
   FloatingActionButton fab;
   RecyclerView postsRecyclerView;

ProgressDialog pd;



private  static  final int CAMERA_REQUEST_CODE = 100;
    private  static  final int STORAGE_REQUEST_CODE = 200;
    private  static  final int IMAGE_PICK_GALLERY_CODE = 300;
    private  static  final int IMAGE_PICK_CAMERA_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;





    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Uri image_uri;

    String profilePhoto;

    public PerfilFragment() {
        // Required empty public constructor
    }


    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_perfil, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
     firebaseDatabase = FirebaseDatabase.getInstance();
     databaseReference = firebaseDatabase.getReference("Users");
     storageReference = getInstance().getReference();


     cameraPermissions = new  String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
     storagePermissions = new  String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        avatarTv = view.findViewById(R.id.avatarIv);
        userTv = view.findViewById(R.id.userTv);
        tipouserTv = view.findViewById(R.id.tipouserTv);
         fab = view.findViewById(R.id.fab);
        postsRecyclerView = view.findViewById(R.id.recyclerview_posts);


        pd = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for (DataSnapshot ds:dataSnapshot.getChildren() ){
                  String user = ""+ds.child("name").getValue();
                  String tipouser = ""+ds.child("tipo_de_user").getValue();
                  String image = ""+ds.child("image").getValue();

                  userTv.setText(user);
                  tipouserTv.setText(tipouser);

                  try{
                      Picasso.get().load(image).into(avatarTv);
                  }catch (Exception e)
                  {
                      Picasso.get().load(R.drawable.ic_addphoto).into(avatarTv);
                  }


              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfile();
            }


        });
        postList = new ArrayList<>();

        checkUserStatus();
        loadMyPosts();

        return view;
    }

    private void loadMyPosts() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPost myPosts = ds.getValue(ModelPost.class);
                    
                    postList.add(myPosts);
                    
                    adapterPosts = new AdapterPosts(getActivity(),postList);
                    
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private  boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void  requestStoragePermission(){
       requestPermissions( storagePermissions,STORAGE_REQUEST_CODE);
    }


    private  boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void  requestCameraPermission(){
       requestPermissions( cameraPermissions,CAMERA_REQUEST_CODE);
    }



    private void showEditProfile() {
        String options[]= {"Editar foto de perfil","Editar nome de usuário","Editar descrição"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Editar");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
               pd.setMessage("Atualizando foto de perfil");
               profilePhoto = "image";
              showImagePicDialog();
                }else if (i ==1)
                {
                    pd.setMessage("Atualizando nome de usuário");
                    showNameDescriUpdateDialog("name");
                }else if (i ==2)
                {
                    pd.setMessage("Atualizando descrição");
                    showNameDescriUpdateDialog("tipo_de_user");

                }
            }


        });

        builder.create().show();

    }

    private void showNameDescriUpdateDialog(final String key) {
     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
     builder.setTitle("Atualizar"+key);
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter"+key);
        linearLayout.addView(editText);


        builder.setView(linearLayout);

        builder.setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                         final String value = editText.getText().toString().trim();
                         if (!TextUtils.isEmpty(value)){
                                  pd.show();
                                  HashMap<String,Object> result = new HashMap<>();
                                   result.put(key,value);
                                   databaseReference.child(user.getUid()).updateChildren(result)
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                    pd.dismiss();
                                                   Toast.makeText(getActivity(), "Atualizado", Toast.LENGTH_SHORT).show();
                                               }
                                           })
                                           .addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   pd.dismiss();
                                                   Toast.makeText(getActivity(), "falhou", Toast.LENGTH_SHORT).show();
                                               }
                                           });

                                   if(key.equals("name")){
                                       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                       Query query = ref.orderByChild("uid").equalTo(uid);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                      for (DataSnapshot ds:snapshot.getChildren()){
                                                          String child = ds.getKey();
                                                          snapshot.getRef().child(child).child("uName").setValue(value);
                                                      }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                   }



                         }else{
                             Toast.makeText(getActivity(), "Por favor insira"+key, Toast.LENGTH_SHORT).show();

                         }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();

    }

    private void showImagePicDialog() {

        String options[]= {"Câmera","Galeria"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Escolher a imagem de");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    if (!checkCameraPermission()){
                            requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }else if (i ==1)
                {
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }

                }
            }


        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
            {
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&& writeStorageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(getActivity(), "Sem permissão", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if ( writeStorageAccepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(getActivity(), "Sem permissão", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();

                 uploadProfileCoverPhoto(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                uploadProfileCoverPhoto(image_uri);


            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {

       pd.show();
        String filePathAndName  = storagePath+""+profilePhoto+"_"+ user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                    final Uri downloadUri = uriTask.getResult();

                    if (uriTask.isSuccessful()){

                        HashMap<String, Object> results = new HashMap<>();






                        results.put(profilePhoto, downloadUri.toString());

                        databaseReference.child(user.getUid()).updateChildren(results)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                     pd.dismiss();
                                        Toast.makeText(getActivity(), "Image Atualizada", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                        Toast.makeText(getActivity(), "Erro ao atualizar imagem", Toast.LENGTH_SHORT).show();

                                    }
                                });


                        if(profilePhoto.equals("image")){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                            Query query = ref.orderByChild("uid").equalTo(uid);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds:snapshot.getChildren()){
                                        String child = ds.getKey();
                                        snapshot.getRef().child(child).child("uDp").setValue(downloadUri);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }



                    }else{
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
                    }


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Falhou", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();

        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");

        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){

            uid = user.getUid();

        }else{
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }
}