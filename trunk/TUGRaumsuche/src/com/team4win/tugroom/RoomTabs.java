package com.team4win.tugroom;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class RoomTabs extends CustomTabWindow {
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.tabs);

	final Bundle extras = getIntent().getExtras();

	setTitlebarText(extras.getString(Property.NAME));

	Resources res = getResources();
	TabHost tabHost = getTabHost();
	TabHost.TabSpec spec;
	Intent intent;

	// create intent to launch tab activity
	intent = new Intent().setClass(this, DetailView.class);
	intent.putExtras(extras);

	// each tab needs a tabspec
	spec = tabHost
		.newTabSpec("detail")
		.setIndicator(getString(R.string.tab_detail),
			res.getDrawable(R.drawable.details)).setContent(intent);
	tabHost.addTab(spec);

	intent = new Intent().setClass(this, LageView.class);
	intent.putExtras(extras);
	spec = tabHost
		.newTabSpec("lage")
		.setIndicator(getString(R.string.tab_map),
			res.getDrawable(R.drawable.location))
		.setContent(intent);
	tabHost.addTab(spec);

	intent = new Intent().setClass(this, UsageView.class);
	intent.putExtras(extras);
	spec = tabHost
		.newTabSpec("usage")
		.setIndicator(getString(R.string.tab_usage),
			res.getDrawable(R.drawable.usage)).setContent(intent);
	tabHost.addTab(spec);
    }

}
