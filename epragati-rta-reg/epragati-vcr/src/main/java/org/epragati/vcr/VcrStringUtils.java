package org.epragati.vcr;

public class VcrStringUtils {

    public static boolean isNullOrEmpty(String str) {
        return (str == null) || str.trim().equals("");
    }

    public static boolean isNumeric(String str) {
        if(isNullOrEmpty(str))
            return false;
        return str.matches("-?\\d+(.\\d+)?");
    }
}
