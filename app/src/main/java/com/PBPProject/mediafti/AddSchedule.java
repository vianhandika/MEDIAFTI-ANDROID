package com.PBPProject.mediafti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AddSchedule extends Fragment{
    EditText matkul,ruang;
    Spinner hari,sesi;
    Button btnSimpan,btnShow;
    FrameLayout framelay;
    View addSchedule;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addSchedule= inflater.inflate(R.layout.add_schedule,container,false);
        init();

        btnSimpan=addSchedule.findViewById(R.id.btnAdd);
        btnShow=addSchedule.findViewById( R.id.btnShow );

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSchedule();
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new ScheduleRecycler());
                transaction.commit();
            }
        });

        return addSchedule;
    }

    private void init(){
        matkul=addSchedule.findViewById(R.id.course);
        ruang=addSchedule.findViewById(R.id.room);
        sesi=addSchedule.findViewById(R.id.spinnerSession);
        hari=addSchedule.findViewById(R.id.spinnerDay);
    }

    private void addSchedule(){
        final String kuliah=matkul.getText().toString(),
                room=ruang.getText().toString(),
                day=hari.getSelectedItem().toString(),
                session=sesi.getSelectedItem().toString();
//        final int maxPost=100;
        final int startIndex = (int) (10000 * Math.random()) + 1;
        mAuth=FirebaseAuth.getInstance();
        mUser= mAuth.getCurrentUser();
        String uid=mUser.getUid();
        String idschedule = FirebaseDatabase.getInstance().getReference().push().getKey();

        if(formChecking()==0) {

            ScheduleDAO jadwal = new ScheduleDAO(day, session, kuliah, room);
            FirebaseDatabase.getInstance().getReference("Schedule")
                    .child(uid).child( idschedule ).setValue(jadwal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                matkul.setText("");
                                ruang.setText("");

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

        String kuliah=matkul.getText().toString(),
                room=ruang.getText().toString();

        if(kuliah.isEmpty()){
            matkul.setError("Course is Required");
            matkul.requestFocus();
            return 1;
        }

        if(room.isEmpty()){
            ruang.setError("Room is Required");
            ruang.requestFocus();
            return 1;
        }

        return 0;
    }
}
