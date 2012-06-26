package com.team4win.tugroom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class RoomAdapter extends ArrayAdapter<Room> implements Filterable {

    private List<Room> items;
    private List<Room> items_orig = null;
    private int layout_id;
    private Context context;
    private Filter filter = null;
    private final Object lock = new Object();
    private FavoriteManager fav_man;

    public RoomAdapter(Context context, int textViewResourceId, List<Room> hs) {
	super(context, textViewResourceId, hs);
	this.items = hs;
	this.context = context;
	this.layout_id = textViewResourceId;
	this.filter = null;
	this.items_orig = null;
	fav_man = new FavoriteManager(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	View view = convertView;
	if (view == null) {
	    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	    view = inflater.inflate(layout_id, parent, false);
	}
	Room room = items.get(position);
	if (room != null) {
	    TextView title_text = (TextView) view.findViewById(R.id.toptext);
	    TextView description_text = (TextView) view
		    .findViewById(R.id.bottomtext);
	    TextView room_number = (TextView) view
		    .findViewById(R.id.raumnummer);
	    Button favorite_icon = (Button) view.findViewById(R.id.icon);
	    if (title_text != null) {
		title_text.setText(room.getProperty(Property.NAME));
	    }
	    if (description_text != null) {
		description_text.setText(room.getProperty(Property.STREET));
	    }
	    if (room_number != null) {
		room_number.setText(room
			.getProperty(Property.ROOM_NUMBER_INTERNAL));
	    }
	    if (favorite_icon != null) {
		if (fav_man.isFavorite(room
			.getProperty(Property.ROOM_NUMBER_INTERNAL))) {
		    favorite_icon.setCompoundDrawablesWithIntrinsicBounds(0,
			    R.drawable.favorites_small, 0, 0);
		} else {
		    favorite_icon.setCompoundDrawablesWithIntrinsicBounds(0,
			    R.drawable.favorites_small_disabled, 0, 0);
		}
	    }
	}
	return view;
    }

    public Room getItem(int position) {
	return items.get(position);
    }

    public int getPosition(Room item) {
	return items.indexOf(item);
    }

    public long getItemId(int position) {
	return position;
    }

    public int getCount() {
	return items.size();
    }

    public Filter getFilter() {
	if (filter == null) {
	    filter = new ArrayFilter();
	}
	return filter;
    }

    private class ArrayFilter extends Filter {
	@Override
	protected FilterResults performFiltering(CharSequence prefix) {
	    FilterResults results = new FilterResults();

	    // save unfiltered items in items_orig (first time filter is
	    // applied)
	    if (items_orig == null) {
		synchronized (lock) {
		    items_orig = new ArrayList<Room>(items);
		}
	    }

	    // remove filter
	    if (prefix == null || prefix.length() == 0) {
		ArrayList<Room> list;
		synchronized (lock) {
		    list = new ArrayList<Room>(items_orig);
		}
		results.values = list;
		results.count = list.size();
	    } else {
		// start filtering
		ArrayList<Room> newValues = new ArrayList<Room>();
		for (int i = 0; i < items_orig.size(); i++) {
		    if (items_orig.get(i).matchesFilter(prefix)) {
			synchronized (lock) {
			    newValues.add(items_orig.get(i));
			}
		    }
		}

		results.values = newValues;
		results.count = newValues.size();
	    }

	    return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint,
		FilterResults results) {
	    // apply filter -> save filtered results as new item list and notify
	    // the list view to update
	    items = (List<Room>) results.values;
	    if (results.count > 0) {
		notifyDataSetChanged();
	    } else {
		notifyDataSetInvalidated();
	    }
	}
    }

}
