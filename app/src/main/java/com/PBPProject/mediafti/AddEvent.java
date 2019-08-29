package com.PBPProject.mediafti;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.orm.query.Select;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;


import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;


import static android.app.Activity.RESULT_OK;

public class AddEvent extends Fragment {
    private static final String TAG="AddEvent";
    EditText textPlace,textTitle,textDate,textPicture;
    Button btnAddEvent;
    FrameLayout framelay;
    View addEvent;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    Uri FilePathUri,FilePathUri2;


    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    int Image_Request_Code = 1;
    ProgressDialog progressDialog ;
    ImageView SelectImage,imgTgl;
    String Storage_Path = "Event/";
    public Bitmap selectImage;
    private static final int STORAGE_PERMISSION_CODE = 123;

    public static final String Database_Path = "Event_Database";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addEvent= inflater.inflate(R.layout.add_event,container,false);

        init();

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

        imgTgl.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year=cal.get( Calendar.YEAR );
                int month=cal.get( Calendar.MONTH);
                int day=cal.get( Calendar.DAY_OF_MONTH );

                DatePickerDialog dialog=new DatePickerDialog( getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.show();
            }
        } );

        dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                Log.d(TAG,"onDateSet: dd/mm/yyy: " + dayOfMonth + "/" + month + "/" + year);
                String date = dayOfMonth+"/"+month+"/"+year;
                textDate.setText( date );
            }
        };

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addNews();
                UploadImageFileToFirebaseStorage();
            }
        });

        return addEvent;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();
            Log.d("Test1",FilePathUri.toString());

            String getname = getFileNameByUri(getContext(),FilePathUri);
            Log.d("realpath",getname);


//            File file = new File(FilePathUri.getPath());//create path from uri
//            final String[] split = file.getPath().split(":");//split the path.
//            String filePath = split[1];//assign it to a string(your choice).
//
//            Log.d("filePath",filePath);

//            String realpath =  getRealPathFromURI(getContext(),FilePathUri);
//            Log.d("real",realpath);

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(addEvent.getContext().getContentResolver(), FilePathUri);
                SelectImage.setImageBitmap(bitmap);
                selectImage=bitmap;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectImage.compress(Bitmap.CompressFormat.JPEG,100,baos);


//                byte[] test =baos.toByteArray();
//                InputStream in = new FileInputStream(new File(FilePathUri.getPath()));
//                byte[] buf;
//                buf = new byte[in.available()];
//                Bitmap bitmapNew = BitmapFactory.decodeByteArray(buf, 0, buf.length);
//                Bitmap bitmapNew = BitmapFactory.decodeByteArray(test, 0, test.length);
//                SelectImage.setImageBitmap(bitmapNew);
//                Log.d("test",test.toString());

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public static String getFileNameByUri(Context context, Uri uri)
    {
        String fileName="unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().compareTo("content")==0)
        {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst())
            {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment();
            }
        }
        else if (uri.getScheme().compareTo("file")==0)
        {
            fileName = filePathUri.getLastPathSegment();
        }
        else
        {
            fileName = fileName+"_"+filePathUri.getLastPathSegment();
        }
        return fileName;
    }
//    public String getRealPathFromURI(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

//    @SuppressLint("NewApi")
//    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
//        String selection = null;
//        String[] selectionArgs = null;
//        // Uri is different in versions after KITKAT (Android 4.4), we need to
//        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                return Environment.getExternalStorageDirectory() + "/" + split[1];
//            } else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                uri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//            } else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                if ("image".equals(type)) {
//                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                selection = "_id=?";
//                selectionArgs = new String[]{
//                        split[1]
//                };
//            }
//        }
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//
//
//            if (isGooglePhotosUri(uri)) {
//                return uri.getLastPathSegment();
//            }
//
//            String[] projection = {
//                    MediaStore.Images.Media.DATA
//            };
//            Cursor cursor = null;
//            try {
//                cursor = context.getContentResolver()
//                        .query(uri, projection, selection, selectionArgs, null);
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                if (cursor.moveToFirst()) {
//                    return cursor.getString(column_index);
//                }
//            } catch (Exception e) {
//            }
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    public static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
//    }



    public void UploadImageFileToFirebaseStorage() {


        final String place=textPlace.getText().toString(),
                title=textTitle.getText().toString(),
                date=textDate.getText().toString();

        final String idEvent = FirebaseDatabase.getInstance().getReference().push().getKey();


        if (FilePathUri != null) {

            progressDialog.setTitle("Image is Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference folderRef = storageReference.child("Event/"+idEvent+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] data =baos.toByteArray();
            final UploadTask uploadTask = folderRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addEvent.getContext(),"Failed Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    String imageulr = folderRef.child("Event/"+idEvent+".jpg").getName().toString();

                    if(formChecking()==0) {

                        EventDAO event = new EventDAO(title, place,imageulr, date);
                        FirebaseDatabase.getInstance().getReference("Event")
                                .child(idEvent).setValue(event)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.add(R.id.framelay, new EventRecycleAdmin());
                                            transaction.commit();

                                            Toast.makeText(getActivity(), "Add Success", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                    Toast.makeText(addEvent.getContext(),"Success Upload", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }else {

            Toast.makeText(addEvent.getContext(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();
        }
    }






    private void init(){
        textPlace=addEvent.findViewById(R.id.textPlace);
        textTitle=addEvent.findViewById(R.id.textTitle);
        textDate=addEvent.findViewById(R.id.textDate);
//        textPicture=addEvent.findViewById(R.id.textPicture);
        imgTgl=addEvent.findViewById( R.id.imgDate );
        btnAddEvent=addEvent.findViewById(R.id.btnAddEvent);
        SelectImage=addEvent.findViewById(R.id.SelectImage);
        progressDialog = new ProgressDialog(addEvent.getContext());

        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();
    }


    private int formChecking(){
        //Fungsi Check Form

        String place=textPlace.getText().toString(),
                title=textTitle.getText().toString(),
                date=textDate.getText().toString();


        if(place.isEmpty()){
            textPlace.setError("Sender Name is Required");
            textPlace.requestFocus();
            return 1;
        }

        if(title.isEmpty()){
            textTitle.setError("Title is Required");
            textTitle.requestFocus();
            return 1;
        }

        if(date.isEmpty()){
            textDate.setError("Desc is Required");
            textDate.requestFocus();
            return 1;
        }




        return 0;
    }

}
