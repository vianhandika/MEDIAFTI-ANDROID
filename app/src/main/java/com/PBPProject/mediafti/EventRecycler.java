package com.PBPProject.mediafti;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EventRecycler extends Fragment {
    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    FrameLayout framelay;

    View event;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        event= inflater.inflate(R.layout.event_recycler,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Event");
        mDatabase.keepSynced(true);
//
        mEventList=event.findViewById(R.id.eventrecyclerview);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(new LinearLayoutManager(getActivity()));


        return event;
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<EventDAO,EventViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<EventDAO, EventRecycler.EventViewHolder>
                (EventDAO.class,R.layout.event_content,EventRecycler.EventViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(final EventRecycler.EventViewHolder viewHolder, EventDAO model, int position) {
                viewHolder.setDate(model.getDate());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setPlace(model.getPlace());
                viewHolder.setImage(getActivity().getApplicationContext(),model.getImage());


            }
        };

        mEventList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public EventViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date)
        {
            TextView eventDate=(TextView)mView.findViewById(R.id.eventDate);
            eventDate.setText(date);
        }

        public void setTitle(String title)
        {
            TextView eventTitle=(TextView)mView.findViewById(R.id.eventTitle);
            eventTitle.setText(title);
        }

        public void setPlace(String place)
        {
            TextView eventPlace=(TextView)mView.findViewById(R.id.eventPlace);
            eventPlace.setText(place);
        }

        public void setImage(final Context ctx, String image)
        {
            final ImageView eventImage=(ImageView)mView.findViewById(R.id.eventImage);

            StorageReference load = FirebaseStorage.getInstance().getReference("Event/");
//            Picasso.with(ctx).load(uri.toString()).into(eventImage);

            load.child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.with(ctx).load(uri.toString()).into(eventImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });




        }



    }
}

