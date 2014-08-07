package com.team4win.tugroom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ShowRooms extends CustomWindow {
    private List<Room> room_list;
    private RoomAdapter room_list_adapter;
    private ProgressDialog room_load_progress;
    private Context context;
    private EditText filter_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        context = this;

        room_list = new ArrayList<Room>();
        room_list_adapter = new RoomAdapter(this, R.layout.row, room_list);

        ListView room_list = (ListView) findViewById(R.id.rooms);
        room_list.setAdapter(room_list_adapter);
        room_list.setTextFilterEnabled(true);

        filter_text = (EditText) findViewById(R.id.search_box);
        filter_text.addTextChangedListener(filterTextWatcher);

        final Bundle extras = getIntent().getExtras();
        setTitlebarText(extras.getString("titlebar"));

        if (extras.getString("Kategorie") != null) {
            loadRooms(extras.getString("Kategorie"));
        } else if (extras.getString("Suche") != null) {
            searchRooms(extras.getString("Suche"));
        } else if (extras.getString("Favoriten") != null) {
            loadFavorites();
        } else if (extras.getString("RoomID") != null) {
            loadRoomId(extras.getString("RoomID"));

        }
    }

    private Room getRoomFromView(View view) {
        if (view == null)
            return null;
        LinearLayout view_parent = (LinearLayout) view.getParent();
        if (view_parent == null)
            return null;
        TextView room_nr = (TextView) view_parent.findViewById(R.id.raumnummer);
        if (room_nr == null)
            return null;

        String r_number = room_nr.getText().toString().trim();
        for (int i = 0; i < room_list.size(); i++) {
            if (room_list.get(i).getProperty(Property.ROOM_NUMBER_INTERNAL)
                    .equals(r_number)) {
                return room_list.get(i);
            }
        }
        return null;
    }

    public void roomClick(View view) {
        Room room = getRoomFromView(view);
        if (room == null)
            return;
        // put all information about room into bundle
        final Intent intent = new Intent(context, RoomTabs.class);
        Map<String, String> properties = room.getPropertyMap();
        Iterator<String> iter = properties.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = properties.get(key);
            intent.putExtra(key, value);
        }
        startActivity(intent);
    }

    public void favoriteClick(View view) {
        Room room = getRoomFromView(view);
        if (room == null)
            return;
        String room_nr = room.getProperty(Property.ROOM_NUMBER_INTERNAL);
        FavoriteManager fav_man = new FavoriteManager(getApplicationContext());
        if (fav_man.isFavorite(room_nr)) {
            fav_man.setFavorite(room_nr, false);
            ((Button) view).setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.favorites_small_disabled, 0, 0);
        } else {
            fav_man.setFavorite(room_nr, true);
            ((Button) view).setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.favorites_small, 0, 0);
        }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable text) {
        }

        public void beforeTextChanged(CharSequence text, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence text, int start, int before,
                                  int count) {
            room_list_adapter.getFilter().filter(text);
        }

    };

    /* ============ loading rooms with eye candy ;) ============= */

    Runnable view_rooms;
    Thread load_thread;

    void loadRooms(final String categorie) {
        view_rooms = new Runnable() {
            @Override
            public void run() {
                room_list = new DBLoader(getApplicationContext())
                        .getRooms(categorie);
                runOnUiThread(return_res);
            }
        };
        startLoadRooms();
    }

    void searchRooms(final String search) {
        view_rooms = new Runnable() {
            @Override
            public void run() {
                // XMLLoader loader = new XMLLoader();
                DBLoader loader = new DBLoader(context);
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                if (!prefs.getBoolean("search_hoersaal", true))
                    loader.addSearchExcludeCategory(getString(R.string.room_cat_hoersaal));
                if (!prefs.getBoolean("search_seminar", true))
                    loader.addSearchExcludeCategory(getString(R.string.room_cat_seminarraum));
                if (!prefs.getBoolean("search_zeichen", true))
                    loader.addSearchExcludeCategory(getString(R.string.room_cat_zeichensaal));
                if (!prefs.getBoolean("search_labore", true))
                    loader.addSearchExcludeCategory(getString(R.string.room_cat_labor));
                // room_list = loader.searchRooms(openFile(file), search);
                room_list = loader.searchRooms(search);
                runOnUiThread(return_res);
            }
        };
        startLoadRooms();
    }

    void loadFavorites() {
        view_rooms = new Runnable() {
            @Override
            public void run() {
                DBLoader loader = new DBLoader(context);

                loader.setFavoriteList(new FavoriteManager(
                        getApplicationContext()).getFavoriteList());
                room_list = loader.getFavorites();
                runOnUiThread(return_res);
            }
        };
        startLoadRooms();
    }

    void loadRoomId(final String id) {
        view_rooms = new Runnable() {
            @Override
            public void run() {
                DBLoader loader = new DBLoader(context);
                room_list = loader.getRoomById(id);
                runOnUiThread(returnRoom);
            }
        };
        startLoadRooms();
    }

    void startLoadRooms() {
        load_thread = new Thread(null, view_rooms, "LoadRooms");
        load_thread.start();
        room_load_progress = ProgressDialog.show(this,
                getString(R.string.please_wait),
                getString(R.string.loading_data), true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        load_thread.interrupt();
                        finish();
                    }
                }
        );
    }

    private Runnable return_res = new Runnable() {
        @Override
        public void run() {
            room_list_adapter = new RoomAdapter(context, R.layout.row,
                    room_list);
            try {
                room_load_progress.dismiss();
            } catch (Exception e) {
            }

            room_list_adapter.notifyDataSetChanged();
            ListView room_list = (ListView) findViewById(R.id.rooms);
            room_list.setAdapter(room_list_adapter);

            room_list_adapter.getFilter().filter(
                    filter_text.getText().toString());
        }
    };

    private Runnable returnRoom = new Runnable() {
        @Override
        public void run() {
            try {
                room_load_progress.dismiss();
            } catch (Exception e) {
            }

            if(room_list.size() > 0) {
                Room room = room_list.get(0);
                final Intent intent = new Intent(context, RoomTabs.class);
                Map<String, String> properties = room.getPropertyMap();
                Iterator<String> iter = properties.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    String value = properties.get(key);
                    intent.putExtra(key, value);
                }
                startActivityForResult(intent, 0);
            }
            finish();
        }
    };

}
