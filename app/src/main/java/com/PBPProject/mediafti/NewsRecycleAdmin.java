package com.PBPProject.mediafti;

import android.app.AlertDialog;
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

public class NewsRecycleAdmin extends Fragment{
    private RecyclerView mNewsList;
    private DatabaseReference mDatabase;
    private ImageView addNews;
    FrameLayout framelay;
    Button mDel;
    View news;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        news= inflater.inflate(R.layout.news_recycler_admin,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("News");
        mDatabase.keepSynced(true);
//
        mNewsList=news.findViewById(R.id.adminNewsRecycle);
        mNewsList.setHasFixedSize(true);
        mNewsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        onClickImage();
        onClickButton();

        return news;
    }

    public void onClickImage(){
        addNews =news.findViewById(R.id.addNews);


        addNews.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.framelay, new AddNews());
                transaction.commit();

            }
        });

    }

    public void onClickButton(){
        mDel=news.findViewById( R.id.btnDelNews );
        mDel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage( "Do you want to delete all news?" )
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
        FirebaseRecyclerAdapter<NewsDAO,NewsRecycleAdmin.NewsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<NewsDAO, NewsRecycleAdmin.NewsViewHolder>
                (NewsDAO.class,R.layout.news_content_admin,NewsRecycleAdmin.NewsViewHolder.class,mDatabase) {
            @Override
            protected void populateViewHolder(NewsRecycleAdmin.NewsViewHolder viewHolder, NewsDAO model, int position) {
                viewHolder.setFrom(model.getFrom());
                viewHolder.setTitle(model.getTitle());
//                viewHolder.mDelNews.setOnClickListener( new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//                        builder.setMessage( "Do you want to delete this News?" )
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

        mNewsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        View mView;
//        TextView mDelNews;

        public NewsViewHolder(View itemView){
            super(itemView);
            mView=itemView;
//            mDelNews=itemView.findViewById( R.id.tvDelNews );
        }

        public void setFrom(String from)
        {
            TextView NewsFrom=(TextView)mView.findViewById(R.id.tvFrom);
            NewsFrom.setText(from);
        }

        public void setTitle(String title)
        {
            TextView NewsTitle=(TextView)mView.findViewById(R.id.tvTitle);
            NewsTitle.setText(title);
        }

    }
}
