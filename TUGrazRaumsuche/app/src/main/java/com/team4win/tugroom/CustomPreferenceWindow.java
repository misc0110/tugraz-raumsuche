package com.team4win.tugroom;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.TextView;
import com.team4win.tugroom.R;

public class CustomPreferenceWindow extends PreferenceActivity {
    private TextView title;
    private String current_text;
    //protected ImageView icon;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
 
        super.onCreate(savedInstanceState);

	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
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


