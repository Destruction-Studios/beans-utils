package net.ds;

public class Utils {
    public static String tickToString(int tick) {
        return String.format("%.1f", (float) tick / 20);
    }
}
