package com.company.zeeshan.wallpaperstories.Fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.company.zeeshan.wallpaperstories.Activities.DetailActivity;
import com.company.zeeshan.wallpaperstories.Models.Post;
import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Profile extends Fragment {

    int lastPosition;
    ArrayList<Post> data;
    private OnFragmentInteractionListener mListener;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        data = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.uploads);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setHasFixedSize(true);


        final RecyclerView.Adapter adapter = new RecyclerView.Adapter<ViewHolder>() {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.new_images_holder, parent, false));
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

                        intent.putExtra(UniversalConstants.IMAGE_DETAILS_URL, data.get(holder.getAdapterPosition()).imageUrl);
                        intent.putExtra(UniversalConstants.POSTED_BY, data.get(holder.getAdapterPosition()).postedBy);
                        intent.putExtra(UniversalConstants.POSTED_ON, data.get(holder.getAdapterPosition()).postedOn);
                        intent.putExtra(UniversalConstants.POST_ID, data.get(holder.getAdapterPosition()).postid);
                        intent.putExtra(UniversalConstants.POSTTEXT, data.get(holder.getAdapterPosition()).postText);
                        intent.putExtra("uid", data.get(holder.getAdapterPosition()).uid);

                        startActivity(intent);
                    }
                });

            }

            @Override
            public int getItemCount() {
                return data.size();
            }

        };


        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Uploads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                data.clear();

                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot s : dataSnapshots) {
                    data.add(s.getValue(Post.class));
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerView.setAdapter(adapter);


        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
