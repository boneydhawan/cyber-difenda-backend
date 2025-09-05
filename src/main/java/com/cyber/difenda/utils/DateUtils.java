package com.cyber.difenda.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtils {

	public static String getLastScanDateString(LocalDateTime lastScanDate) {
        if (lastScanDate == null) {
            return "Never";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastScanDate, now);

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) { // less than 1 hour
            long minutes = seconds / 60;
            return minutes + " minutes ago";
        } else if (seconds < 86400) { // less than 1 day
            long hours = seconds / 3600;
            return hours + " hours ago";
        } else {
            long days = seconds / 86400;
            return days + " days ago";
        }
    }
}
