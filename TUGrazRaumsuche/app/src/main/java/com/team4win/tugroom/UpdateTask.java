package com.team4win.tugroom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateTask extends AsyncTask<Void, Integer, Void> {
    private NotificationHelper notification_helper;
    private Context context;
    private SharedPreferences prefs;
    private boolean no_internet;
    private Handler handler;

    public static final int UPDATE_DONE = 1;
    public static final int UPDATE_STOP = 2;

    public UpdateTask(Context context, Handler async_handler) {
	this.context = context;
	handler = async_handler;
	notification_helper = new NotificationHelper(context);
	prefs = context.getSharedPreferences("update", 0);
	Editor edit = prefs.edit();
	edit.putBoolean("is_running", true);
	edit.putBoolean("stop", false);
	edit.commit();
    }

    protected void onPreExecute() {
	// Create the notification in the statusbar
	notification_helper.createNotification();
	no_internet = false;
    }

    public boolean isOnline() {
	ConnectivityManager connectivity = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (connectivity == null
		|| connectivity.getActiveNetworkInfo() == null
		|| !connectivity.getActiveNetworkInfo()
			.isConnectedOrConnecting()) {
	    return false;
	}
	return true;
    }

    public long getAvailableDiskSpace() {
	File path = Environment.getDataDirectory();
	StatFs stat = new StatFs(path.getPath());
	long block_size = stat.getBlockSize();
	long available_blocks = stat.getAvailableBlocks();
	return block_size * available_blocks;
    }

    public boolean downloadFile(String download_url) {
	long total = 0;
	int progress = 0, _progress = 0;
	long file_length = 1024;
	FileOutputStream output = null;
	InputStream input = null;
	HttpGet http_request = null;
	HttpParams http_parameters = new BasicHttpParams();
	int timeout_connection = 2500;

	HttpConnectionParams.setConnectionTimeout(http_parameters,
		timeout_connection);
	int timeout_socket = 2500;
	HttpConnectionParams.setSoTimeout(http_parameters, timeout_socket);

	try {
	    http_request = new HttpGet(new URI(download_url));
	    DefaultHttpClient httpClient = new DefaultHttpClient(
		    http_parameters);
	    HttpResponse response = (HttpResponse) httpClient
		    .execute(http_request);

	    int status_code = response.getStatusLine().getStatusCode();
	    if (status_code == HttpStatus.SC_OK) {
		HttpEntity entity = response.getEntity();
		output = context.openFileOutput("rooms.zip",
			Context.MODE_PRIVATE);
		input = entity.getContent();
		file_length = entity.getContentLength();

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = input.read(buffer)) != -1) {
		    total += length;
		    output.write(buffer, 0, length);
		    progress = (int) (total * 100 / file_length);
		    if (progress != _progress) {
			onProgressUpdate(progress);
			_progress = progress;
		    }

		    if (notification_helper.isStopped()) {
			return false;
		    }
		}
		output.flush();
		output.close();
		input.close();
	    }
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    @Override
    protected Void doInBackground(Void... v) {
	// update stuff
	ContextWrapper wrapper = new ContextWrapper(context);
	String home_dir = wrapper.getFilesDir().getAbsolutePath();

	if (!isOnline()) {
	    no_internet = true;
	    return null;
	} else {
	    no_internet = false;
	}

	if (!downloadFile(context.getString(R.string.update_url))) {
	    return null;
	}

	notification_helper.setProgressBarIndeterminate(true);
	notification_helper.setImageSync();
	onProgressUpdate(0);

	InputSource file = Unzip.getInputSource(home_dir + "/rooms.zip");

	XMLParser parser = new XMLParser();
	List<Room> rooms = parser.getRooms(
		file,
		Arrays.asList(context.getResources().getTextArray(
			R.array.room_categories)));

	DBLoader loader = new DBLoader(context);
	loader.updateRooms(rooms);

	UpdateHelper.setLastUpdate(context, (new Date()).getTime());

	return null;
    }

    protected void onProgressUpdate(Integer... progress) {
	// update status bar
	notification_helper.progressUpdate(progress[0]);
    }

    protected void onPostExecute(Void result) {
	// task is done
	notification_helper.completed();
	Editor edit = prefs.edit();
	edit.putBoolean("is_running", false);
	edit.commit();

	if (no_internet)
	    Toast.makeText(context,
		    context.getString(R.string.no_internet_connection),
		    Toast.LENGTH_SHORT).show();

	if (handler != null) {
	    Message msg = Message.obtain();
	    msg.what = UPDATE_DONE;
	    handler.sendMessage(msg);
	}
    }

    public class NotificationHelper {
	private Context ctx;
	private int NOTIFICATION_ID = 1;
	private Notification notification;
	private NotificationManager notification_manager;
	private PendingIntent content_intent;
	private RemoteViews content_view;
	private boolean notification_is_indeterminate;

	public NotificationHelper(Context context) {
	    ctx = context;
	    notification_is_indeterminate = false;
	}

	public void createNotification() {
	    // get the notification manager
	    notification_manager = (NotificationManager) ctx
		    .getSystemService(Context.NOTIFICATION_SERVICE);

	    // create the notification
	    int icon = android.R.drawable.stat_sys_download;
	    CharSequence tickerText = ctx
		    .getString(R.string.update_notification);
	    long when = System.currentTimeMillis();
	    notification = new Notification(icon, tickerText, when);

	    content_view = new RemoteViews(ctx.getPackageName(),
		    R.layout.update_notification);
	    content_view.setImageViewResource(R.id.image,
		    android.R.drawable.stat_sys_download);
	    content_view.setTextViewText(R.id.title,
		    ctx.getString(R.string.update_notification));
	    content_view.setTextViewText(R.id.percent, "0%");
	    content_view.setProgressBar(R.id.update_progress, 100, 0,
		    notification_is_indeterminate);
	    notification.contentView = content_view;

	    Intent notificationIntent = new Intent();
	    content_intent = PendingIntent.getActivity(ctx, 0,
		    notificationIntent, 0);
	    notification.contentIntent = content_intent;

	    // notification should be an "ongoing event"
	    notification.flags = Notification.FLAG_ONGOING_EVENT;

	    // show it
	    notification_manager.notify(NOTIFICATION_ID, notification);
	}

	public void setProgressBarIndeterminate(boolean is_indeterminate) {
	    notification_is_indeterminate = is_indeterminate;
	    content_view.setProgressBar(R.id.update_progress, 100, 0,
		    is_indeterminate);
	    content_view.setTextViewText(R.id.percent, is_indeterminate ? ""
		    : "0%");
	}

	public void setImageSync() {
	    content_view.setImageViewResource(R.id.image,
		    android.R.drawable.stat_notify_sync);
	}

	public void progressUpdate(int percentageComplete) {
	    content_view.setProgressBar(R.id.update_progress, 100,
		    percentageComplete, notification_is_indeterminate);
	    if (!notification_is_indeterminate)
		content_view.setTextViewText(R.id.percent, percentageComplete
			+ "%");
	    notification_manager.notify(NOTIFICATION_ID, notification);
	}

	public void completed() {
	    notification_manager.cancel(NOTIFICATION_ID);
	}

	public boolean isStopped() {
	    return prefs.getBoolean("stop", false);
	}
    }
}
