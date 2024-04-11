package com.matrix.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddSongToPlayslist extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Song> list  = new ArrayList<>();
    ListAdapter2 adapter;
    ProgressDialog progressDialog;
    String name,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song_to_playslist);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle("Add song in playlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        retrieveSongs();
    }
    //getdata from firebase
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

                }

                if(list.size()>0){
                    adapter = new ListAdapter2( list,AddSongToPlayslist.this,name,id);
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
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}