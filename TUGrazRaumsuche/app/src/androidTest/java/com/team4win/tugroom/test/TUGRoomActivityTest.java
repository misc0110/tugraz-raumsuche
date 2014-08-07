package com.team4win.tugroom.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xml.sax.InputSource;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;
import com.team4win.tugroom.FavoriteManager;
import com.team4win.tugroom.LocationIntentReceiver;
import com.team4win.tugroom.NaturalRoomComparator;
import com.team4win.tugroom.Property;
import com.team4win.tugroom.Room;
import com.team4win.tugroom.TUGRoomActivity;
import com.team4win.tugroom.UpdateHelper;
import com.team4win.tugroom.UpdateTask;
import com.team4win.tugroom.XMLParser;
import com.team4win.tugroom.ZoomImageView;

public class TUGRoomActivityTest extends
	ActivityInstrumentationTestCase2<TUGRoomActivity> {

    private TUGRoomActivity m_activity;
    private Button btn_hs, btn_sem, btn_zeichen, btn_labor, btn_settings,
	    btn_fav;
    private Solo solo;
    private PendingIntent restart_intent;

    public TUGRoomActivityTest() {
	super("com.team4win.tugroom", TUGRoomActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();

	solo = new Solo(getInstrumentation(), getActivity());

	m_activity = this.getActivity();
	restart_intent = PendingIntent.getActivity(m_activity.getBaseContext(),
		0, new Intent(m_activity.getIntent()), m_activity.getIntent()
			.getFlags());

	if (solo.searchText("Update")) {
	    solo.clickOnText("Nein");
	}

	btn_hs = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_hs);
	btn_sem = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_seminar);
	btn_zeichen = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_zeichen);
	btn_labor = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_labor);
	btn_settings = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_sett);
	btn_fav = (Button) m_activity
		.findViewById(com.team4win.tugroom.R.id.btn_fav);

	// reset preferences
	SharedPreferences prefs = PreferenceManager
		.getDefaultSharedPreferences(m_activity);
	Editor edit = prefs.edit();
	edit.putBoolean("search_hoersaal", true);
	edit.putBoolean("search_seminar", true);
	edit.putBoolean("search_labore", true);
	edit.putBoolean("search_zeichen", true);
	edit.commit();
    }

    @Override
    public void tearDown() throws Exception {
	solo.finishOpenedActivities();
    }

    public void testPreconditions() {
	assertNotNull(btn_hs);
	assertNotNull(btn_sem);
	assertNotNull(btn_zeichen);
	assertNotNull(btn_labor);
	assertNotNull(btn_settings);
	assertNotNull(btn_fav);
    }

    public void testButtonText() {
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_hoersaal),
		(String) btn_hs.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_labor),
		(String) btn_labor.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_zeichen),
		(String) btn_zeichen.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_seminar),
		(String) btn_sem.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_favoriten),
		(String) btn_fav.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_einstellungen),
		(String) btn_settings.getText());
    }

    public void testTitlebar() {
	solo.sleep(100);
	TextView tb = (TextView) solo
		.getView(com.team4win.tugroom.R.id.titlebar_text);
	assertNotNull(tb);
	assertEquals(m_activity.getTitlebarText(), tb.getText());
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.titlebar_main),
		m_activity.getTitlebarText());
    }

    private void _testButton(int btn_name, int titlebar) {
	for (int i = 0; i <= 1; i++) {
	    solo.setActivityOrientation(i);
	    solo.clickOnButton(m_activity.getString(btn_name));
	    solo.assertCurrentActivity("Button not working",
		    com.team4win.tugroom.ShowRooms.class);
	    solo.sleep(500);
	    assertEquals(m_activity.getString(titlebar),
		    ((TextView) (solo
			    .getView(com.team4win.tugroom.R.id.titlebar_text)))
			    .getText());
	    solo.sleep(100);
	    // solo.goBack();
	    solo.sendKey(KeyEvent.KEYCODE_BACK);
	    assertTrue(solo.waitForActivity("TUGRoomActivity", 10000));
	    solo.assertCurrentActivity("Did not go back", TUGRoomActivity.class);
	}
    }

    public void testButtonHoersaal() {
	_testButton(com.team4win.tugroom.R.string.button_hoersaal,
		com.team4win.tugroom.R.string.titlebar_hoersaal);
    }

    public void testButtonLabor() {
	_testButton(com.team4win.tugroom.R.string.button_labor,
		com.team4win.tugroom.R.string.titlebar_labor);
    }

    public void testButtonSeminar() {
	_testButton(com.team4win.tugroom.R.string.button_seminar,
		com.team4win.tugroom.R.string.titlebar_seminar);
    }

    public void testButtonZeichen() {
	_testButton(com.team4win.tugroom.R.string.button_zeichen,
		com.team4win.tugroom.R.string.titlebar_zeichen);
    }

    public void testButtonEinstellungen() {
	for (int i = 0; i <= 1; i++) {
	    solo.setActivityOrientation(i);
	    solo.clickOnButton(m_activity
		    .getString(com.team4win.tugroom.R.string.button_einstellungen));
	    solo.assertCurrentActivity("Button not working",
		    com.team4win.tugroom.Preferences.class);
	    solo.sleep(500);
	    assertEquals(
		    m_activity
			    .getString(com.team4win.tugroom.R.string.titlebar_sett),
		    ((TextView) (solo
			    .getView(com.team4win.tugroom.R.id.titlebar_text)))
			    .getText());
	    solo.goBack();
	    solo.assertCurrentActivity("Did not go back", TUGRoomActivity.class);
	}
    }

    public void testOrientationChange() {
	solo.setActivityOrientation(0);
	solo.sleep(100);
	solo.setActivityOrientation(1);
	solo.sleep(100);
	solo.setActivityOrientation(0);
    }

    public void testSearchButtonState() {
	assertFalse(solo.getImageButton(0).isEnabled());
	solo.enterText(0, "abc");
	assertTrue(solo.getImageButton(0).isEnabled());
	solo.enterText(0, "");
	assertFalse(solo.getImageButton(0).isEnabled());
    }

    public void testSearchButtonState2() {
	assertFalse(solo.getImageButton(0).isEnabled());
	solo.enterText(0, " ");
	assertFalse(solo.getImageButton(0).isEnabled());
	solo.enterText(0, " ");
	assertFalse(solo.getImageButton(0).isEnabled());
	solo.enterText(0, "a");
	assertTrue(solo.getImageButton(0).isEnabled());
    }

    public void testSearchTitlebar() {
	solo.enterText(0, "lorem ipsum");
	solo.clickOnImageButton(0);
	solo.assertCurrentActivity("Button not working",
		com.team4win.tugroom.ShowRooms.class);
	solo.sleep(100);
	assertEquals(
		m_activity
			.getString(com.team4win.tugroom.R.string.titlebar_suche),
		((TextView) (solo
			.getView(com.team4win.tugroom.R.id.titlebar_text)))
			.getText());
    }

    public void testSearchRoomNumber() {
	solo.enterText(0, "Ick1120h");
	solo.clickOnImageButton(0);
	solo.assertCurrentActivity("Button not working",
		com.team4win.tugroom.ShowRooms.class);
	solo.sleep(100);
	solo.clickInList(0);
	solo.assertCurrentActivity("Nothing found",
		com.team4win.tugroom.RoomTabs.class);
	assertTrue(solo.searchText("i13"));
    }

    public void testSearchRoomName() {
	solo.enterText(0, "I2");
	solo.clickOnImageButton(0);
	solo.assertCurrentActivity("Button not working",
		com.team4win.tugroom.ShowRooms.class);
	solo.sleep(100);
	solo.clickInList(0);
	solo.assertCurrentActivity("Nothing found",
		com.team4win.tugroom.RoomTabs.class);
	assertTrue(solo.searchText("HFEG038J"));
    }

    public void testSearchRoomAddress() {
	solo.enterText(0, "Inffeldgasse");
	solo.clickOnImageButton(0);
	solo.assertCurrentActivity("Button not working",
		com.team4win.tugroom.ShowRooms.class);
	solo.sleep(100);
	solo.clickInList(0);
	solo.assertCurrentActivity("Nothing found",
		com.team4win.tugroom.RoomTabs.class);
	assertTrue(solo.searchText("Inffeldgasse"));
    }

    private void _testSearchFilter(String filter_name, String room_nr,
	    String room_name) {
	solo.enterText(0, room_nr);
	solo.clickOnImageButton(0);
	assertTrue(solo.waitForActivity("ShowRooms", 10000));
	solo.sleep(100);
	solo.clickInList(0);
	assertTrue(solo.searchText(room_name));

	solo.sleep(200);
	solo.goBack();
	solo.sleep(200);
	solo.goBack();

	solo.sleep(500);
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_einstellungen));
	solo.sleep(100);
	solo.clickOnText(filter_name);

	solo.goBack();
	solo.sleep(100);
	solo.clickOnImageButton(0);
	assertTrue(solo.waitForActivity("ShowRooms", 10000));
	solo.sleep(100);
	solo.clickInList(0);
	solo.sleep(100);
	assertFalse(solo.searchText(room_name));
    }

    public void testSearchFilterHoersaal() {
	_testSearchFilter(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_hoersaal),
		"Ick1120h", "i13");
    }

    public void testSearchFilterSeminar() {
	_testSearchFilter(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_seminar),
		"Ifeg042", "IFEG042");
    }

    public void testSearchFilterZeichen() {
	_testSearchFilter(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_zeichen),
		"L102010", "STAHLBAU ZS");
    }

    public void testSearchFilterLabore() {
	_testSearchFilter(
		m_activity
			.getString(com.team4win.tugroom.R.string.button_labor),
		"IEEG140", "Cocktail-Party Room");
    }

    public void testAddRemoveFavorite() {
	FavoriteManager fav_man = new FavoriteManager(m_activity);
	fav_man.setFavorite("4010", false);
	assertFalse(fav_man.isFavorite("4010"));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_favoriten));
	assertTrue(solo.waitForActivity("ShowRooms"));
	solo.sleep(1000);
	assertFalse(solo.waitForText("HS A", 1, 10000));
	solo.goBack();

	fav_man.setFavorite("4010", true);
	assertTrue(fav_man.isFavorite("4010"));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_favoriten));
	assertTrue(solo.waitForActivity("ShowRooms", 10000));
	solo.sleep(2000);
	assertTrue(solo.waitForText("HS A", 1, 10000));
    }

    public void testFavoriteManager() {
	FavoriteManager fav_man = new FavoriteManager(m_activity);
	fav_man.setFavorite("1", true);
	fav_man.setFavorite("2", true);
	fav_man.setFavorite("3", true);
	assertTrue(fav_man.isFavorite("1"));
	assertTrue(fav_man.isFavorite("2"));
	assertTrue(fav_man.isFavorite("3"));
	assertFalse(fav_man.isFavorite("4"));

	fav_man.setFavorite("2", false);
	assertFalse(fav_man.isFavorite("2"));
	assertTrue(fav_man.isFavorite("1"));
	assertTrue(fav_man.isFavorite("3"));

	fav_man.setFavorite("1", false);
	assertFalse(fav_man.isFavorite("1"));
	assertFalse(fav_man.isFavorite("2"));
	assertTrue(fav_man.isFavorite("3"));

	fav_man.setFavorite("3", false);
	assertFalse(fav_man.isFavorite("1"));
	assertFalse(fav_man.isFavorite("2"));
	assertFalse(fav_man.isFavorite("3"));

	fav_man.setFavorite("4", false);
	assertFalse(fav_man.isFavorite("1"));
	assertFalse(fav_man.isFavorite("2"));
	assertFalse(fav_man.isFavorite("3"));
	assertFalse(fav_man.isFavorite("4"));
    }

    public void testFavoriteManagerList() {
	FavoriteManager fav_man = new FavoriteManager(m_activity);
	fav_man.setFavorite("1", false);
	List<String> favs_before = new ArrayList<String>(
		fav_man.getFavoriteList());
	assertFalse(favs_before.contains("1"));

	fav_man.setFavorite("1", true);
	List<String> favs = new ArrayList<String>(fav_man.getFavoriteList());
	assertTrue(favs.contains("1"));

	fav_man.setFavorite("1", false);
	List<String> favs_after = new ArrayList<String>(
		fav_man.getFavoriteList());
	assertFalse(favs_after.contains("1"));

	assertEquals(favs_before.size(), favs_after.size());
	assertFalse(favs_before.size() == favs.size());
    }

    public void testFavoriteStar() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.sleep(100);
	int count_before = new FavoriteManager(m_activity).getFavoriteList()
		.size();
	solo.clickOnButton(1);
	solo.sleep(100);
	int count = new FavoriteManager(m_activity).getFavoriteList().size();
	solo.clickOnButton(1);
	solo.sleep(100);
	int count_after = new FavoriteManager(m_activity).getFavoriteList()
		.size();
	assertTrue(count_before == count_after);
	assertFalse(count == count_before);
    }

    public void testCancelLoading() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.sendKey(KeyEvent.KEYCODE_BACK);
	solo.sleep(1000);
	assertTrue(solo.waitForActivity("TUGRoomActivity", 10000));
	solo.assertCurrentActivity("Did not cancel loading",
		com.team4win.tugroom.TUGRoomActivity.class);
    }

    public void testOrientationChangeWhileLoading() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.setActivityOrientation(0);
	solo.sleep(50);
	solo.setActivityOrientation(1);
	solo.sleep(50);
	solo.setActivityOrientation(0);
    }

    public void testOrientationChangeInRoomList() {
	solo.setActivityOrientation(1);
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.waitForDialogToClose(10000);
	assertTrue(solo.searchText("HS i2"));
	solo.setActivityOrientation(0);
	solo.waitForDialogToClose(10000);
	assertTrue(solo.searchText("HS i2"));
    }

    public void testOrientationChangeWithFilter() {
	solo.setActivityOrientation(1);
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.waitForDialogToClose(10000);
	assertTrue(solo.searchText("HS i2"));
	solo.enterText(0, "i7");
	assertFalse(solo.searchText("HS i2"));
	solo.setActivityOrientation(0);
	solo.waitForDialogToClose(10000);
	assertFalse(solo.searchText("HS i2"));
    }

    public void testTabs() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.clickInList(0);
	solo.sleep(1000);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_map));
	solo.assertCurrentActivity("expected map view",
		com.team4win.tugroom.LageView.class);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_detail));
	solo.assertCurrentActivity("expected detail view",
		com.team4win.tugroom.DetailView.class);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_usage));
	solo.assertCurrentActivity("expected usage view",
		com.team4win.tugroom.UsageView.class);
    }

    public void testMapSwitchButton() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.clickInList(0);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_map));
	assertTrue(solo.searchButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_map)));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_map));
	assertTrue(solo.searchButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_room)));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_room));
	assertTrue(solo.searchButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_map)));
    }

    public void testDragOfMap() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.clickInList(0);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_map));
	solo.sleep(100);
	ZoomImageView zoom_view = (ZoomImageView) solo
		.getView(com.team4win.tugroom.R.id.img_lage);
	assertFalse(zoom_view == null);
	float x_before = zoom_view.getImageX();
	float y_before = zoom_view.getImageY();
	TouchUtils.dragViewBy(this, zoom_view, Gravity.CENTER_HORIZONTAL
		+ Gravity.CENTER_VERTICAL, 20, 20);
	assertFalse(x_before == zoom_view.getImageX());
	assertFalse(y_before == zoom_view.getImageY());
    }

    public void testMapPositionReset() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.clickOnText("HS A");
	solo.sleep(1000);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_map));
	solo.sleep(100);
	ZoomImageView zoom_view = (ZoomImageView) solo
		.getView(com.team4win.tugroom.R.id.img_lage);
	assertFalse(zoom_view == null);
	float x_before = zoom_view.getImageX();
	float y_before = zoom_view.getImageY();
	TouchUtils.dragViewBy(this, zoom_view, Gravity.CENTER_HORIZONTAL
		+ Gravity.CENTER_VERTICAL, 20, 20);
	assertFalse(x_before == zoom_view.getImageX());
	assertFalse(y_before == zoom_view.getImageY());

	assertTrue(solo.searchButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_map)));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_map));
	assertTrue(solo.searchButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_room)));
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.map_room));

	assertTrue(x_before == zoom_view.getImageX());
	assertTrue(y_before == zoom_view.getImageY());
    }

    public void testZoomImageViewBitmapNull() {
	ZoomImageView zoom_view = new ZoomImageView(m_activity, null);
	assertFalse(zoom_view == null);
	zoom_view.setImage(null);
    }

    public void testCancelLoadingUsage() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	assertTrue(solo.waitForActivity("ShowRooms", 10000));
	solo.sleep(1000);
	solo.clickInList(0);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.tab_usage));
	solo.sleep(100);
	solo.sendKey(KeyEvent.KEYCODE_BACK);
	solo.sleep(500);
	solo.assertCurrentActivity("did not cancel loading",
		com.team4win.tugroom.UsageView.class);
	solo.sendKey(KeyEvent.KEYCODE_BACK);
	solo.sleep(1000);
	solo.assertCurrentActivity("Did not cancel loading",
		com.team4win.tugroom.ShowRooms.class);
    }

    public void testAboutDialog() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_einstellungen));
	solo.sleep(100);
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.about));
	solo.sleep(500);
	assertTrue(solo.searchText("Copyright"));
    }

    public void testDetailViewLocationToFilename() {
	com.team4win.tugroom.DetailView d = new com.team4win.tugroom.DetailView();
	assertEquals("fotos/Kopernikusgasse24_foto.jpg",
		d.locationToFilename("Kopernikusgasse 24, 1. Obergeschoß"));
	assertEquals("fotos/aeoeuessstrasse173D_foto.jpg",
		d.locationToFilename("äöüßstraße 173/D, 1. Obergeschoß, Links"));
	assertEquals("fotos/Inffeldgasse5_foto.jpg",
		d.locationToFilename(" Inffeldgasse 5 , Keller"));
	assertEquals(
		"fotos/Technikerstrasse9_foto.jpg",
		d.locationToFilename("Technikerstraße        9, 2. Obergeschoß"));
	assertEquals(
		"fotos/Muenzgrabenstrasse17_foto.jpg",
		d.locationToFilename("   Münzgrabenstraße   17   ,   7.   Obergeschoß    "));
    }

    public void testLageViewGetAddress() {
	com.team4win.tugroom.LageView l = new com.team4win.tugroom.LageView();
	assertEquals("Inffeldgasse 16b, 8010 Graz",
		l.getAddress("Inffeldgasse 16b, 1. Obergeschoß", "8010 Graz"));
	assertEquals("Münzgrabenstraße 5, 8010 Graz", l.getAddress(
		" Münzgrabenstraße 5 , Keller , Links", " 8010 Graz "));
    }

    public void testLageViewLocationToFilename() {
	com.team4win.tugroom.LageView l = new com.team4win.tugroom.LageView();
	assertEquals("lageplaene/Kopernikusgasse24.gif",
		l.locationToFilename("Kopernikusgasse 24, 1. Obergeschoß"));
	assertEquals("lageplaene/aeoeuessstrasse173D.gif",
		l.locationToFilename("äöüßstraße 173/D, 1. Obergeschoß, Links"));
	assertEquals("lageplaene/Inffeldgasse5.gif",
		l.locationToFilename(" Inffeldgasse 5 , Keller"));
	assertEquals(
		"lageplaene/Technikerstrasse9.gif",
		l.locationToFilename("Technikerstraße        9, 2. Obergeschoß"));
	assertEquals(
		"lageplaene/Muenzgrabenstrasse17.gif",
		l.locationToFilename("   Münzgrabenstraße   17   ,   7.   Obergeschoß    "));
    }

    public void testDetailViewText() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_hoersaal));
	solo.clickOnText("HS A");
	solo.sleep(100);
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.detail_equipment)));
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.detail_location)));
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.detail_roomcode)));
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.detail_seats)));
	assertTrue(solo.searchText("Kopernikusgasse"));
	assertTrue(solo.searchText("NT01004"));
	assertTrue(solo.searchText("146"));
	assertTrue(solo.searchText("Pult"));
    }

    public void testXMLParser() {
	String room_string = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><OneBoxResults><resultCode>success</resultCode><provider>http://search-proxy.tugraz.at/search/</provider><searchTerm></searchTerm><totalResults>7842</totalResults><MODULE_RESULT><Title>1</Title><Field name=\"roomID_text\">44</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=44</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Büro</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03014</Field><Field name=\"seats\">123</Field><Field name=\"additionalInformation\">WERKSTATT</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">1/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT></OneBoxResults>";
	XMLParser parser = new XMLParser();
	List<Room> rooms = parser.getRooms(new InputSource(new StringReader(
		room_string)), null);
	assertTrue(rooms.size() == 1);
	Room room = rooms.get(0);
	assertTrue(room.getProperty(Property.ROOM_NUMBER_INTERNAL).equals("44"));
	assertTrue(room.getProperty(Property.STREET).equals(
		"Kopernikusgasse 24, 3.Obergeschoß"));
	assertTrue(room.getProperty(Property.USAGE).equals("Büro"));
	assertTrue(room.getProperty(Property.ROOM_ID).equals("NT03014"));
	assertTrue(room.getProperty(Property.NAME).equals("WERKSTATT"));
	assertTrue(room.getProperty(Property.SEATS).equals("123"));
    }

    public void testXMLParserNoName() {
	String room_string = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><OneBoxResults><resultCode>success</resultCode><provider>http://search-proxy.tugraz.at/search/</provider><searchTerm></searchTerm><totalResults>7842</totalResults><MODULE_RESULT><Title>1</Title><Field name=\"roomID_text\">44</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=44</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Büro</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03014</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">1/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT></OneBoxResults>";
	XMLParser parser = new XMLParser();
	List<Room> rooms = parser.getRooms(new InputSource(new StringReader(
		room_string)), null);
	assertTrue(rooms.size() == 1);
	Room room = rooms.get(0);
	assertTrue(room.getProperty(Property.NAME).equals("Büro"));
    }

    public void testXMLParserTwoRooms() {
	String room_string = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><OneBoxResults><resultCode>success</resultCode><provider>http://search-proxy.tugraz.at/search/</provider><searchTerm></searchTerm><totalResults>7842</totalResults><MODULE_RESULT><Title>1</Title><Field name=\"roomID_text\">44</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=44</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Büro</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03014</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">1/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT><MODULE_RESULT><Title>2</Title><Field name=\"roomID_text\">45</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=45</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Büro</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03015</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">2/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT></OneBoxResults>";
	XMLParser parser = new XMLParser();
	List<Room> rooms = parser.getRooms(new InputSource(new StringReader(
		room_string)), null);
	assertTrue(rooms.size() == 2);
    }

    public void testXMLParserCategorieFilter() {
	String room_string = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><OneBoxResults><resultCode>success</resultCode><provider>http://search-proxy.tugraz.at/search/</provider><searchTerm></searchTerm><totalResults>7842</totalResults><MODULE_RESULT><Title>1</Title><Field name=\"roomID_text\">44</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=44</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Hörsaal</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03014</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">1/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT><MODULE_RESULT><Title>2</Title><Field name=\"roomID_text\">45</Field><Field name=\"address\">Kopernikusgasse 24, 3.Obergeschoß</Field><Field name=\"address_attrAltUrl\">https://online.tugraz.at/tug_online/ris.einzelraum?raumkey=45</Field><Field name=\"purposeID\">12</Field><Field name=\"purpose\">Büro (Informatik)</Field><Field name=\"area\">18.52</Field><Field name=\"roomCode\">NT03015</Field><Field name=\"WEB SERVICE\">RL</Field><Field name=\"wb_result_cnt\">2/7842</Field><Field name=\"count_all_results\">7842</Field></MODULE_RESULT></OneBoxResults>";
	XMLParser parser = new XMLParser();
	List<Room> rooms = parser.getRooms(new InputSource(new StringReader(
		room_string)), Arrays.asList((CharSequence) ("Hörsaal")));
	assertTrue(rooms.size() == 1);
	rooms = parser.getRooms(new InputSource(new StringReader(room_string)),
		Arrays.asList((CharSequence) ("Büro")));
	assertTrue(rooms.size() == 1);
	rooms = parser.getRooms(new InputSource(new StringReader(room_string)),
		Arrays.asList((CharSequence) ("Foobar")));
	assertTrue(rooms.size() == 0);
	rooms = parser
		.getRooms(new InputSource(new StringReader(room_string)), null);
	assertTrue(rooms.size() == 2);
    }

    public void testXMLParserNULL() {
	assertTrue(new XMLParser().getRooms(null, null) == null);
    }

    public void testUpdateTimeSpan() {
	long save_date = UpdateHelper.getLastUpdate(m_activity);
	long new_time = (new Date()).getTime() - 86400 * 1000 * 3;
	UpdateHelper.setLastUpdate(m_activity, new_time);
	assertTrue(UpdateHelper.getLastUpdate(m_activity) == new_time);
	assertTrue(UpdateHelper.getLastUpdateInDays(m_activity) == 3);
	UpdateHelper.setLastUpdate(m_activity, save_date);
	assertTrue(UpdateHelper.getLastUpdate(m_activity) == save_date);
    }

    public void testUpdateString() {
	long save_date = UpdateHelper.getLastUpdate(m_activity);
	UpdateHelper.setLastUpdate(m_activity, 2000000000000l);
	android.util.Log.d("test",
		"lupd: " + UpdateHelper.getLastUpdateString(m_activity));
	assertTrue(UpdateHelper.getLastUpdateString(m_activity).equals(
		"18.05.2033"));
	UpdateHelper.setLastUpdate(m_activity, save_date);
    }

    public void testUpdateCancelPreferenceButton() {
	solo.clickOnButton(m_activity
		.getString(com.team4win.tugroom.R.string.button_einstellungen));
	solo.sleep(100);

	if (solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_cancel))) {
	    solo.clickOnText(m_activity
		    .getString(com.team4win.tugroom.R.string.update_cancel));
	    solo.sleep(1000);
	}
	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.update_data));
	solo.sleep(100);
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_cancel)));

	solo.clickOnText(m_activity
		.getString(com.team4win.tugroom.R.string.update_cancel));
	solo.sleep(100);
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_data)));
    }

    public void testAvailableDiskSpace() {
	String home_dir = new ContextWrapper(m_activity).getFilesDir()
		.getAbsolutePath();
	UpdateTask upd = new UpdateTask(m_activity, null);
	long avail = upd.getAvailableDiskSpace();
	File file = null;

	try {
	    file = new File(home_dir + "/test.txt");
	    file.createNewFile();
	    assertTrue(file.exists());

	    OutputStream output = new FileOutputStream(file);
	    byte[] buffer = new byte[8192];
	    output.write(buffer);
	    output.close();
	} catch (Exception exception) {
	    assertTrue(false);
	}

	long avail1 = upd.getAvailableDiskSpace();
	assertTrue(avail1 != avail);
	file.delete();
	assertTrue(avail1 != upd.getAvailableDiskSpace());
    }

    private void _restartApp() {
	AlarmManager mgr = (AlarmManager) m_activity
		.getSystemService(Context.ALARM_SERVICE);
	mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
		restart_intent);
	m_activity.finish();
	solo.sleep(2000);
    }

    public void testShowUpdateDialog() {
	UpdateHelper.setNeverShowUpdateDialog(m_activity, false);

	UpdateHelper.setLastUpdate(m_activity, (new Date()).getTime());
	_restartApp();

	assertFalse(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
    }

    public void testShowUpdateDialog2() {
	UpdateHelper.setNeverShowUpdateDialog(m_activity, false);

	UpdateHelper.setLastUpdate(m_activity, 0);
	_restartApp();

	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
    }

    public void testShowUpdateDialogOrientationChange() {
	UpdateHelper.setNeverShowUpdateDialog(m_activity, false);

	UpdateHelper.setLastUpdate(m_activity, 0);
	_restartApp();

	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertFalse(UpdateHelper.wasUpdateDialogShown());
	solo.setActivityOrientation(0);
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertFalse(UpdateHelper.wasUpdateDialogShown());
	solo.setActivityOrientation(1);
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertFalse(UpdateHelper.wasUpdateDialogShown());
    }

    public void testShowUpdateDialogAlreadyShownOrientationChange() {
	UpdateHelper.setNeverShowUpdateDialog(m_activity, false);

	UpdateHelper.setLastUpdate(m_activity, 0);
	_restartApp();

	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertFalse(UpdateHelper.wasUpdateDialogShown());
	solo.clickOnText(m_activity.getString(com.team4win.tugroom.R.string.no));
	assertFalse(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertTrue(UpdateHelper.wasUpdateDialogShown());
	solo.setActivityOrientation(0);
	assertFalse(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertTrue(UpdateHelper.wasUpdateDialogShown());
	solo.setActivityOrientation(1);
	assertFalse(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertTrue(UpdateHelper.wasUpdateDialogShown());
    }

    public void testShowUpdateDialogNeverShowAgain() {
	UpdateHelper.setNeverShowUpdateDialog(m_activity, false);

	UpdateHelper.setLastUpdate(m_activity, 0);
	_restartApp();

	assertFalse(UpdateHelper.getNeverShowUpdateDialog(m_activity));
	assertTrue(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	solo.clickOnCheckBox(0);
	solo.clickOnText(m_activity.getString(com.team4win.tugroom.R.string.no));
	solo.sleep(100);
	assertFalse(solo.searchText(m_activity
		.getString(com.team4win.tugroom.R.string.update_message_1)));
	assertTrue(UpdateHelper.getNeverShowUpdateDialog(m_activity));
    }

    private List<Room> _gen3Rooms(String name1, String name2, String name3) {
	List<Room> rooms = new ArrayList<Room>();
	Room room = new Room();
	room.setProperty(Property.NAME, name1);
	rooms.add(room);
	room = new Room();
	room.setProperty(Property.NAME, name2);
	rooms.add(room);
	room = new Room();
	room.setProperty(Property.NAME, name3);
	rooms.add(room);
	return rooms;
    }

    public void testNaturalRoomSort() {
	List<Room> rooms = _gen3Rooms("HS i1", "HS i11", "HS i2");
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("HS i1"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("HS i11"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS i2"));
	Collections.sort(rooms, new NaturalRoomComparator());
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("HS i1"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("HS i2"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS i11"));
    }

    public void testNaturalRoomSortCaseInsensitive() {
	List<Room> rooms = _gen3Rooms("hS b", "HS c", "HS A");
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("hS b"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("HS c"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS A"));
	Collections.sort(rooms, new NaturalRoomComparator());
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("HS A"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("hS b"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS c"));
    }

    public void testNaturalRoomSortLeadingZero() {
	List<Room> rooms = _gen3Rooms("HS B2", "HS B01", "HS B0003");
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("HS B2"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("HS B01"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS B0003"));
	Collections.sort(rooms, new NaturalRoomComparator());
	assertTrue(rooms.get(0).getProperty(Property.NAME).equals("HS B01"));
	assertTrue(rooms.get(1).getProperty(Property.NAME).equals("HS B2"));
	assertTrue(rooms.get(2).getProperty(Property.NAME).equals("HS B0003"));
    }

    public void testGeoUrlLocation() {
	LocationIntentReceiver intent_receiver = new LocationIntentReceiver();
	assertEquals("HS A", intent_receiver.getGeoUrlLocation(Uri.parse("geo:0,0?q=HS A")));
	assertEquals("HS A (NT01004)",
		intent_receiver.getGeoUrlLocation(Uri.parse("geo:0,0?q=HS A (NT01004)")));
	assertEquals("HS i13",
		intent_receiver.getGeoUrlLocation(Uri.parse("geo:0,0?zoom=17&q=HS i13")));
	assertEquals("", intent_receiver.getGeoUrlLocation(Uri.parse("geo:0,0")));
	assertEquals("", intent_receiver.getGeoUrlLocation(Uri.parse("geo:0,0?zoom=3")));
    }

    public void testRoomNumber() {
	LocationIntentReceiver intent_receiver = new LocationIntentReceiver();
	assertEquals("", intent_receiver.getRoomNumber(""));
	assertEquals("", intent_receiver.getRoomNumber("HS A"));
	assertEquals("", intent_receiver.getRoomNumber("Foo (bar)"));
	assertEquals("BAr", intent_receiver.getRoomNumber("Foo ((BAr))"));
	assertEquals("BAR", intent_receiver.getRoomNumber("Foo (BAR)"));
	assertEquals("", intent_receiver.getRoomNumber("Foo (Bar)"));
	assertEquals("NT01004", intent_receiver.getRoomNumber("HS A (NT01004)"));
	assertEquals("NT01004", intent_receiver.getRoomNumber("HS A ( NT01004 )"));
	assertEquals("NT01004", intent_receiver.getRoomNumber("(NT01004)"));
    }

}
