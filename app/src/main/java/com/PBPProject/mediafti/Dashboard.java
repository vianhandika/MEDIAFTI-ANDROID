package com.PBPProject.mediafti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String uid;
    private TextView userName,userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        userName =  findViewById(R.id.userName);
//        userEmail = findViewById(R.id.userEmail);
        loadprofileinformation();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new UserDashboard()).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FrameLayout framelay;
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new UserDashboard()).commit();
            // Handle the camera action
        } else if (id == R.id.nav_news) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new NewsRecycler()).commit();
//            Intent intent = new Intent(Dashboard.this,NewsRecycler.class);
//            startActivity(intent);
        } else if (id == R.id.nav_event) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new EventRecycler()).commit();

        } else if (id == R.id.nav_schedule) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new ScheduleRecycler()).commit();

        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new AccountProfile()).commit();

        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay, new About()).commit();
        } else if(id==R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this,Login.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                String email=dataSnapshot.child(uid).child("email").getValue(String.class);

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView userName = (TextView) headerView.findViewById(R.id.userName);
                TextView userEmail = (TextView) headerView.findViewById(R.id.userEmail);
                final ImageView userPict = headerView.findViewById(R.id.userPict);
                StorageReference load = FirebaseStorage.getInstance().getReference("Profile/");
//            Picasso.with(ctx).load(uri.toString()).into(eventImage);

                load.child(uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.with(getApplicationContext()).load(uri.toString()).into(userPict);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
//                Toast.makeText(Dashboard.this,username.toString(),Toast.LENGTH_SHORT).show();
//                Toast.makeText(Dashboard.this,email.toString(),Toast.LENGTH_SHORT).show();
                userName.setText(username);
                userEmail.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setFrameLayout(String menu)
    {
        FrameLayout framelay;
        if(menu.equals("news")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new NewsRecycler()).commit();
        } else if(menu.equals("event")){
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new EventRecycler()).commit();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            getSupportFragmentManager().beginTransaction().replace(R.id.framelay,new UserDashboard()).commit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
