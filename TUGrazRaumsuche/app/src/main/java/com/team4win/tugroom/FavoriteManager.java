package com.team4win.tugroom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class FavoriteManager {
    private SharedPreferences prefs;
    private List<String> favorites;

    public FavoriteManager(Context context) {
	prefs = context.getSharedPreferences("favorites", 0);
	String[] fav = prefs.getString("favorite", "").split("::");
	favorites = new ArrayList<String>();

	for (int i = 0; i < fav.length; i++) {
	    if (fav[i].length() > 1)
		favorites.add(fav[i]);
	}
    }

    public boolean isFavorite(String roomnumber) {
	return favorites.contains(roomnumber);
    }

    public void setFavorite(String roomnumber, boolean is_fav) {
	if (!is_fav)
	    favorites.remove(roomnumber);
	else
	    favorites.add(roomnumber);

	String new_fav = "";
	for (int i = 0; i < favorites.size(); i++) {
	    new_fav += favorites.get(i) + "::";
	}

	Editor edit = prefs.edit();
	edit.putString("favorite", new_fav);
	edit.commit();
    }

    public List<String> getFavoriteList() {
	return favorites;
    }

}
