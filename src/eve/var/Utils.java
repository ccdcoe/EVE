package eve.var;

/**
 * Created by jesus on 12/2/16.
 */
public class Utils {
    public static boolean isEquivalent(String s1, String s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null && s2 != null) return false;
        if (s1 != null && s2 == null) return false;

        return s1.trim().toLowerCase().equals(s2.trim().toLowerCase());
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String adaptToLength(String s, int length) {
        if (length < 0 || s == null || s.length() <= length) return s;
        if (length >= 0 && length <= 3) return s.substring(0, length);
        else return s.substring(0, length - 3) + "...";
    }

    public static boolean hasInfo (String s) {
        return s != null && s.trim().length() > 0 ;
    }

    public static void main(String[] args) {
        System.out.println("Utils.adaptToLength(\"Hola\", 0) = " + Utils.adaptToLength("Holaesto ", -8));

    }
}
