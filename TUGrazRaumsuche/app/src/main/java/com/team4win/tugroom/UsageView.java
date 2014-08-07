package com.team4win.tugroom;

import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Picture;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;

public class UsageView extends Activity {
	WebView page;
	String room;
	ProgressDialog plan_load_progress;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.room_usage);

		final Bundle extras = getIntent().getExtras();
		room = extras.getString("RaumnummerIntern");

		page = (WebView) findViewById(R.id.wv_usage);
		// enable javascript
		WebSettings settings = page.getSettings();
		settings.setJavaScriptEnabled(true);
		// enable viewport
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
		// allow zoom
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		// intercept redirection
		page.setWebViewClient(new TUGWebViewClient());
		// dismiss progress bar when page loaded
		page.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				try {
					plan_load_progress.dismiss();
				} catch (Exception e) {
				}
			}
		});
		// make alerts work
		page.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				try {
					plan_load_progress.dismiss();
				} catch (Exception e) {
				}
				return super.onJsAlert(view, url, message, result);
			}
		});

		loadPlan();
	}

	void loadPlan() {
		Runnable viewPlan = new Runnable() {
			@Override
			public void run() {
				page.loadUrl("https://online.tugraz.at/tug_online/wbKalender.wbRessource?pResNr="
						+ room);
			}
		};
		Thread thread = new Thread(null, viewPlan, "LoadPlan");
		thread.start();
		plan_load_progress = ProgressDialog.show(this,
				getString(R.string.please_wait),
				getString(R.string.loading_data_online), true, true,
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// finish();
						dialog.dismiss();
					}
				});
	}

	void injectCSS(String css) {
		String contents = css.replace('\r', ' ').replace('\n', ' ')
				.replace("\"", "\\\"");
		String url = "javascript:(function injectCSS(){var s=document.createElement(\"style\"); s.setAttribute(\"type\",\"text/css\"); s.innerHTML=\""
				+ contents
				+ "\";"
				+ "document.getElementsByTagName(\"head\")[0].appendChild(s);})();";

		page.loadUrl(url);
	}

	void injectJS(String js) {
		String contents = js.replace('\r', ' ').replace('\n', ' ')
				.replace("\"", "\\\"");
		String url = "javascript:(function injectJS(){var s=document.createElement(\"script\"); s.setAttribute(\"type\",\"text/javascript\"); s.innerHTML=\""
				+ contents
				+ "\";"
				+ "document.getElementsByTagName(\"head\")[0].appendChild(s);})();";

		page.loadUrl(url);
	}

	void markTime() {
		String time;
		Calendar cal = Calendar.getInstance();
		time = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
				((int) cal.get(Calendar.MINUTE) / 15) * 15);
		page.loadUrl("javascript:(function foo(){markTime('" + time + "');})()");
	}

	private class TUGWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// inject css to remove unnecessary content
			injectCSS(".pageObjectNoTopBorder, .bodyTable, table[class=\"wr100\"] {display: none;} ");

			// inject function to mark the current time
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			boolean mark = prefs.getBoolean("mark_time", true);
			if (mark) {
				injectJS("function markTime(t) {tr = document.getElementsByTagName(\"tr\");"
						+ "for(i = 0; i < tr.length; i++) {"
						+ "if(tr[i].className != \"z1\" && tr[i].className != \"z0\") continue;"
						+ "tds = tr[i].getElementsByTagName(\"td\");"
						+ "for(j = 0; j < tds.length; j++) {"
						+ "if(tds[j].width == \"1%\") {"
						+ "if(tds[j].innerHTML.indexOf(t) != -1) {"
						+ "tr[i].style.backgroundColor = \"#00c000\";"
						+ "return;}}}}}");
				markTime();
			}
		}
	}
}
