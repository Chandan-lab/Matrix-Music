package com.matrix.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PopularSongs extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Song> list  = new ArrayList<>();
    ListAdapter adapter;
    JcPlayerView jcPlayerView;
    List<JcAudio> jcAudios;
    int time ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_songs);
        getSupportActionBar().setTitle("Popular Songs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage("Please Wait...");
        recyclerView = findViewById(R.id.songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jcAudios = new ArrayList<>();
        jcPlayerView = findViewById(R.id.jcplayer);
        retrieveSongs();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(PopularSongs.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
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

    public void retrieveSongs() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Songs");
        databaseReference.orderByChild("times").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Song song = ds.getValue(Song.class);
                    String key = ds.getKey();
                    list.add(new Song(key, song.getSongName(), song.getSongUrl(), song.getImageUrl(), song.getSongArtist(), song.getSongDuration(), song.getTimes()));

                }


                if(list.size()>0){
                    for(int i = 0 ;i<list.size();i++){
                    jcAudios.add(JcAudio.createFromURL(list.get(i).getSongName(), list.get(i).getSongUrl()));
                    }
                    adapter = new ListAdapter( list,PopularSongs.this);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu,menu);

            menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

}