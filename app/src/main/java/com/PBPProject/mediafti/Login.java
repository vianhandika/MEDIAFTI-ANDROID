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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private Button btnRegister,btnLogin;
    private EditText textEmail,textPassword;
    private TextView txtForgotPass;
    private FirebaseAuth mAuth;
    private String role,uid;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();

        setInit();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(formChecking()==0){
                    login();
                }
            }
        });
        txtForgotPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Login.this,ResetPassword.class);
                startActivity(intent);
            }
        } );


    }

    private void login(){
        String email=textEmail.getText().toString(),
                password=textPassword.getText().toString();
        progressDialog= ProgressDialog.show(Login.this,"","Loading. . .",true);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    checkEmailVerification();
                    progressDialog.dismiss();
                    //startActivity(new Intent(Login.this,Dashboard.class));
                }else{
                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        });

    }

    public void checkEmailVerification(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        uid = mAuth.getUid();
       // final String temp;


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
                Boolean emailflag = firebaseUser.isEmailVerified();

                String role=dataSnapshot.child(uid).child("role").getValue(String.class);

                if(emailflag){

                    if(role.equals("admin"))
                    {
                        startActivity(new Intent(Login.this, AdminPanel.class));
                        finish();
                    }
                    else if(role.equals("user"))
                    {
                        startActivity(new Intent(Login.this, Dashboard.class));
                        finish();
                    }
//
//                    startActivity(new Intent(Login.this, Dashboard.class));
                } else {
                    Toast.makeText(Login.this,"Verify Your Email First",Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                }


//                Toast.makeText(Login.this,role.toString(),Toast.LENGTH_LONG).show();
//
//                temp =role;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Login.this,"Failed Matching From Database",Toast.LENGTH_LONG).show();
            }

        });


    }



    private void setInit(){
        btnRegister=findViewById(R.id.btnRegister);
        btnLogin=findViewById(R.id.btnLogin);
        textPassword=findViewById(R.id.textPassword);
        textEmail=findViewById(R.id.textEmail);
        txtForgotPass=findViewById( R.id.txtForgotPass );
    }

    private int formChecking(){
        //Fungsi Check Form
        String email=textEmail.getText().toString(),
                password=textPassword.getText().toString();



        if(email.isEmpty()){
            textEmail.setError("Email is Required");
            textEmail.requestFocus();
            return 1;
        }


        if(password.isEmpty()){
            textPassword.setError("Password is Required");
            textPassword.requestFocus();
            return 1;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textEmail.setError("Email Format is Wrong");
            textEmail.requestFocus();
            return 1;
        }

//        if(password.length()<8){
//            textPassword.setError("Minimum Password Length is 8");
//            textPassword.requestFocus();
//            return 1;
//        }
//
//        if(!cmfpassword.equals(password)){
//            textCmfPassword.setError("Password Didnt Matches");
//            textCmfPassword.requestFocus();
//            return 1;
//        }


        return 0;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
