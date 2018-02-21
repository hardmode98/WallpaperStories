package com.company.zeeshan.wallpaperstories.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.company.zeeshan.wallpaperstories.Activities.DetailActivity;
import com.company.zeeshan.wallpaperstories.Activities.ImagePoster;
import com.company.zeeshan.wallpaperstories.Models.Post;
import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class Community extends Fragment {

    ArrayList<Post> data;
    int lastPosition;
    Boolean gotAllImagesAtRuntime = false;
    private OnFragmentInteractionListener mListener;


    public Community() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popular, container, false);

        final ProgressBar progressBar= view.findViewById(R.id.loading);
        final TextView laodingText = view.findViewById(R.id.loadingText);

        data= new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("Community");

        RecyclerView recyclerView = view.findViewById(R.id.rv_popular);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        final RecyclerView.Adapter adapter = new RecyclerView.Adapter<ViewHolder>() {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.new_images_holder , parent , false));
            }

            @Override
            public void onBindViewHolder(final ViewHolder holder, int position) {

                Animation animation = AnimationUtils.loadAnimation(getActivity(),
                        (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom
                                : R.anim.down_from_top);

                holder.itemView.startAnimation(animation);

                lastPosition = holder.getAdapterPosition();

                holder.name.setText(data.get(holder.getAdapterPosition()).postedBy);

                holder.date.setText(data.get(holder.getAdapterPosition()).postedOn);

                Picasso.with(getActivity()).load(data.get(holder.getAdapterPosition()).imageUrl)
                        .resize(500, 500).centerInside()
                        .into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);

                        intent.putExtra(UniversalConstants.IMAGE_DETAILS_URL , data.get(holder.getAdapterPosition()).imageUrl);
                        intent.putExtra(UniversalConstants.POSTED_BY, data.get(holder.getAdapterPosition()).postedBy);
                        intent.putExtra(UniversalConstants.POSTED_ON, data.get(holder.getAdapterPosition()).postedOn);
                        intent.putExtra(UniversalConstants.POST_ID, data.get(holder.getAdapterPosition()).postid);
                        intent.putExtra(UniversalConstants.POSTTEXT, data.get(holder.getAdapterPosition()).postText);

                        startActivity(intent);
                    }
                });

            }

            @Override
            public int getItemCount() {
                return data.size();
            }

        };


        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!gotAllImagesAtRuntime){

                    getAllDataAtStart(dataSnapshot.getChildren());
                    progressBar.setVisibility(View.GONE);
                    laodingText.setVisibility(View.GONE);

                }else {

                    getNewData(dataSnapshot.getChildren());

                }

                Collections.reverse(data);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ImagePoster.class));
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void getAllDataAtStart(Iterable<DataSnapshot> iterable){

        Log.d("Entered" , "ALL DATA");
        for (DataSnapshot DS : iterable) {

            if (DS.getValue(Post.class).certified.equals("yes")){
                data.add(DS.getValue(Post.class));
            }
        }
        gotAllImagesAtRuntime = true;



    }

    public void getNewData(Iterable<DataSnapshot> iterable){
        Log.d("Entered" , "NEW DATA");

        for (DataSnapshot DS : iterable) {
            if (!iterable.iterator().hasNext()){
                if (DS.getValue(Post.class).certified.equals("yes")){
                    data.add(DS.getValue(Post.class));

                }
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        ImageView imageView;
        TextView date;


        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textView5);
            date = itemView.findViewById(R.id.textView6);
            imageView = itemView.findViewById(R.id.postImageView);
        }
    }
}
