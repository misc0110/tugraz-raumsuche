package com.team4win.tugroom;

import java.util.HashMap;
import java.util.Map;

public class Room {

    private Map<String, String> properties;

    public Room() {
	properties = new HashMap<String, String>();
    }

    public Room(HashMap<String, String> props) {
	properties = new HashMap<String, String>(props);
    }

    public void setProperty(String prop, String value) {
	properties.put(prop, value);
    }

    public String getProperty(String prop) {
	return properties.get(prop);
    }

    public Map<String, String> getPropertyMap() {
	return properties;
    }

    public boolean matchesFilter(CharSequence filter) {
	if (properties.get(Property.NAME).toLowerCase()
		.contains(filter.toString().toLowerCase()))
	    return true;
	if (properties.get(Property.ROOM_ID).toLowerCase()
		.contains(filter.toString().toLowerCase()))
	    return true;
	return false;
    }
}
