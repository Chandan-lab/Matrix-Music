package com.matrix.music;

public class Song {
    private String key,songName,songUrl;
    private String imageUrl, songArtist, songDuration;
    String times;


    public Song() {
    }

    public Song(String key, String songName, String songUrl, String imageUrl, String songArtist, String songDuration,String times) {
        this.key = key;
        this.songName = songName;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
        this.songArtist = songArtist;
        this.songDuration = songDuration;
        this.times = times;
    }
    public Song(String songName, String songUrl) {
        this.songName = songName;
        this.songUrl = songUrl;
    }
    public Song(String songName, String songUrl, String imageUrl, String artistName, String songDuration, String time) {
        this.songName = songName;
        this.songUrl = songUrl;
        this.imageUrl = imageUrl;
        this.songArtist = artistName;
        this.songDuration = songDuration;
        this.times = time;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
