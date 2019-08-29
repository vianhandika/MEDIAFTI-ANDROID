package com.PBPProject.mediafti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.awt.font.TextAttribute;

public class AdminDashboard  extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private ImageView News,Event,MNews,MEvent,Logout;
    TextView userName,userEmail;
    FrameLayout framelay;

    View admindashboard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        admindashboard= inflater.inflate(R.layout.admin_dashboard,container,false);
//        userName =  admindashboard.findViewById(R.id.userName);
//        userEmail = admindashboard.findViewById(R.id.userEmail);
//        Toast.makeText(getActivity(),userName.getText().toString(),Toast.LENGTH_SHORT).show();
        loadprofileinformation();
        onClickImage();
        return admindashboard;
    }

    private void loadprofileinformation(){

        mAuth= FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        uid= mUser.getUid();

        userName =  admindashboard.findViewById(R.id.userName);
        userEmail = admindashboard.findViewById(R.id.userEmail);

        mDatabase= FirebaseDatabase.getInstance().getReference("Users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=dataSnapshot.child(uid).child("username").getValue(String.class);
                String email=dataSnapshot.child(uid).child("email").getValue(String.class);
//                userName =  admindashboard.findViewById(R.id.userName);
//                userEmail = admindashboard.findViewById(R.id.userEmail);
//                Toast.makeText(getActivity(),userName.getText().toString(),Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(),userEmail.getText().toString(),Toast.LENGTH_SHORT).show();
                userName.setText(username);
                userEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {

        super.onStart();
    }

    public void onClickImage() {

        News=admindashboard.findViewById(R.id.bgNews);
        Event=admindashboard.findViewById(R.id.bgEvent);
        MEvent=admindashboard.findViewById(R.id.bgMEvent);
        MNews=admindashboard.findViewById(R.id.bgMNews);
        Logout=admindashboard.findViewById(R.id.bgLogout);

        News.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new NewsRecycler());
                transaction.commit();
            }
        });
        Event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new EventRecycler());
                transaction.commit();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity(),Login.class));

            }
        });
        MNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new NewsRecycleAdmin());
                transaction.commit();
            }
        });
        MEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new EventRecycleAdmin());
                transaction.commit();
            }
        });

    }
}
