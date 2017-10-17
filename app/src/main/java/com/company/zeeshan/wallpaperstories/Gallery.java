package com.company.zeeshan.wallpaperstories;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Gallery extends Fragment {

    private OnFragmentInteractionListener mListener;


    public Gallery() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        final RecyclerView rv_gallery = (RecyclerView) v.findViewById(R.id.rv_gallery);

        final ArrayList<String> strings = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("wallpapers");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> iterator = dataSnapshot.child("all").getChildren();
                for (DataSnapshot dataSnapshot2 : iterator){
                    strings.add(dataSnapshot2.getValue(String.class));
                }
                Toast.makeText(getActivity(),iterator.toString(),Toast.LENGTH_LONG).show();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rv_gallery.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.wallpaper_holder,parent,false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {

            }


            @Override
            public int getItemCount() {
                return 4;
            }
        };
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


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class ViewHolder extends RecyclerView.ViewHolder{
        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
