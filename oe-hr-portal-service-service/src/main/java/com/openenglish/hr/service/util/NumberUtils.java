package com.openenglish.hr.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static final int SECONDS_IN_HOUR = 3600;
    public static final int SECONDS_IN_MINUTE = 60;

    /**
     * Round a double to the specified decimal positions
     * @param number number
     * @param decimalPositions number of decimal position to round the number
     * @return number rounded
     */
    public static double round(double number, int decimalPositions){
        return new BigDecimal(number).setScale(decimalPositions, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * round a double to two decimal positions
     * @param number number
     * @return number rounded
     */
    public static double round(double number){
        final int DEFAULT_PRECISION = 2 ;
        return round(number, DEFAULT_PRECISION);
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
