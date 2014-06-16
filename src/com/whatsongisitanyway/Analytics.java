package com.whatsongisitanyway;

import java.util.HashMap;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Google analytics setup
 */
public class Analytics extends Application {

	private static final String PROPERTY_ID = "UA-51968141-1";
	public static int GENERAL_TRACKER = 0;

	public enum TrackerName {
		APP_TRACKER // only one for now
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	public Analytics() {
		super();
	}

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = analytics.newTracker(PROPERTY_ID);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}
}
