package com.android.whatsongisitanyway;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.util.Log;
import android.annotation.SuppressLint;
import android.media.MediaMetadataRetriever;

import com.android.helloworld.R;

/**
 * ADT that represents a single game of WSIIA. Has the songs list, and can
 * switch to the next song. Plays music from /res/raw (eventually sdcard)
 * 
 */
public class Game {
	private List<Music> songsList;
	private int currentSongIndex;
	MediaMetadataRetriever retriever;

	/**
	 * Creates a new Game, grabs information about what songs are available to
	 * be played
	 */
	public Game() {
		currentSongIndex = -1;
		songsList = populateSongs();
	}

	/**
	 * Get the song information and populate the songsList (in random order)
	 * 
	 * @return the list of Music objects
	 */
	@SuppressLint("NewApi")
    private List<Music> populateSongs() {
		List<Music> songs = new ArrayList<Music>();

		Field[] fields = R.raw.class.getFields();
		
		retriever = new MediaMetadataRetriever(); 

		// add the music object to the list
		// and add song metadata to the music object 
		for (int i = 0; i < fields.length; i++) {
			try { // to please eclipse
				int musicID = fields[i].getInt(fields[i]); 
				retriever.setDataSource(Integer.toString(musicID)); 

		        // Get song metadata and store 
	            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

				String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
	            
	            String genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
	            
	            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); 

	            Music music = new Music(musicID, title, artist, duration, genre); 
	            
	            Log.d(" Title:",title+" Artist:"+artist+" Genre:"+genre+" Duration:"+duration); 

				songs.add(music);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// randomize!
		long seed = System.nanoTime();
		Collections.shuffle(songs, new Random(seed));

		return songs;
	}

	/**
	 * Finds the next Music object to play, or null if we have reached the end
	 * 
	 * @return the next Music to play or null
	 */
	public Music getNextSong() {
		// we're going to the next song
		++currentSongIndex;

		// make sure there are still songs available
		if (currentSongIndex >= songsList.size()) {
			return null;
		}

		return songsList.get(currentSongIndex);
	}

}
