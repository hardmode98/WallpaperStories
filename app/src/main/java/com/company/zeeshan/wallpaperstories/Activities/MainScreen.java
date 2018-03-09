package com.company.zeeshan.wallpaperstories.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.company.zeeshan.wallpaperstories.Fragments.ArtWallpapers;
import com.company.zeeshan.wallpaperstories.Fragments.Community;
import com.company.zeeshan.wallpaperstories.Fragments.DarkWallpapers;
import com.company.zeeshan.wallpaperstories.Fragments.Favorites;
import com.company.zeeshan.wallpaperstories.Fragments.Gallery;
import com.company.zeeshan.wallpaperstories.Fragments.MotivationalWallpapers;
import com.company.zeeshan.wallpaperstories.Fragments.Profile;
import com.company.zeeshan.wallpaperstories.Models.Post;
import com.company.zeeshan.wallpaperstories.Models.UniversalConstants;
import com.company.zeeshan.wallpaperstories.R;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Favorites.OnFragmentInteractionListener,
        Community.OnFragmentInteractionListener
        , Gallery.OnFragmentInteractionListener,
        ArtWallpapers.OnFragmentInteractionListener,
        DarkWallpapers.OnFragmentInteractionListener,
        Profile.OnFragmentInteractionListener,
        MotivationalWallpapers.OnFragmentInteractionListener {

    TabLayout tabLayout;
    ViewPager viewPager;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {

                case R.id.community:

                    findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                    Community community = new Community();
                    transaction.add(R.id.fragment_container, community).commit();
                    getSupportActionBar().setTitle("Community");
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);

                    return true;

                case R.id.explore:

                    findViewById(R.id.fragment_container).setVisibility(View.GONE);
                    getSupportActionBar().setTitle("Explore");
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    return true;

                case R.id.favorites:

                    findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                    Favorites favorites = new Favorites();
                    transaction.replace(R.id.fragment_container, favorites).commit();
                    getSupportActionBar().setTitle("Favorites");
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);

                    return true;
                case R.id.profile:

                    Profile profile = new Profile();
                    transaction.replace(R.id.fragment_container, profile).commit();
                    getSupportActionBar().setTitle("Uploads");
                    tabLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Community");
        toolbar.setTitleTextColor(getResources().getColor(R.color.cardview_dark_background));
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, "ca-app-pub-5098126384084691/7145103256");

        final Community community = new Community();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, community).commit();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new Adapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //SHOW THE PHOTO OF THE DAY

        final TextView tv = findViewById(R.id.textView13);
        FirebaseDatabase.getInstance().getReference("POTD").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                final Post post = dataSnapshot.getValue(Post.class);

                if (post != null) {
                    tv.setText(post.postedBy);


                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                            intent.putExtra(UniversalConstants.IMAGE_DETAILS_URL, post.imageUrl);
                            intent.putExtra(UniversalConstants.POSTED_BY, post.postedBy);
                            intent.putExtra(UniversalConstants.POSTED_ON, post.postedOn);
                            intent.putExtra(UniversalConstants.POST_ID, post.postid);
                            intent.putExtra(UniversalConstants.POSTTEXT, post.postText);
                            intent.putExtra("uid", post.uid);

                            startActivity(intent);
                        }
                    });
                } else {
                    tv.setText("Wait till morning");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class Adapter extends FragmentStatePagerAdapter{

        Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0 :
                    return new MotivationalWallpapers();
                case 1:
                    return new DarkWallpapers();
                case 2:
                    return new ArtWallpapers();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Motivational";
                case 1:
                    return "Dark";
                case 2:
                    return "Art";

            }
            return null;
        }
    }
}
