package com.company.zeeshan.wallpaperstories.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.company.zeeshan.wallpaperstories.Models.Post;
import com.company.zeeshan.wallpaperstories.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MotivationalWallpapers extends Fragment {

    ArrayList<Post> wallpapers;
    private OnFragmentInteractionListener mListener;
    public MotivationalWallpapers() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_motivational_wallpapers, container, false);

        final ProgressBar progressBar= v.findViewById(R.id.loading);
        final TextView loading = v.findViewById(R.id.loadingText);
        RecyclerView recyclerView = v.findViewById(R.id.rv_motivational);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity() , 2));
        recyclerView.setHasFixedSize(true);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wallpapers").child("Motivational");
        WeakReference<DatabaseReference> reference = new WeakReference<DatabaseReference>(ref);
        wallpapers = new ArrayList<>();
        final MotivationalAdapter adapter = new MotivationalAdapter(wallpapers);

        reference.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();

                for (DataSnapshot d:
                     dataSnapshots) {
                    wallpapers.add(d.getValue(Post.class));
                }
                adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerView.setAdapter(adapter);



        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

     class MotivationalAdapter extends RecyclerView.Adapter<ViewHolder>{

        WeakReference<ArrayList<Post>> wallpapers;
        int lastPosition;


         public MotivationalAdapter(ArrayList<Post> posts){
            wallpapers = new WeakReference<ArrayList<Post>>(posts);
        }

       @Override
       public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_images_holder, parent, false));
       }

       @Override
       public void onBindViewHolder(ViewHolder holder, int position) {


           Animation animation = AnimationUtils.loadAnimation(getActivity(),
                   (holder.getAdapterPosition() > lastPosition) ? R.anim.up_from_bottom
                           : R.anim.down_from_top);

           holder.itemView.startAnimation(animation);

           lastPosition = holder.getAdapterPosition();

           Picasso.with(getActivity()).load(wallpapers.get().get(holder.getAdapterPosition()).imageUrl);
           holder.name.setText(wallpapers.get().get(holder.getAdapterPosition()).postedBy);
           holder.date.setText(wallpapers.get().get(holder.getAdapterPosition()).postedOn);
       }

       @Override
       public int getItemCount() {
           return wallpapers.get().size();
       }
   }

     class ViewHolder extends RecyclerView.ViewHolder{

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
