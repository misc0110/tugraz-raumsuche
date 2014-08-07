package com.team4win.tugroom;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class UpdateHelper {
    private static boolean reminder_shown = false;

    public static long getLastUpdate(Context ctx) {
	SharedPreferences prefs = ctx.getSharedPreferences("update", 0);
	return prefs.getLong("last_update", 1332288000000l);
    }

    public static int getLastUpdateInDays(Context ctx) {
	return (int) (((new Date()).getTime() - getLastUpdate(ctx)) / (1000 * 60 * 60 * 24));
    }

    public static String getLastUpdateString(Context ctx) {
	String format = ctx.getString(R.string.date_format);
	SimpleDateFormat date_format = new SimpleDateFormat(format);
	return date_format.format(getLastUpdate(ctx));
    }

    public static void setLastUpdate(Context ctx, long time) {
	SharedPreferences prefs = ctx.getSharedPreferences("update", 0);
	Editor edit = prefs.edit();
	edit.putLong("last_update", time);
	edit.commit();
    }

    public static void showUpdateDialog(final Context context) {
	int days = getLastUpdateInDays(context);
	LayoutInflater inflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	final View update = inflater.inflate(R.layout.update_dialog, null);

	Builder update_dlg = new AlertDialog.Builder(context)
		.setView(update)
		.setTitle(context.getString(R.string.update_title))
		.setMessage(
			context.getString(R.string.update_message_1) + " "
				+ days + " "
				+ context.getString(R.string.update_message_2))
		.setPositiveButton(context.getString(R.string.yes),
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,
				    int whichButton) {
				new UpdateTask(context, null).execute();
				setNeverShowUpdateDialog(context,
					((CheckBox) update
						.findViewById(R.id.dont_ask))
						.isChecked());
				reminder_shown = true;
			    }
			})
		.setNegativeButton(context.getString(R.string.no),
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,
				    int whichButton) {
				setNeverShowUpdateDialog(context,
					((CheckBox) update
						.findViewById(R.id.dont_ask))
						.isChecked());
				reminder_shown = true;
			    }
			});
	if (!getNeverShowUpdateDialog(context)) {
	    update_dlg.show();
	}
    }

    public static void setNeverShowUpdateDialog(Context context,
	    boolean do_not_show) {
	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(context);
	Editor edit = prefs.edit();
	edit.putBoolean("update_reminder", !do_not_show);
	edit.commit();
    }

    public static boolean getNeverShowUpdateDialog(Context context) {
	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(context);
	return !prefs.getBoolean("update_reminder", true);
    }

    public static boolean wasUpdateDialogShown() {
	return reminder_shown;
    }

    public static void setUpdateDialogShown(boolean shown) {
	reminder_shown = shown;
    }

    public static void resetDatabase(Context context) {
	(new DBLoader(context)).resetDatabase();
	SharedPreferences prefs = context.getSharedPreferences("update", 0);
	Editor edit = prefs.edit();
	edit.remove("last_update");
	edit.commit();
	Toast.makeText(context, context.getString(R.string.database_reset),
		Toast.LENGTH_SHORT).show();
    }
}
