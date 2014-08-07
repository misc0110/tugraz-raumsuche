/*
    TU Graz Raumsuche - Offline Raumsuche fuer die Technische Universitaet Graz
    Copyright (C) 2012  Michael Schwarz, Mark Bergmoser, Stefan More, Christopher Kaponig

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.team4win.tugroom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TUGRoomActivity extends CustomWindow {
    Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.mainscreen);

	setTitlebarText(getString(R.string.titlebar_main));
	this.getWindow().setSoftInputMode(
		WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	context = this;

	final ImageButton search_btn = (ImageButton) findViewById(R.id.btn_search);
	final EditText search_field = (EditText) findViewById(R.id.search_field);

	search_btn.setEnabled(false);
	search_btn.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		startSearch(search_field.getText().toString().trim());
	    }
	});

	// text change listener to enable/disable search button
	search_field.setSingleLine();
	search_field.setOnEditorActionListener(new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId,
		    KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		    startSearch(v.getText().toString().trim());
		}
		return true;
	    }
	});
	search_field.addTextChangedListener(new TextWatcher() {

	    public void afterTextChanged(Editable s) {
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {
	    }

	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {
		if (search_field.getText().toString().trim().length() > 0)
		    search_btn.setEnabled(true);
		else
		    search_btn.setEnabled(false);
	    }

	});

	((Button) findViewById(R.id.btn_hs))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, ShowRooms.class);
			intent.putExtra("Kategorie", "Hörsaal");
			intent.putExtra("titlebar",
				getString(R.string.titlebar_hoersaal));
			startActivity(intent);
		    }
		});

	((Button) findViewById(R.id.btn_zeichen))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, ShowRooms.class);
			intent.putExtra("Kategorie", "Zeichensaal");
			intent.putExtra("titlebar",
				getString(R.string.titlebar_zeichen));
			startActivity(intent);
		    }
		});

	((Button) findViewById(R.id.btn_labor))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, ShowRooms.class);
			intent.putExtra("Kategorie", "Labor");
			intent.putExtra("titlebar",
				getString(R.string.titlebar_labor));
			startActivity(intent);
		    }
		});

	((Button) findViewById(R.id.btn_seminar))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, ShowRooms.class);
			intent.putExtra("Kategorie", "Seminarraum");
			intent.putExtra("titlebar",
				getString(R.string.titlebar_seminar));
			startActivity(intent);
		    }
		});

	((Button) findViewById(R.id.btn_fav))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, ShowRooms.class);
			intent.putExtra("Favoriten", "Favoriten");
			intent.putExtra("titlebar",
				getString(R.string.titlebar_favoriten));
			startActivity(intent);
		    }
		});

	((Button) findViewById(R.id.btn_sett))
		.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
			final Intent intent = new Intent(context, Preferences.class);
			startActivityForResult(intent, 0);
		    }
		});
	if (savedInstanceState == null)
	    UpdateHelper.setUpdateDialogShown(false);
	else
	    UpdateHelper.setUpdateDialogShown(savedInstanceState.getBoolean(
		    "reminder_shown", false));

	// check if user should update
	if (UpdateHelper.getLastUpdateInDays(this) > 90) {
	    if (!UpdateHelper.wasUpdateDialogShown()) {
		UpdateHelper.showUpdateDialog(this);
	    }
	}

    }

    protected void onSaveInstanceState(Bundle outState) {
	outState.putBoolean("reminder_shown",
		UpdateHelper.wasUpdateDialogShown());
	super.onSaveInstanceState(outState);
    }

    void startSearch(String query) {
	final Intent intent = new Intent(context, ShowRooms.class);
	intent.putExtra("Suche", query);
	intent.putExtra("titlebar", getString(R.string.titlebar_suche));
	startActivity(intent);
    }

}
