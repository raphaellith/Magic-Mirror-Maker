public class DoubleUtil {
    // Checks whether a double value is zero whilst preventing floating-point issues
    public static final double epsilon = 1e-10;

    public static boolean isZero(double d) {
        return Math.abs(d) < epsilon;
    }

    public static boolean isNonzero(double d) {
        return !isZero(d);
    }
}
