package com.company.zeeshan.wallpaperstories.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.company.zeeshan.wallpaperstories.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Gallery extends Fragment {

    private OnFragmentInteractionListener mListener;


    public Gallery() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        //ArrayList

        final ArrayList<String> data = new ArrayList<>();

        //Setup Recycler View
        final RecyclerView rv_gallery = v.findViewById(R.id.rv_gallery);
        rv_gallery.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_gallery.setHasFixedSize(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("Wallpapers");


        final RecyclerView.Adapter adapter = new RecyclerView.Adapter<ViewHolder>() {



            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.wallpaper_holder,parent,false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Picasso.with(getActivity()).load(data.get(holder.getAdapterPosition()))
                        .resize(300,400).centerCrop().into(holder.imageView);

            }

            @Override
            public int getItemCount() {
                return data.size();
            }
        };


        new AsyncTask<Void,Void,Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {

                databaseReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        data.clear();
                        Iterable<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren();
                        for (DataSnapshot DS : dataSnapshotIterator){
                            data.add(DS.getValue(String.class));
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getActivity().findViewById(R.id.progressBarMain).setVisibility(View.GONE);
            }

        }.execute();

        rv_gallery.setAdapter(adapter);

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
    private class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        ViewHolder(View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.imageView2);
        }
    }
}
