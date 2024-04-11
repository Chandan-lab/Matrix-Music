package com.matrix.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean checkPermission = false;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<Song> list  = new ArrayList<>();
    ListAdapter adapter;
    JcPlayerView jcPlayerView;
    List<JcAudio> jcAudios;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;


    TextView navUsername,navEmail;
    ImageView imageView;
    private StorageReference storageReference;
    private ArrayList<Song> songsList;
    private FloatingActionButton fabUpload;

    int time ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        storageReference = FirebaseStorage.getInstance().getReference();

        songsList = new ArrayList<>();
        fetchSongsFromFirebase();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
               if(item.getItemId()==R.id.logout)
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                } else if (item.getItemId() == R.id.forget_password) {

                   Intent i = new Intent(MainActivity.this,ForgetPassword.class);
                   i.putExtra("email", CommonItem.email);
                   startActivity(i);
               }
               else if (item.getItemId() == R.id.my_profile) {

                   Intent i = new Intent(MainActivity.this,ProfileActivity.class);
                   startActivity(i);
               }
               else if(item.getItemId()==R.id.popular){
                   startActivity(new Intent(MainActivity.this,PopularSongs.class));
               }else if(item.getItemId()==R.id.playlist){
                   startActivity(new Intent(MainActivity.this,PlaylistScreen.class));
               }
                return false;
            }

            @SuppressLint("WrongViewCast")
            private void setupUploadFab() {
                fabUpload = findViewById(R.id.uploadSongButton);
                fabUpload.setOnClickListener(view -> {
                    // Check permissions and start UploadSongActivity
                    Intent intent = new Intent(MainActivity.this, UploadSongActivity.class);
                    startActivity(intent);
                });
            }
        });


        View headerView = navigationView.getHeaderView(0);
        navUsername = (TextView) headerView.findViewById(R.id.name);
        navEmail = (TextView) headerView.findViewById(R.id.email);
        imageView = (ImageView) headerView.findViewById(R.id.himageview);
            profileupdate();
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        recyclerView = findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jcAudios = new ArrayList<>();
        jcPlayerView = findViewById(R.id.jcplayer);
        retrieveSongs();


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // ...
                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();
                adapter.notifyDataSetChanged();
                time++;
                HashMap<String,Object> map = new HashMap<>();
                map.put("times",String.valueOf(time));
              FirebaseDatabase.getInstance().getReference().child("Songs").child(list.get(position).getKey()).updateChildren(map);
            }
            // Sharing on long click
            @Override
            public void onItemLongClick(View view, int position) {
                String songLink = list.get(position).getSongUrl();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Listen to this amazing song!\n\n"+songLink);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this song!");
                startActivity(Intent.createChooser(shareIntent, "Share Song Link"));

            }
        }));

    }
    private void fetchSongsFromFirebase() {
        StorageReference songsFolderRef = storageReference.child("Audios").child("Songs");

        songsFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference songRef : listResult.getItems()) {
                        songRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            String songName = songRef.getName();
                            String songUrl = uri.toString();

                            list.add(new Song(songName, songUrl));
                            Song song = null ;
                            jcAudios.add(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));

                            if(jcAudios.size()>0){
                                adapter = new ListAdapter( list, MainActivity.this);
                                jcPlayerView.initPlaylist(jcAudios, null);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();
                        });
                    }
                })
                .addOnFailureListener(exception -> {
                });
    }

    // retrieving songs
    public void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    String key = ds.getKey();
                    list.add(new Song(key,song.getSongName(),song.getSongUrl(),song.getImageUrl(),song.getSongArtist(),song.getSongDuration(),song.getTimes()));
                    jcAudios.add(JcAudio.createFromURL(song.getSongName(), song.getSongUrl()));
                }
                if(jcAudios.size()>0){
                adapter = new ListAdapter( list, MainActivity.this);
                jcPlayerView.initPlaylist(jcAudios, null);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                   }
        });
    }

//handle permission for/from android uploadd
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.uploadItem){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                Intent intent = new Intent(this,UploadSongActivity.class);
                startActivity(intent);
            } else{
            if (validatePermissions()){
                Intent intent = new Intent(this,UploadSongActivity.class);
                startActivity(intent);
            }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
            profileupdate();

    }

    // Permission
    private boolean validatePermissions(){

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        checkPermission = true;
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        checkPermission = false;
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        return checkPermission;

    }
    private void profileupdate(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            String uid = FirebaseAuth.getInstance().getUid();
            CommonItem.uid = uid;
            FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot ds) {
                    for(DataSnapshot snapshot:ds.getChildren()){
                        String key = snapshot.getKey();
                        if(uid.equals(key)){
                            CommonItem.email = snapshot.child("email").getValue(String.class);
                            CommonItem.name = snapshot.child("name").getValue(String.class);
                             CommonItem.password = snapshot.child("password").getValue(String.class);


                            navUsername.setText(CommonItem.name);
                            navEmail.setText(CommonItem.email);
                            if(CommonItem.imagUrl.equals("default")){
                            }
                            else{
                                Glide.with(MainActivity.this).load(CommonItem.imagUrl).into(imageView);
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}