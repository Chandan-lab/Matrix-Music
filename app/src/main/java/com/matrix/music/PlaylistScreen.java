package com.matrix.music;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlaylistScreen extends AppCompatActivity {

    ListView listView;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Playlist");

        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlaylistScreen.this, SongList.class);
                intent.putExtra("name", list.get(position));
                intent.putExtra("id", ids.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(PlaylistScreen.this)
                        .setTitle("Delete Playlist")
                        .setMessage("Are you sure you want to delete this playlist?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the selected playlist
                                deletePlaylist(ids.get(position));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("playlist").child(CommonItem.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                ids.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    String id = ds.child("id").getValue(String.class);
                    list.add(name);
                    ids.add(id);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlaylistScreen.this, android.R.layout.simple_list_item_1, android.R.id.text1, list) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        textView.setTextColor(Color.WHITE);
                        return view;
                    }
                };
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deletePlaylist(String playlistId) {
        FirebaseDatabase.getInstance().getReference().child("playlist").child(CommonItem.uid)
                .child(playlistId).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(PlaylistScreen.this, "Playlist deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optionally refresh your list here
                });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Enter playlist name");

        final EditText editText = new EditText(this);
        alertDialogBuilder.setView(editText);

        alertDialogBuilder.setPositiveButton(" Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String enteredText = editText.getText().toString();
                if (enteredText.isEmpty()) {
                    editText.setError("Enter Playlist name");
                } else {
                    final int random = new Random().nextInt(401);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("uid", CommonItem.uid);
                    map.put("name", enteredText);
                    map.put("id", String.valueOf(random));
                    FirebaseDatabase.getInstance().getReference().child("playlist").child(CommonItem.uid).child(String.valueOf(random)).setValue(map);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
