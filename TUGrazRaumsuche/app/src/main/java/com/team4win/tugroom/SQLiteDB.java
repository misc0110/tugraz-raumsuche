package com.team4win.tugroom;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDB extends SQLiteOpenHelper {
    /*
     * Schema: CREATE TABLE Rooms ( _id INTEGER PRIMARY KEY, 'RaumnummerIntern'
     * TEXT, 'ID' TEXT, 'Gebaeude' TEXT, 'Stockwerk' TEXT, 'Raumnummer' TEXT,
     * 'Strasse' TEXT, 'Ort' TEXT, 'Verwendung' TEXT, 'Raumtyp' TEXT,
     * 'Nutzungstyp' TEXT, 'Zwischengeschoss' TEXT, 'Flaeche' TEXT, 'Hoehe'
     * TEXT, 'Boden' TEXT, 'Reinigung' TEXT, 'Zyklus' TEXT, 'Art' TEXT,
     * 'Verwaltung' TEXT, 'Zusatzbezeichnung' TEXT, 'Nebenstelle' TEXT, 'Name'
     * TEXT, 'Beschreibung' TEXT, 'Image' TEXT, 'MarkerX' TEXT, 'MarkerY' TEXT,
     * 'Sitzplaetze' TEXT, 'ArchitektenRaumnr' TEXT, 'Ausrichtung' TEXT,
     * 'Benutzungshinweis' TEXT, 'Stehplaetze' TEXT, 'Equipment' TEXT, 'URL'
     * TEXT, 'Rollstuhlplaetze' TEXT)
     */

    private SQLiteDatabase db;
    private String db_name;
    private String db_path;
    private Context ctx;
    private String columns[] = { Property.ROOM_ID,
	    Property.ROOM_NUMBER_INTERNAL, Property.BUILDING, Property.STORY,
	    Property.ROOM_NUMBER, Property.STREET, Property.CITY,
	    Property.USAGE, Property.ROOM_TYPE, Property.USAGE_TYPE,
	    Property.INTERMEDIATE_STORY, Property.AREA, Property.HIGH,
	    Property.FLOOR, Property.CLEANING, Property.CLEANING_CYCLUS,
	    Property.CLEANING_TYPE, Property.ADMINISTRATION, Property.SUBJECT,
	    Property.PHONE_SUBSTATION, Property.NAME, Property.DESCRIPTION,
	    Property.IMAGE, Property.MARKER_X, Property.MARKER_Y,
	    Property.SEATS, Property.ARCHITECTS_ROOM, Property.ADJUSTMENT,
	    Property.USER_ADVICE, Property.STANCE, Property.EQUIPMENT,
	    Property.URL, Property.WHEEL_CHAIR };

    public SQLiteDB(Context context, String name) {
	super(context, name, null, 1);
	db_name = name;
	ctx = context;
	db_path = (new ContextWrapper(context)).getFilesDir().getAbsolutePath()
		.replace("/files/", "/databases/");
    }

    private void copyDataBase() throws IOException {
	InputStream asset_db = ctx.getAssets().open(db_name);
	String db_filename = db_path + db_name;
	OutputStream sys_db = new FileOutputStream(db_filename);

	// copy database
	byte[] buffer = new byte[1024];
	int length;
	while ((length = asset_db.read(buffer)) > 0) {
	    sys_db.write(buffer, 0, length);
	}

	sys_db.flush();
	sys_db.close();
	asset_db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
    }

    @Override
    public synchronized void close() {
	if (db != null) {
	    db.close();
	    db = null;
	}
	super.close();
    }

    public boolean open() {
	try {
	    db = getWritableDatabase();
	} catch (Exception e) {
	    return false;
	}
	if (db == null) {
	    return false;
	}

	try {
	    String path = db_path + db_name;
	    db = SQLiteDatabase.openDatabase(path, null,
		    SQLiteDatabase.OPEN_READWRITE);

	} catch (Exception execption) {
	    if (!tableExists()) {
		try {
		    copyDataBase();
		    return open();
		} catch (IOException except) {
		    return false;
		}
	    }
	}
	return true;
    }

    public void insertRoom(List<Room> room_list) {
	for (int i = 0; i < room_list.size(); i++) {
	    insertRoom(room_list.get(i));
	}
    }

    public void insertRoom(Room room) {
	ContentValues values = new ContentValues();
	for (int i = 0; i < columns.length; i++) {
	    values.put(columns[i], room.getProperty(columns[i]));
	}
	db.insert(db_name, null, values);
    }

    public List<Room> getRooms(String where_clause, String exclude_clause) {
	Cursor cursor = null;
	List<Room> list = new ArrayList<Room>();

	String selection_args = (where_clause == null ? "" : where_clause);
	if (where_clause != null && exclude_clause != null) {
	    selection_args += " AND ";
	}
	selection_args += (exclude_clause != null ? exclude_clause : "");

	cursor = db.query(db_name, columns, selection_args, null, null, null,
		Property.NAME);
	cursor.moveToFirst();

	for (int i = 0; i < cursor.getCount(); i++) {
	    Room room = new Room();
	    for (int n = 0; n < columns.length; n++) {
		room.setProperty(columns[n], cursor.getString(n));
	    }

	    list.add(room);
	    cursor.moveToNext();
	}
	cursor.close();
	return list;
    }

    public void updateRoom(List<Room> rooms) {

	List<Room> actual_rooms = getRooms(null, null);
	HashMap<String, String> map = new HashMap<String, String>();
	db.beginTransaction();

	for (int i = 0; i < actual_rooms.size(); i++) {
	    Room room = actual_rooms.get(i);
	    map.put(room.getProperty(Property.ROOM_NUMBER_INTERNAL), "");
	}

	for (int room_count = 0; room_count < rooms.size(); room_count++) {
	    Room room = rooms.get(room_count);
	    ContentValues values = new ContentValues();
	    for (int property_count = 0; property_count < columns.length; property_count++) {
		if (room.getProperty(columns[property_count]) != null) {
		    values.put(columns[property_count],
			    room.getProperty(columns[property_count]));
		}
	    }

	    String[] args = { room.getProperty(Property.ROOM_NUMBER_INTERNAL) };

	    int amount = db.update(db_name, values,
		    Property.ROOM_NUMBER_INTERNAL + " = ?", args);

	    if (amount == 0) {
		insertRoom(room);
	    }

	    map.remove(room.getProperty(Property.ROOM_NUMBER_INTERNAL));

	}

	Iterator<String> iterator = map.keySet().iterator();
	while (iterator.hasNext()) {
	    String[] args = { (String) iterator.next() };
	    int res = db.delete(db_name,
		    Property.ROOM_NUMBER_INTERNAL + " = ?", args);
	    Log.e("SQLiteDB", res + " removed. Room with id = " + args[0]);
	}

	// commit the changes of the data
	db.setTransactionSuccessful();
	db.endTransaction();
    }

    public List<Room> searchRooms(String search, List<String> excludes) {

	List<Room> list = new ArrayList<Room>();

	String exclude_string = "";
	if (excludes != null) {
	    for (int i = 0; i < excludes.size(); i++) {
		exclude_string += " AND " + Property.USAGE + " NOT LIKE '%"
			+ excludes.get(i) + "%'";
	    }
	}

	Cursor cursor = db.query(
		db_name,
		columns,
		"(lower(" + Property.ROOM_ID + ") like '%"
			+ search.toLowerCase() + "%' or " + "lower("
			+ Property.NAME + ") like '%" + search.toLowerCase()
			+ "%' or " + "lower(" + Property.STREET + ") like '%"
			+ search.toLowerCase() + "%' or " + "lower("
			+ Property.ARCHITECTS_ROOM + ") like '%"
			+ search.toLowerCase() + "%')" + exclude_string, null,
		null, null, null);
	cursor.moveToFirst();

	for (int i = 0; i < cursor.getCount(); i++) {
	    Room room = new Room();
	    for (int property_nr = 0; property_nr < columns.length; property_nr++) {
		room.setProperty(columns[property_nr],
			cursor.getString(property_nr));
	    }

	    list.add(room);
	    cursor.moveToNext();
	}
	cursor.close();
	return list;
	// Property.NAME Property.STREET Property.ARCHITECTS_ROOM
    }

    public boolean tableExists() {
	try {
	    db.query(db_name, null, null, null, null, null, null);
	} catch (Exception exception) {
	    return false;
	}
	return true;
    }

    public void resetDatabase() {
	db.execSQL("DROP TABLE IF EXISTS " + db_name);
	try {
	    copyDataBase();
	} catch (Exception exception) {
	}
    }

    public void deleteAllElementsFromTable() {
	db.delete(db_name, null, null);
    }

    public SQLiteDatabase getDb() {
	return db;
    }

}
