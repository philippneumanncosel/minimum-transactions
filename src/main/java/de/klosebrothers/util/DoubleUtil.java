package de.klosebrothers.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtil {

    private DoubleUtil(){
    }

    public static double roundToTwoPlaces(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
