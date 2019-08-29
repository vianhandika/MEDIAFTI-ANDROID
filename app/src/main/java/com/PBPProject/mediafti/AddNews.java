package com.PBPProject.mediafti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddNews extends Fragment {

    EditText textDesc,textTitle,textFrom,textPicture;
    Button btnAddNews;
    FrameLayout framelay;
    View addNews;

    Uri FilePathUri,FilePathUri2;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    int Image_Request_Code = 1;
    ProgressDialog progressDialog ;
    ImageView SelectImage;
    String Storage_Path = "Event/";
    public Bitmap selectImage;
    private static final int STORAGE_PERMISSION_CODE = 123;

    public static final String Database_Path = "Event_Database";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addNews= inflater.inflate(R.layout.add_news,container,false);
        init();

        btnAddNews=addNews.findViewById(R.id.btnAddNews);

        SelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        btnAddNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImageFileToFirebaseStorage();
            }
        });

        return addNews;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();


            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(addNews.getContext().getContentResolver(), FilePathUri);
                SelectImage.setImageBitmap(bitmap);
                selectImage=bitmap;

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public void UploadImageFileToFirebaseStorage() {


        final String desc=textDesc.getText().toString(),
                title=textTitle.getText().toString(),
                from=textFrom.getText().toString();


        final String idNews = FirebaseDatabase.getInstance().getReference().push().getKey();


        if (FilePathUri != null) {

            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference folderRef = storageReference.child("News/"+idNews+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data =baos.toByteArray();
            final UploadTask uploadTask = folderRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addNews.getContext(),"Failed Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    String imageulr = folderRef.child("Event/"+idNews+".jpg").getName().toString();

                    if(formChecking()==0) {

                        NewsDAO news = new NewsDAO(title, desc, imageulr, from);
                        FirebaseDatabase.getInstance().getReference("News")
                                .child(idNews).setValue(news)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            Toast.makeText(getActivity(), "Add Success", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                    Toast.makeText(addNews.getContext(),"Success Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }else {

            Toast.makeText(addNews.getContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }

    private void init(){
        textDesc=addNews.findViewById(R.id.textDesc);
        textTitle=addNews.findViewById(R.id.textTitle);
        textFrom=addNews.findViewById(R.id.textFrom);
        SelectImage=addNews.findViewById(R.id.SelectImage);
        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(addNews.getContext());

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void addNews(){
        final String desc=textDesc.getText().toString(),
                title=textTitle.getText().toString(),
                from=textFrom.getText().toString(),
                image=textPicture.getText().toString();
//        final int maxPost=100;
        final int startIndex = (int) (10000 * Math.random()) + 1;;

        if(formChecking()==0) {

            NewsDAO news = new NewsDAO(title, desc, image, from);
            FirebaseDatabase.getInstance().getReference("News")
                    .child(Integer.toString(startIndex)).setValue(news)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

//                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                transaction.add(R.id.framelay, new AdminDashboard());
//                                transaction.commit();
                                textDesc.setText("");
                                textTitle.setText("");
                                textFrom.setText("");
                                textPicture.setText("");

                                Toast.makeText(getActivity(), "Add Success", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private int formChecking(){
        //Fungsi Check Form

        String desc=textDesc.getText().toString(),
                title=textTitle.getText().toString(),
                from=textFrom.getText().toString();

        if(from.isEmpty()){
            textFrom.setError("Sender Name is Required");
            textFrom.requestFocus();
            return 1;
        }

        if(title.isEmpty()){
            textTitle.setError("Title is Required");
            textTitle.requestFocus();
            return 1;
        }

        if(desc.isEmpty()){
            textDesc.setError("Desc is Required");
            textDesc.requestFocus();
            return 1;
        }






        return 0;
    }

}
