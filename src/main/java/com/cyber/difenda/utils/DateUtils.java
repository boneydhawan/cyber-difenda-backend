package com.cyber.difenda.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtils {

	public static String getLastScanDateString(LocalDateTime lastScanDate) {
	    if (lastScanDate == null) {
	        return "Never";
	    }

	    LocalDateTime now = LocalDateTime.now();
	    Duration duration = Duration.between(now, lastScanDate); // note: reversed
	    long seconds = duration.getSeconds();

	    if (seconds >= 0) {
	        // lastScanDate is in the future
	        if (seconds < 60) {
	            return "In " + seconds + " seconds";
	        } else if (seconds < 3600) {
	            long minutes = seconds / 60;
	            return "In " + minutes + " minutes";
	        } else if (seconds < 86400) {
	            long hours = seconds / 3600;
	            return "In " + hours + " hours";
	        } else {
	            long days = seconds / 86400;
	            return "In " + days + " days";
	        }
	    } else {
	        // lastScanDate is in the past
	        seconds = -seconds; // make positive
	        if (seconds < 60) {
	            return seconds + " seconds ago";
	        } else if (seconds < 3600) {
	            long minutes = seconds / 60;
	            return minutes + " minutes ago";
	        } else if (seconds < 86400) {
	            long hours = seconds / 3600;
	            return hours + " hours ago";
	        } else {
	            long days = seconds / 86400;
	            return days + " days ago";
	        }
	    }
	}

}
