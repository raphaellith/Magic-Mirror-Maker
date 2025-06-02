import java.util.Optional;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static Vector2D zeroVector() {
        return new Vector2D(0, 0);
    }

    public boolean isZeroVector() {
        return DoubleUtil.isZero(x) && DoubleUtil.isZero(y);
    }

//    public boolean equals(Vector2D otherVector) {
//        return minus(otherVector).isZeroVector();
//    }
//
//    public boolean isCollinearWith(Vector2D otherVector) {
//        return DoubleUtil.isZero(x / otherVector.getX() - y / otherVector.getY());
//    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

//    public Vector2D horizontalComponent() {
//        return new Vector2D(x, 0);
//    }
//
//    public Vector2D verticalComponent() {
//        return new Vector2D(0, y);
//    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2D plus(Vector2D otherVector) {
        return new Vector2D(x + otherVector.getX(), y + otherVector.getY());
    }

    public Vector2D minus(Vector2D otherVector) {
        return new Vector2D(x - otherVector.getX(), y - otherVector.getY());
    }

    public Vector2D negated() {
        return new Vector2D(-x, -y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

//    public Vector2D normalised() {
//        return scaled(1/length());
//    }

    public Optional<Vector2D> scaledToLength(double len) {
        if (length() == 0) {
            return Optional.empty();
        }
        return Optional.of(scaled(len / length()));
    }

//    public Optional<Vector2D> normalised() {
//        return scaledToLength(1);
//    }

    public Vector2D scaled(double m) {
        return new Vector2D(x * m, y * m);
    }

//    public double atan2() {  // Returns values from 0 to 2*pi
//        double result = Math.atan2(y, x);
//        if (result < 0) {
//            result += Math.TAU;
//        }
//        return result;
//    }

//    public double dotProduct(Vector2D otherVector) {
//        return x * otherVector.getX() + y * otherVector.getY();
//    }
}
