package com.PBPProject.mediafti;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class UserDashboard extends Fragment {
    View dashboard;
    ImageView event,news,schedule;
    FrameLayout framelay;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        dashboard= inflater.inflate(R.layout.user_dashboard,container,false);

        onClickImage();

        return dashboard;
    }

    public void onClickImage(){
        event=dashboard.findViewById(R.id.bgEvent);
        news=dashboard.findViewById(R.id.bgNews);
        schedule=dashboard.findViewById( R.id.bgSchedule );
        framelay=dashboard.findViewById(R.id.framelay);

        event.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new EventRecycler());
                transaction.commit();
            }
        });

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new NewsRecycler());
                transaction.commit();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new ScheduleRecycler());
                transaction.commit();
            }
        });

    }
}
