package th.ac.chula.fims.utils;

public class NumberUtils {
    public static boolean isNumeric(String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
