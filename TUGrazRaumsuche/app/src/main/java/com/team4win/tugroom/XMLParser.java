package com.team4win.tugroom;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser {

    public List<Room> getRooms(InputSource file, List<CharSequence> categories) {
	try {
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();

	    RoomXMLHandler xml_handler = new RoomXMLHandler();
	    xml_handler.setCategories(categories);
	    reader.setContentHandler(xml_handler);

	    reader.parse(file);
	    return xml_handler.getResult();
	} catch (Exception exception) {
	}

	return null;
    }

    public class RoomXMLHandler extends DefaultHandler {
	private Room room;
	private List<Room> list = new ArrayList<Room>();
	private boolean current = false;
	private String current_value = null;
	private String current_attr = null;
	private boolean is_in_room = false;
	private List<CharSequence> categories = null;

	public List<Room> getResult() {
	    return list;
	}

	public void setCategories(List<CharSequence> categorie_list) {
	    categories = categorie_list;
	}

	@Override
	public void startElement(String uri, String local_name, String q_name,
		Attributes attributes) throws SAXException {
	    current = true;

	    if (local_name.equals("MODULE_RESULT")) {
		is_in_room = true;
		room = new Room();
	    } else if (is_in_room) {
		current_attr = attributes.getValue("name");
	    }
	}

	@Override
	public void endElement(String uri, String local_name, String q_name)
		throws SAXException {
	    current = false;
	    if (!local_name.equals("MODULE_RESULT") && is_in_room) {
		if (current_attr == null)
		    return;
		String property = "";
		if (current_attr.equals("roomID_text"))
		    property = Property.ROOM_NUMBER_INTERNAL;
		else if (current_attr.equals("address"))
		    property = Property.STREET;
		else if (current_attr.equals("purpose"))
		    property = Property.USAGE;
		else if (current_attr.equals("roomCode"))
		    property = Property.ROOM_ID;
		else if (current_attr.equals("additionalInformation"))
		    property = Property.NAME;
		else if (current_attr.equals("seats"))
		    property = Property.SEATS;

		if (!property.equals(""))
		    room.setProperty(property, current_value);
	    }

	    if (local_name.equals("MODULE_RESULT")) {
		if (room.getProperty(Property.NAME) == null)
		    room.setProperty(Property.NAME, room.getProperty(Property.USAGE));
		// check categories
		boolean add_room = true;
		if (categories != null) {
		    add_room = false;

		    String[] usage = room.getProperty(Property.USAGE).split(" ");
		    for (int categorie_nr = 0; categorie_nr < categories.size(); categorie_nr++) {
			for (int i = 0; i < usage.length; i++) {
			    if (usage[i].toLowerCase().equals(
				    categories.get(categorie_nr).toString().toLowerCase())) {
				add_room = true;
				break;
			    }
			}
		    }
		}
		if (add_room)
		    list.add(room);
		is_in_room = false;
	    }
	}

	@Override
	public void characters(char[] ch, int start, int length)
		throws SAXException {

	    if (current) {
		current_value = new String(ch, start, length);
		current = false;
	    }
	}
    }
}
