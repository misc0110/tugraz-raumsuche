package com.team4win.tugroom;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LageView extends Activity {
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.room_lage);

	final Bundle extras = getIntent().getExtras();

	String mark_x = extras.getString(Property.MARKER_X);
	String mark_y = extras.getString(Property.MARKER_Y);
	if (mark_x == null)
	    mark_x = "0";
	if (mark_y == null)
	    mark_y = "0";

	final Bitmap plan = getMarkedBitmap(extras.getString(Property.IMAGE)
		+ ".gif", Integer.parseInt(mark_x), Integer.parseInt(mark_y));

	final ZoomImageView img = (ZoomImageView) findViewById(R.id.img_lage);
	final Button change = (Button) findViewById(R.id.toggle_plan);
	String street = extras.getString(Property.STREET);
	if (street == null)
	    street = "";
	final Bitmap map = getBitmapFromAsset(locationToFilename(street));
	if (map == null)
	    change.setVisibility(View.INVISIBLE);
	else
	    change.setVisibility(View.VISIBLE);

	if (plan != null)
	    img.setImage(plan);
	else
	    img.setImageResource(R.drawable.no_plan);

	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(getApplicationContext());
	if (prefs.getBoolean("map_instead_route", false)) {
	    ((Button) findViewById(R.id.start_navi))
		    .setText(getString(R.string.map_maps));
	}

	change.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		if (change.getText().equals(getString(R.string.map_map))) {
		    img.setImage(map);
		    change.setText(getString(R.string.map_room));
		    change.setCompoundDrawablesWithIntrinsicBounds(
			    R.drawable.room, 0, 0, 0);
		} else {
		    if (plan != null)
			img.setImage(plan);
		    else
			img.setImageResource(R.drawable.no_plan);
		    change.setText(getString(R.string.map_map));
		    change.setCompoundDrawablesWithIntrinsicBounds(
			    R.drawable.location, 0, 0, 0);
		}
	    }
	});

	((Button) findViewById(R.id.start_navi))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
			Intent navigation;
			if (prefs.getBoolean("map_instead_route", false)) {
			    navigation = new Intent(
				    Intent.ACTION_VIEW,
				    Uri.parse("geo:0,0?q="
					    + getAddress(
						    extras.getString(Property.STREET),
						    extras.getString(Property.CITY))));
			} else {
			    navigation = new Intent(
				    Intent.ACTION_VIEW,
				    Uri.parse("http://maps.google.com/maps?daddr="
					    + getAddress(
						    extras.getString(Property.STREET),
						    extras.getString(Property.CITY))));
			}
			startActivity(navigation);
		    }

		});
    }

    private Bitmap getMarkedBitmap(String name, int x, int y) {
	Bitmap plan = getBitmapFromAsset(name);
	if (plan == null)
	    return null;

	// create canvas to draw on the bitmap
	Canvas canvas = new Canvas(plan);
	Paint paint = new Paint();
	paint.setColor(Color.RED);
	canvas.drawCircle(x, y, 11, paint);

	return plan;
    }

    private Bitmap getBitmapFromAsset(String strName) {
	AssetManager assetManager = getAssets();

	InputStream istr;
	try {
	    istr = assetManager.open(strName);
	    Bitmap bitmap = BitmapFactory.decodeStream(istr).copy(
		    Bitmap.Config.RGB_565, true);
	    return bitmap;
	} catch (IOException exception) {
	}
	return null;
    }

    public String locationToFilename(String location) {
	location = location.replace("ß", "ss").replace("ü", "ue").replace("ä", "ae")
		.replace("ö", "oe").replace("/", "").replaceAll(" +", " ")
		.trim();
	String[] parts = location.split(",");
	String[] parts1 = parts[0].trim().split(" ");
	String file = "lageplaene/";
	if (parts1.length == 1)
	    file += parts1[0].trim();
	else if (parts1.length == 2)
	    file += parts1[0].trim() + parts1[1].trim();
	file += ".gif";
	return file;
    }

    public String getAddress(String street, String city) {
	if (street == null)
	    return "";
	String[] parts = street.split(",");
	return parts[0].trim() + (city != null ? ", " + city.trim() : "");
    }
}
