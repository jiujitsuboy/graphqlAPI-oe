package com.openenglish.hr.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static final int SECONDS_IN_HOUR = 3600;
    public static final int SECONDS_IN_MINUTE = 60;

    public static double round(double number, int decimalPositions){
        return new BigDecimal(number).setScale(decimalPositions, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Convert minutes to seconds
     * @param minutes
     * @return number of seconds
     */
    public static int toSeconds(int minutes) {
        return minutes * SECONDS_IN_MINUTE;
    }

    /**
     * Convert from seconds to hours
     * @param timeInSeconds time in seconds
     * @return time in hours
     */
    public static double convertSecondsToHours(double timeInSeconds) {
        return timeInSeconds / SECONDS_IN_HOUR;
    }
}
