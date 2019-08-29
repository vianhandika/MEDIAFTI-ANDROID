package com.PBPProject.mediafti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Register extends AppCompatActivity {

    private Button btnRegister;
    private EditText textUsername,textEmail,textPassword,textCmfPassword;

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        setInit();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    private void register(){
        //Fungsi Register
        final String username=textUsername.getText().toString(),
                email=textEmail.getText().toString(),
                password=textPassword.getText().toString(),
                cmfpassword=textCmfPassword.getText().toString(),
                role="user";
        progressDialog= ProgressDialog.show(Register.this,"","Loading. . .",true);
        if(formChecking()==0) {

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                UserDAO user =new UserDAO(username,email,md5(password),role);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sendEmailVerification();
                                                    progressDialog.dismiss();
                                                } else {
                                                    Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            }
//                            else{
//                                Toast.makeText(Register.this,"Failed Register",Toast.LENGTH_LONG).show();
//                            }
                        }
                    });
//            String id=databaseUser.push().getKey();
//            UserDAO user=new UserDAO(id,username,email,password);
//            databaseUser.child(id).setValue(user);

        }
        else {
//            Toast.makeText(this,"You Must Fill all Form",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
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

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()!=null){
            //Jika user sudah login

        }
    }

    private void sendEmailVerification(){
//        progressDialog= ProgressDialog.show(Register.this,"","Loading. . .",true);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
//                        progressDialog.dismiss();
                        Toast.makeText(Register.this, "Success Register, Email Verification Has Been Sent", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(Register.this, Login.class));

                    } else {
                        Toast.makeText(Register.this,"Email Verification not sent",Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    private void setInit() {
        //Fungsi Init
        btnRegister=findViewById(R.id.btnRegister);
        textUsername=findViewById(R.id.textUsername);
        textEmail=findViewById(R.id.textEmail);
        textPassword=findViewById(R.id.textPassword);
        textCmfPassword=findViewById(R.id.textCmfPassword);

    }

    private int formChecking(){
        //Fungsi Check Form
        String username=textUsername.getText().toString(),
                email=textEmail.getText().toString(),
                password=textPassword.getText().toString(),
                cmfpassword=textCmfPassword.getText().toString();


        if(username.isEmpty()){
            textUsername.setError("Username is Required");
            textUsername.requestFocus();
            return 1;
        }

        if(email.isEmpty()){
            textEmail.setError("Email is Required");
            textEmail.requestFocus();
            return 1;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textEmail.setError("Email Format is Wrong");
            textEmail.requestFocus();
            return 1;
        }

        if(password.isEmpty()){
            textPassword.setError("Password is Required");
            textPassword.requestFocus();
            return 1;
        }

        if(password.length()<8){
            textPassword.setError("Minimum Password Length is 8");
            textPassword.requestFocus();
            return 1;
        }

        if(!cmfpassword.equals(password)){
            textCmfPassword.setError("Password Didnt Matches");
            textCmfPassword.requestFocus();
            return 1;
        }


        return 0;
    }

}
