package com.team4win.tugroom;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailView extends Activity {
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.room_detail);
	setRequestedOrientation(1);

	final Bundle extras = getIntent().getExtras();

	if (extras.getString(Property.ROOM_ID) != null)
	    ((TextView) findViewById(R.id.room_nr)).setText(extras
		    .getString(Property.ROOM_ID));
	if (extras.getString(Property.NAME) != null)
	    ((TextView) findViewById(R.id.room_name)).setText(extras
		    .getString(Property.NAME));
	if (extras.getString(Property.STREET) != null)
	    ((TextView) findViewById(R.id.location)).setText(extras.getString(
		    Property.STREET).replace(", ", ",\n"));
	if (extras.getString(Property.SEATS) != null)
	    ((TextView) findViewById(R.id.seats)).setText(extras
		    .getString(Property.SEATS));
	if (extras.getString(Property.EQUIPMENT) != null
		&& extras.getString(Property.EQUIPMENT).length() > 3) {
	    ((TextView) findViewById(R.id.equip)).setText(extras.getString(
		    Property.EQUIPMENT).replace('|', '\n'));
	}

	try {
	    Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(
		    locationToFilename(extras.getString(Property.STREET))));
	    ((ImageView) findViewById(R.id.photo)).setImageBitmap(bitmap);
	} catch (Exception exception) {
	    ((ImageView) findViewById(R.id.photo))
		    .setImageResource(R.drawable.tulogo);
	}

    }

    public String locationToFilename(String location) {
	location = location.replace("ß", "ss").replace("ü", "ue")
		.replace("ä", "ae").replace("ö", "oe").replace("/", "")
		.replaceAll(" +", " ").trim();
	String[] parts = location.split(",");
	String[] parts1 = parts[0].trim().split(" ");
	String file = "fotos/" + parts1[0].trim() + parts1[1].trim()
		+ "_foto.jpg";
	return file;
    }
}
