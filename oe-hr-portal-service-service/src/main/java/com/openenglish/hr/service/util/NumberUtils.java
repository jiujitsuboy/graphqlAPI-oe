package com.openenglish.hr.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
    public static double round(double number, int decimalPositions){
        return new BigDecimal(number).setScale(decimalPositions, RoundingMode.HALF_UP).doubleValue();
    }
}
