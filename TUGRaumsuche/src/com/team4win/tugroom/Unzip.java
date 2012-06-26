package com.team4win.tugroom;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xml.sax.InputSource;

public class Unzip {
    public static void unzip(String file, String location,
	    UpdateTask.NotificationHelper helper) {

	if (!location.endsWith("/"))
	    location += "/";
	try {
	    ZipInputStream input = new ZipInputStream(new FileInputStream(file));
	    ZipEntry entry = null;
	    while ((entry = input.getNextEntry()) != null) {
		FileOutputStream output = new FileOutputStream(location
			+ entry.getName());
		BufferedOutputStream output_stream = new BufferedOutputStream(
			output, 4096);
		byte[] buffer = new byte[4096];
		int count;
		int progress_count = 0;
		while ((count = input.read(buffer)) != -1) {
		    output_stream.write(buffer, 0, count);
		    progress_count++;
		    if (progress_count == 128 && helper != null) {
			helper.progressUpdate(0);
			progress_count = 0;
		    }
		    if (helper != null && helper.isStopped())
			return;
		}
		output_stream.flush();
		output_stream.close();
	    }
	    input.close();
	} catch (Exception exception) {
	}
    }

    public static InputSource getInputSource(String file) {
	try {
	    ZipInputStream input = new ZipInputStream(new FileInputStream(file));
	    input.getNextEntry();
	    return new InputSource(input);
	} catch (Exception exception) {
	    return null;
	}
    }
}
