package com.example.joinfive.util;

public class TimeUtil {

    /**
     * Converts time to String representation
     *
     * @param time time in milliseconds
     * @return String representation in format Days : Hours : Minutes : Seconds
     */
    public static String toStringRepresentation(long time) {
        int seconds = (int) (time / 1000);
        int days = seconds / 86400;
        seconds = seconds - days * 86400;
        int hours = seconds / 3600;
        seconds = seconds - hours * 3600;
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        return String.format("%2d : %2d : %2d : %2d", days, hours, minutes, seconds);
    }
}
