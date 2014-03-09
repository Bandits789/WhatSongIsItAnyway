package com.android.whatsongisitanyway.models;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.android.whatsongisitanyway.R;

/**
 * ADT that represents a single game of WSIIA. Has the songs list, and can
 * switch to the next song. Plays music from /res/raw (eventually sdcard)
 * 
 */
public class Game {
	private final List<Music> songsList;
	private int currentSongIndex;
	private final Timer timer;
	private final Resources res;
	
	// in seconds
	private final int duration = 2 * 60;
	private final int skipPenalty = 2;
	
	/**
	 * Creates a new Game, grabs information about what songs are available to
	 * be played
	 */
	public Game(Resources resources) {
		currentSongIndex = -1;
		res = resources;
		songsList = populateSongs(); 
		timer = new Timer(duration);
	}

	/**
	 * Get the song information and populate the songsList (in random order)
	 * 
	 * @return the list of Music objects
	 */
	
    private List<Music> populateSongs() {
        
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		List<Music> songs = new ArrayList<Music>();

		Field[] fields = R.raw.class.getFields();
		
		// add the music object to the list
		// and add song metadata to the music object 
		for (int i = 0; i < fields.length; i++) {
			try { // to please eclipse

			    // Dummy initializing for now
			    String title = "0";
			    String artist = "0";
			    String genre = "0";
			    String duration = "0"; 
			    
			    int musicID = fields[i].getInt(fields[i]); 
				Log.d("musicID", "id: " + musicID);
				FileDescriptor fd = res.openRawResourceFd(musicID)
						.getFileDescriptor();
			    retriever.setDataSource(fd); 
//				
//		        // Get song metadata and store 
//				title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//
//				artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//	            
//	            genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
//	            
//	            duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); 
//
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
		Log.d(" Next song", "index:" + currentSongIndex);

		// make sure there are still songs available
		if (currentSongIndex >= songsList.size()) {
			return null;
		}

		return songsList.get(currentSongIndex);
	}

	/**
	 * Decrement the timer by skipPenalty number of seconds
	 */
	public void skipPenalty() {
		timer.decrement(skipPenalty);
	}

	/**
	 * Pause the game and the timer
	 */
	public void pause() {
		timer.pause();
	}

	/**
	 * Resume the game and the timer
	 */
	public void resume() {
		timer.run();
	}

	/**
	 * Return the amount of time left on the timer
	 * 
	 * @return time left in seconds
	 */
	public int timeLeft() {
		return timer.getTimeLeft();
	}

}
