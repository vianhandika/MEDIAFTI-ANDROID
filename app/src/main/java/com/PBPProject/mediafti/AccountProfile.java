package com.PBPProject.mediafti;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AccountProfile extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button btnUpdate;
    private EditText textUsername,textEmail,textPassword;
    private TextView txtResetPassword;
    private ImageView profile_image,img;
    private String uid;

    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap,selectImage;

    Uri uriProfileImage;
    String profileImageUrl;
    ProgressDialog progressDialog ;


    View profile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profile= inflater.inflate(R.layout.account_profile,container,false);

        setinit();
        loadprofileinformation();
        requestStoragePermission();

        onClickImage();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        txtResetPassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new ChangePassword());
                transaction.commit();
            }
        } );
        return profile;
    }

    public void onClickImage(){
        img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }

    private void setinit(){
        btnUpdate= profile.findViewById(R.id.btnUpdate);
        textUsername=profile.findViewById(R.id.textUsername);
        textEmail=profile.findViewById(R.id.textEmail);
        textPassword=profile.findViewById(R.id.textPassword);
        profile_image=profile.findViewById(R.id.profile_image);
        txtResetPassword=profile.findViewById( R.id.txtResetPassword );
        img=profile.findViewById(R.id.btnImgProfile);
        progressDialog = new ProgressDialog(getContext());
    }

    public void changeUsername() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        final String username = textUsername.getText().toString();

        if (uriProfileImage != null) {

            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference folderRef = storageReference.child("Profile/"+mUser.getUid().toString()+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data =baos.toByteArray();
            final UploadTask uploadTask = folderRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Failed Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    String imageulr = folderRef.child("Profile/"+mUser.getUid().toString()+".jpg").getName().toString();


                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("username").setValue(username)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(profile.getContext(),"Success Update",Toast.LENGTH_SHORT).show();

//                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                        transaction.add(R.id.framelay, new AccountProfile());
//                                        transaction.commit();
                                        Intent intent = new Intent(getActivity(),Dashboard.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(profile.getContext(),"Failed Update",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    Toast.makeText(getContext(),"Success Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }else {

            Toast.makeText(getContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }




    }

    private void loadprofileinformation(){

        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        uid= mUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference("Users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child(uid).child("username").getValue(String.class);
                String password=dataSnapshot.child(uid).child("password").getValue(String.class);
                String email=dataSnapshot.child(uid).child("email").getValue(String.class);


                StorageReference load = FirebaseStorage.getInstance().getReference("Profile/");
//            Picasso.with(ctx).load(uri.toString()).into(eventImage);

                load.child(uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.with(getContext()).load(uri.toString()).into(profile_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
                textUsername.setText(username);
                textEmail.setText(email);
                textPassword.setText(password);

                //textUsername.setFocusable(false);
                textEmail.setEnabled(false);
                textEmail.setKeyListener(null);
                textPassword.setEnabled(false);
                textPassword.setKeyListener(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Failed Load From Database",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;
        ActivityCompat.requestPermissions( getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriProfileImage);
                profile_image.setImageBitmap(bitmap);
                selectImage=bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

}
