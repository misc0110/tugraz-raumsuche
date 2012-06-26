package com.team4win.tugroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;

public class DBLoader {
    private List<String> search_exclude = null;
    private List<String> favorite_list = null;
    private SQLiteDB db;
    private Context ctx;
    private String db_name = "Rooms";

    public DBLoader(Context ctx) {
	this.ctx = ctx;
    }

    public List<Room> getRooms(String categorie) {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	List<Room> result = db.getRooms(Property.USAGE + " LIKE '%" + categorie
		+ "%'", null);
	Collections.sort(result, new NaturalRoomComparator());
	db.close();
	return result;
    }

    public List<Room> getRoomById(String id) {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	List<Room> result = db.getRooms(Property.ROOM_ID + " = '" + id + "'",
		null);
	db.close();
	return result;
    }

    List<Room> getFavorites() {
	String fav_list = "";
	for (int i = 0; i < favorite_list.size(); i++) {
	    fav_list += "\"" + favorite_list.get(i) + "\"";
	    if (i != favorite_list.size() - 1)
		fav_list += ",";
	}
	db = new SQLiteDB(ctx, db_name);
	db.open();
	List<Room> result = db.getRooms(Property.ROOM_NUMBER_INTERNAL + " IN ("
		+ fav_list + ")", null);
	Collections.sort(result, new NaturalRoomComparator());
	db.close();
	return result;
    }

    public void insertRooms(List<Room> rooms) {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	db.insertRoom(rooms);
	db.close();
    }

    public void updateRooms(List<Room> rooms) {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	db.updateRoom(rooms);
	db.close();
    }

    public List<Room> searchRooms(String search) {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	List<Room> list = db.searchRooms(search, search_exclude);
	db.close();
	return list;
    }

    public void addSearchExcludeCategory(String cat) {
	if (search_exclude == null)
	    search_exclude = new ArrayList<String>();
	search_exclude.add(cat);
    }

    public void setFavoriteList(List<String> list) {
	favorite_list = list;
    }

    public void resetSearchExcludeCategory() {
	search_exclude = null;
    }

    public void resetDatabase() {
	db = new SQLiteDB(ctx, db_name);
	db.open();
	db.resetDatabase();
	db.close();
    }
}
