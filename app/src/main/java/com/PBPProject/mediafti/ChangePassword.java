package com.PBPProject.mediafti;

import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChangePassword extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Button btnUpdate,btnCancel;
    private EditText newPass,cmfNewPass;

    View changepass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changepass= inflater.inflate(R.layout.change_password,container,false);
        setinit();

        btnUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(UpdatePassword.this, ContactsContract.Profile.class));
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new AccountProfile());
                transaction.commit();
            }
        });


        return changepass;
    }

    public void changePassword() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userPasswordNew = cmfNewPass.getText().toString();
        if(formChecking()==0) {
            mUser.updatePassword( userPasswordNew ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference( "Users" ).child( mUser.getUid() ).child( "password" ).setValue( md5( userPasswordNew ) )
                                .addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText( changepass.getContext(), "Success Change Password", Toast.LENGTH_SHORT ).show();

                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                            transaction.add( R.id.framelay, new AccountProfile() );
                                            transaction.commit();
                                        } else {
                                            Toast.makeText( changepass.getContext(), "Failed Change Password", Toast.LENGTH_SHORT ).show();
                                        }
                                    }
                                } );


                    } else {
                        Toast.makeText( changepass.getContext(), "Failed Change Password", Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
        }else {

        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void setinit(){
        btnUpdate= changepass.findViewById(R.id.btnUpdate);
        btnCancel= changepass.findViewById(R.id.btnCancel);
        newPass=changepass.findViewById(R.id.newPass);
        cmfNewPass=changepass.findViewById( R.id.cmfNewPass );

    }

    private int formChecking(){
        //Fungsi Check Form
        String pass=newPass.getText().toString(),
                cmfpass=cmfNewPass.getText().toString();


        if(pass.isEmpty()){
            newPass.setError("New Password is Required");
            newPass.requestFocus();
            return 1;
        }

        if(cmfpass.isEmpty()){
            cmfNewPass.setError("Confirm New Password is Required");
            cmfNewPass.requestFocus();
            return 1;
        }

        if(pass.length()<8){
            newPass.setError("Minimum Password Length is 8");
            newPass.requestFocus();
            return 1;
        }

        if(!cmfpass.equals(pass)){
            cmfNewPass.setError("Password Didnt Matches");
            cmfNewPass.requestFocus();
            return 1;
        }


        return 0;
    }
}
