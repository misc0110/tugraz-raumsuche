package com.team4win.tugroom;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import com.team4win.tugroom.R;

public class CustomTabWindow extends TabActivity {
    protected TextView title;
    //protected ImageView icon;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        
        setContentView(R.layout.tabs);
        
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
 
        title = (TextView) findViewById(R.id.titlebar_text);
    }
    
    void setTitlebarText(String text) {
	title.setText(text);
    }
    
}


