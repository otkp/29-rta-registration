package org.epragati.vahan.common.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.epragati.vahan.exception.InvalidManufacturingDateFormatException;
import org.epragati.vahan.util.StringsUtil;

/**
 * Class for processing manufacturing date provided by vahan-api.</br>
 * Only mfgDate format with only 6 digits '072016' is valid.
 * 
 * @author rahul.sharma
 *
 */
public class ManufacturingDate {

    private static final Logger logger = Logger.getLogger(ManufacturingDate.class);

    private static final String REGEX_MFG_DATE_FORMAT = "\\D*\\d{6}\\D*";
    private String manufacturingDate;
    private String month;
    private String year;

    public ManufacturingDate(String manufacturingDate) throws InvalidManufacturingDateFormatException {
        this.manufacturingDate = manufacturingDate;
        init();
    }

    private void init() throws InvalidManufacturingDateFormatException {
        validateFormat();
        month = StringsUtil.getStartingChars(manufacturingDate,
                2/* first 2 character are considered as month */);
        year = StringsUtil.getLastChars(manufacturingDate,
                4/* last 4 characters are considered as year */);
    }

    /**
     * returns the actual mfgDate.</br>
     * {@code null} is returned if mfgDate format is not valid.
     * 
     * @return {@code month}
     */
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * returns the month extracted from mfgDate.</br>
     * {@code null} is returned if mfgDate format is not valid.
     * 
     * @return {@code month}
     */
    public String getMonth() {
        return month;
    }

    /**
     * returns the year extracted from mfgDate.</br>
     * {@code null} is returned if mfgDate format is not valid.
     * 
     * @return {@code year}
     */
    public String getYear() {
        return year;
    }

    public void validateFormat() throws InvalidManufacturingDateFormatException {
        if (manufacturingDate == null) {
            throw new NullPointerException("mfg date format can't be null");
        }
        final Pattern p = Pattern.compile(REGEX_MFG_DATE_FORMAT);
        final Matcher m = p.matcher(manufacturingDate);
        if (!m.matches()) {
            logger.warn("invalid manufacturing date format found, " + manufacturingDate);
            throw new InvalidManufacturingDateFormatException("mfg date format is invalid");
        }
    }

    // only for test
    public static void main(String[] args) {
        ManufacturingDate mfgDate;
        try {
            mfgDate = new ManufacturingDate("072016");
        } catch (InvalidManufacturingDateFormatException e) {
            mfgDate = null;
        }
        System.out.println(mfgDate.getMonth());
        System.out.println(mfgDate.getYear());
    }
}
