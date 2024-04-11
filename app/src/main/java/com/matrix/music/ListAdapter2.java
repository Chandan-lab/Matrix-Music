package com.matrix.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.ViewHolder> {
//this is the playlist recycler view
    ArrayList<Song> list;
    Context context;
    String name,id;
    public ListAdapter2(ArrayList<Song> list, Context context,String name,String id) {
        this.list = list;
        this.context = context;
        this.name = name;
        this.id = id;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @NonNull
    @Override
    public ListAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        listItem = layoutInflater.inflate(R.layout.songs_list_layout2, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter2.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song =list.get(position);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(20)
                .build();
        Picasso.get().load(list.get(position).getImageUrl()).transform(transformation).into(holder.thumbnail);
        holder.songName.setText(list.get(position).getSongName());
        holder.artistName.setText(list.get(position).getSongArtist());
        holder.sngDration.setText(list.get(position).getSongDuration());
        FirebaseDatabase.getInstance().getReference("playlist").child(CommonItem.uid).child(id).child("Songs").orderByChild("songName").equalTo(list.get(position).getSongName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.imageadd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        holder.imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            holder.imageadd.setVisibility(View.GONE);
                Song song = new Song(list.get(position).getSongName(),list.get(position).getSongUrl(),list.get(position).getImageUrl(),list.get(position).getSongArtist(),list.get(position).getSongDuration(),list.get(position).getTimes());
                FirebaseDatabase.getInstance().getReference("playlist").child(CommonItem.uid).child(id).child("Songs")
                        .push().setValue(song).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.i("database", "upload success");
                                Toast.makeText(context, "Song is added to "+name+" Playlist", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

//everything below wil be displayed
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        TextView sngDration;
        ImageView thumbnail;
        CardView cardView;
        ImageView imageadd;
        public ViewHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            thumbnail = itemView.findViewById(R.id.songThumbnail);
            artistName = itemView.findViewById(R.id.artistName);
            sngDration = itemView.findViewById(R.id.songDuration);
            cardView = itemView.findViewById(R.id.cardView);
            imageadd = itemView.findViewById(R.id.add);
        }

    }

    public void filterList(ArrayList<Song> filterlist) {

        list = filterlist;
        notifyDataSetChanged();
    }


}
