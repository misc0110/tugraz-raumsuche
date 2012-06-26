package com.team4win.tugroom;

import java.util.Comparator;

public class NaturalRoomComparator implements Comparator<Room> {
    @Override
    public int compare(Room room1, Room room2) {
	String room_name1 = room1.getProperty(Property.NAME);
	String room_name2 = room2.getProperty(Property.NAME);
	
	room_name1 = room_name1.toLowerCase();
	room_name2 = room_name2.toLowerCase();

	int len_room_name1 = room_name1.length();
	int len_room_name2 = room_name2.length();
	int ptr_room1 = 0, ptr_room2 = 0;
	char current_char_room1, current_char_room2;
	
	while(ptr_room1 < len_room_name1 && ptr_room2 < len_room_name2) {
	    current_char_room1 = room_name1.charAt(ptr_room1);
	    current_char_room2 = room_name2.charAt(ptr_room2);
	    // if both are digits, compare numerically
	    if(Character.isDigit(current_char_room1) && Character.isDigit(current_char_room2)) {
		// parse integers
		String integer_room2 = "", integer_room1 = "";
		while(ptr_room1 < len_room_name1 && Character.isDigit((current_char_room1 = room_name1.charAt(ptr_room1)))) {
		    integer_room1 += current_char_room1;
		    ptr_room1++;
		}
		while(ptr_room2 < len_room_name2 && Character.isDigit((current_char_room2 = room_name2.charAt(ptr_room2)))) {
		    integer_room2 += current_char_room2;
		    ptr_room2++;
		}
		int number_room1 = 0, number_room2 = 0;
		try {
		    number_room1 = Integer.parseInt(integer_room1);
		    number_room2 = Integer.parseInt(integer_room2);
		} catch(Exception e) {
		    return 0;
		}
		
		if(number_room1 != number_room2) {
		    return (number_room1 > number_room2) ? 1 : -1;
		}
	    } else {
		if(current_char_room1 == current_char_room2) {
		    ptr_room1++;
		    ptr_room2++;
		    continue;
		}
		else return (current_char_room1 > current_char_room2) ? 1 : -1; 
	    }
	}
	
	return 0;
    }
}
