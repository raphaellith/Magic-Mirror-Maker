public class DoubleUtil {
    public static final double epsilon = 1e-10;

    public static boolean isZero(double d) {
        return Math.abs(d) < epsilon;
    }

    public static boolean isNonzero(double d) {
        return !isZero(d);
    }
}
