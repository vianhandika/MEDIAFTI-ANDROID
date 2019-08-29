package com.PBPProject.mediafti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanel extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private ImageView bgAdmin,bgUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new AdminDashboard()).commit();

//        onClickImage();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new AdminDashboard()).commit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    private void onClickImage(){
//        bgAdmin=findViewById(R.id.bgAdmin);
//        bgUser=findViewById(R.id.bgUser);
//
//        mAuth= FirebaseAuth.getInstance();
//
//
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
//        uid = mAuth.getUid();
//
//        bgUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(AdminPanel.this, Dashboard.class));
//
//            }
//        });
//
//        bgAdmin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(AdminPanel.this,AdminDashboard.class));
//            }
//        });
//    }


}
