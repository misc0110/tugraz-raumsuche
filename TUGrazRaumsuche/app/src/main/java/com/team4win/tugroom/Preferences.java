package com.team4win.tugroom;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.KeyEvent;

public class Preferences extends CustomPreferenceWindow {

    String app_version = "";
    Context context = null;
    UpdateTask update = null;

    Handler asyncHandler = new Handler() {
	public void handleMessage(Message msg) {
	    super.handleMessage(msg);
	    switch (msg.what) {
	    case UpdateTask.UPDATE_DONE:
		updateUpdateStatus();
		break;
	    }
	}
    };

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	addPreferencesFromResource(R.xml.preferences);

	findPreference("about").setOnPreferenceClickListener(pref_about);

	findPreference("update_data").setOnPreferenceClickListener(
		new OnPreferenceClickListener() {
		    @Override
		    public boolean onPreferenceClick(Preference preference) {
			SharedPreferences prefs = context.getSharedPreferences(
				"update", 0);

			if (!prefs.getBoolean("is_running", false)) {
			    update = new UpdateTask(getApplicationContext(),
				    asyncHandler);
			    update.execute();
			} else {
			    Editor edit = prefs.edit();
			    edit.putBoolean("stop", true);
			    edit.putBoolean("is_running", false);
			    edit.commit();
			}
			updateUpdateStatus();
			return true;
		    }
		});

	findPreference("reset_db").setOnPreferenceClickListener(reset_database);

	context = this;

	try {
	    app_version = this.getPackageManager().getPackageInfo(
		    this.getPackageName(), 0).versionName;
	} catch (NameNotFoundException exception) {
	    exception.printStackTrace();
	}

	setTitlebarText(getString(R.string.titlebar_sett));
	updateUpdateStatus();
    }

    private void updateUpdateStatus() {

	SharedPreferences prefs = context.getSharedPreferences("update", 0);
	if (prefs.getBoolean("is_running", false)) {
	    findPreference("update_data").setTitle(
		    context.getString(R.string.update_running));
	    findPreference("update_data").setSummary(
		    context.getString(R.string.update_cancel));
	} else {
	    findPreference("update_data").setTitle(
		    context.getString(R.string.update_data));
	    findPreference("update_data").setSummary(
		    "Letztes Update: "
			    + UpdateHelper.getLastUpdateString(context));
	}
    }

    public boolean onKeyDown(int keyCode, KeyEvent msg) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    setResult(RESULT_OK);
	    this.finish();
	    return true;
	}

	return false;
    }

    private OnPreferenceClickListener reset_database = new OnPreferenceClickListener() {
	public boolean onPreferenceClick(Preference preference) {
	    Builder reset_dlg = new AlertDialog.Builder(context)
		    .setTitle(context.getString(R.string.warning))
		    .setMessage(context.getString(R.string.really_reset_db))
		    .setNegativeButton(context.getString(R.string.no),
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int which) {
				}
			    })
		    .setPositiveButton(context.getString(R.string.yes),
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				    UpdateHelper.resetDatabase(context);
				}
			    });
	    reset_dlg.show();
	    return false;
	}

    };

    private OnPreferenceClickListener pref_about = new OnPreferenceClickListener() {
	public boolean onPreferenceClick(Preference preference) {
	    Builder about_dlg = new AlertDialog.Builder(context)
		    .setTitle(
			    context.getString(R.string.app_name) + " v"
				    + app_version + ": "
				    + context.getString(R.string.about))
		    .setMessage(
			    context.getString(R.string.copyright_msg) + " "
				    + UpdateHelper.getLastUpdateString(context))
		    .setNeutralButton(context.getString(android.R.string.ok),
			    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
					int whichButton) {
				}
			    });
	    about_dlg.show();
	    return false;
	}

    };

}
