package com.team4win.tugroom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class LocationIntentReceiver extends Activity {

    public String getGeoUrlLocation(Uri geo_url) {
	String location = "";
	String url = geo_url.toString();
	String[] parts = url.split("\\?|&");
	for (int i = 0; i < parts.length; i++) {
	    if (parts[i].startsWith("q=")) {
		location = parts[i].substring(2).trim();
	    }
	}
	return location;
    }

    public String getRoomNumber(String location) {
	Pattern pattern = Pattern.compile("\\(\\s*[A-Z]{2,}\\w+\\s*\\)");
	Matcher matcher = pattern.matcher(location);
	if (matcher.find()) {
	    return matcher.group().replace("(", "").replace(")", "").trim();
	} else
	    return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	Intent intent = getIntent();
	String location = getGeoUrlLocation(intent.getData());
	String room_nr = getRoomNumber(location);

	if (room_nr != "") {
	    final Intent room_intent = new Intent(getApplicationContext(),
		    ShowRooms.class);
	    room_intent.putExtra("RoomID", room_nr);
	    room_intent.putExtra("titlebar", getString(R.string.titlebar_suche));
	    startActivityForResult(room_intent, 0);
	} else {
	    final Intent room_intent = new Intent(getApplicationContext(),
		    ShowRooms.class);
	    room_intent.putExtra("Suche", location);
	    room_intent.putExtra("titlebar", getString(R.string.titlebar_suche));
	    startActivityForResult(room_intent, 0);
	}

	this.finish();
    }

}
