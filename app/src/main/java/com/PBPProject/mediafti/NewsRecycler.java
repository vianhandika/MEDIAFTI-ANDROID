package com.PBPProject.mediafti;

import android.app.Application;
import android.content.Context;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class NewsRecycler extends Fragment {
    private RecyclerView mNewsList;
    private DatabaseReference mDatabase;

    View news;






    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        news= inflater.inflate(R.layout.news_recycler,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("News");
        mDatabase.keepSynced(true);
//
        mNewsList=news.findViewById(R.id.newsrecyclerview);
        mNewsList.setHasFixedSize(true);
        mNewsList.setLayoutManager(new LinearLayoutManager(getActivity()));


        return news;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<NewsDAO,NewsViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<NewsDAO, NewsViewHolder>
                (NewsDAO.class,R.layout.news_content,NewsViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(NewsViewHolder viewHolder, NewsDAO model, int position) {
                viewHolder.setFrom(model.getFrom());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity().getApplicationContext(),model.getImage());


            }
        };

        mNewsList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public NewsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        public void setFrom(String from)
        {
            TextView newsFrom=(TextView)mView.findViewById(R.id.newsFrom);
            newsFrom.setText(from);
        }

        public void setTitle(String title)
        {
            TextView newsTitle=(TextView)mView.findViewById(R.id.newsTitle);
            newsTitle.setText(title);
        }

        public void setDesc(String desc)
        {
            TextView newsDesc=(TextView)mView.findViewById(R.id.newsDesc);
            newsDesc.setText(desc);
        }

        public void setImage(final Context ctx, String image) {
            final ImageView newsImage = (ImageView) mView.findViewById(R.id.newsImage);

            StorageReference load = FirebaseStorage.getInstance().getReference("News/");
//            Picasso.with(ctx).load(uri.toString()).into(eventImage);

            load.child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Picasso.with(ctx).load(uri.toString()).into(newsImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }

    }
}
