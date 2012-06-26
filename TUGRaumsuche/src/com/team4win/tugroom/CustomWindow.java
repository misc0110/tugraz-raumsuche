package com.team4win.tugroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public abstract class CustomWindow extends Activity {
    private TextView title;
    private String current_text;

    // protected ImageView icon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

	setContentView(R.layout.main);

	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		R.layout.titlebar);

	title = (TextView) findViewById(R.id.titlebar_text);
    }

    void setTitlebarText(String text) {
	current_text = text;
	title.setText(text);
    }

    public String getTitlebarText() {
	return current_text;
    }

}
