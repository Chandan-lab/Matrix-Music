package com.matrix.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class    ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private ArrayList<Song> list;
    private Context context;

    public ListAdapter(ArrayList<Song> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.songs_list_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(15)
                .build();

        Picasso.get()
                .load(list.get(position).getImageUrl())
                .transform(transformation)
                .into(holder.thumbnail);

        holder.songName.setText(list.get(position).getSongName());
        holder.artistName.setText(list.get(position).getSongArtist());
        holder.songDuration.setText(list.get(position).getSongDuration());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        TextView songDuration;
        ImageView thumbnail;
        CardView cardView;
        ImageView currentPlayingSong;

        public ViewHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            thumbnail = itemView.findViewById(R.id.songThumbnail);
            artistName = itemView.findViewById(R.id.artistName);
            songDuration = itemView.findViewById(R.id.songDuration);
            cardView = itemView.findViewById(R.id.cardView);
            currentPlayingSong = itemView.findViewById(R.id.currentlyPlaying);
        }
    }

    public void filterList(ArrayList<Song> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}
