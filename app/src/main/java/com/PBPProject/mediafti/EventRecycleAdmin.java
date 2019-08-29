package com.PBPProject.mediafti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class EventRecycleAdmin extends Fragment{
    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    private ImageView addEvent;
    FrameLayout framelay;
    Button mDel;
    View event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        event= inflater.inflate(R.layout.event_recycler_admin,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Event");
        mDatabase.keepSynced(true);
//
        mEventList=event.findViewById(R.id.adminEventRecycle);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        onClickImage();
        onClickButton();


        return event;
    }

    public void onClickImage(){
        addEvent=event.findViewById(R.id.addEvent);


        addEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new AddEvent());
                transaction.commit();

            }
        });

    }

    public void onClickButton(){
        mDel=event.findViewById( R.id.btnDelEvent );
        mDel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage( "Do you want to delete all event?" )
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<EventDAO,EventRecycleAdmin.EventViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EventDAO, EventRecycleAdmin.EventViewHolder>
                (EventDAO.class,R.layout.event_content_admin,EventRecycleAdmin.EventViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(EventRecycleAdmin.EventViewHolder viewHolder, EventDAO model, int position) {
                viewHolder.setDate("Date\t\t:"+model.getDate());
                viewHolder.setTitle("Event\t:"+model.getTitle());
                viewHolder.setPlace("Place\t:"+model.getPlace());
//                viewHolder.mDelEvent.setOnClickListener( new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//                        builder.setMessage( "Do you want to delete this event?" )
//                                .setCancelable( false )
//                                .setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //mDatabase.removeValue();
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

        mEventList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView mDelEvent;

        public EventViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            mDelEvent=itemView.findViewById( R.id.tvDelEvent );
        }

        public void setDate(String date)
        {
            TextView eventDate=(TextView)mView.findViewById(R.id.tvDate);
            eventDate.setText(date);
        }

        public void setTitle(String title)
        {
            TextView eventTitle=(TextView)mView.findViewById(R.id.tvEvent);
            eventTitle.setText(title);
        }

        public void setPlace(String place)
        {
            TextView eventPlace=(TextView)mView.findViewById(R.id.tvPlace);
            eventPlace.setText(place);
        }

    }
}
