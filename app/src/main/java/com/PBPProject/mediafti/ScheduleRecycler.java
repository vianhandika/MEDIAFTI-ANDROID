package com.PBPProject.mediafti;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScheduleRecycler extends Fragment{
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView mJadwalList;
    private DatabaseReference mDatabase;
    ImageView add;
    String uid,idschedule;
    private Button mDel;

    View jadwal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        jadwal= inflater.inflate(R.layout.schedule_recycler,container,false);
        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();

        uid= mUser.getUid();


        mDatabase= FirebaseDatabase.getInstance().getReference().child("Schedule").child( uid );
        mDatabase.keepSynced(true);

        mJadwalList=jadwal.findViewById(R.id.schedulerecyclerview);
        mJadwalList.setHasFixedSize(true);
        mJadwalList.setLayoutManager(new LinearLayoutManager(getActivity()));

        onClickImage();
        onClickButton();
        return jadwal;
    }

    public void onClickButton(){
        mDel=jadwal.findViewById( R.id.btnDel );
        mDel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage( "Do you want to delete all schedule?" )
                        .setCancelable( false )
                        .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.removeValue();
                            }
                        } )
                        .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        } );
                AlertDialog alert=builder.create();
                alert.setTitle( "Alert!!!" );
                alert.show();
            }
        } );
    }

    public void onClickImage(){
        add=jadwal.findViewById(R.id.imgAdd);


        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new AddSchedule());
                transaction.commit();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<ScheduleDAO,ScheduleViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ScheduleDAO,ScheduleRecycler.ScheduleViewHolder>
                (ScheduleDAO.class,R.layout.schedule_content,ScheduleRecycler.ScheduleViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(ScheduleRecycler.ScheduleViewHolder viewHolder, final ScheduleDAO model, final int position) {
                idschedule = FirebaseDatabase.getInstance().getReference().push().getKey();
                viewHolder.setHari(model.getHari());
                viewHolder.setSesi("Session\t: " + model.getSesi());
                viewHolder.setMatkul("Course\t: " + model.getMatkul());
                viewHolder.setRuang("Room\t\t: " + model.getRuang() );
//                viewHolder.mDel.setOnClickListener( new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//                        builder.setMessage( "Do you want to delete this schedule?" )
//                                .setCancelable( false )
//                                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //mDatabase.child( idschedule).removeValue();
//                                    }
//                                } )
//                                .setNegativeButton( "No", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                } );
//                        AlertDialog alert=builder.create();
//                        alert.setTitle( "Alert!!!" );
//                        alert.show();
//                    }
//                } );
            }
        };

        mJadwalList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mDel;

        public ScheduleViewHolder(View itemView){
            super(itemView);
            mView=itemView;
//            mDel=itemView.findViewById( R.id.tvDel );
        }

        public void setHari(String hari)
        {
            TextView jadwalHari=(TextView)mView.findViewById(R.id.tvHari);
            jadwalHari.setText(hari);
        }

        public void setSesi(String sesi)
        {
            TextView jadwalSesi=(TextView)mView.findViewById(R.id.tvSesi);
            jadwalSesi.setText(sesi);
        }

        public void setMatkul(String matkul)
        {
            TextView jadwalMatkul=(TextView)mView.findViewById(R.id.tvMatkul);
            jadwalMatkul.setText(matkul);
        }

        public void setRuang(String ruang)
        {
            TextView jadwalRuang=(TextView)mView.findViewById(R.id.tvRuang);
            jadwalRuang.setText(ruang);

        }

    }
}
