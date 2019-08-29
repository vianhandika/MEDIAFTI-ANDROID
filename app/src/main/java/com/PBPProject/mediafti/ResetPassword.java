package com.PBPProject.mediafti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity{
    Button btnResetPass;
    EditText email;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_reset_password);

        init();
        firebaseAuth=FirebaseAuth.getInstance();

        btnResetPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail( email.getText().toString() ).addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPassword.this,"Password send to your email",Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(ResetPassword.this,Login.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(ResetPassword.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                } );
            }
        } );
    }

    /*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resetPass= inflater.inflate(R.layout.activity_reset_password,container,false);
        init();
        firebaseAuth=FirebaseAuth.getInstance();

        btnResetPass.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.sendPasswordResetEmail( email.getText().toString())
                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Password send to your email",Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(getActivity(),Login.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                } );
            }
        } );

        return resetPass;
    }*/

    private void init(){
        email=findViewById(R.id.txtEmail);
        btnResetPass=findViewById(R.id.btnResetPass);
    }
}
