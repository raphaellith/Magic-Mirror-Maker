import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;


public class ScalarField extends Matrix<Double> implements Visualisable {
    public ScalarField(int width, int height) {
        super(width, height);

        // Initialiise all elements to the double 0.0
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setElement(x, y, 0d);
            }
        }
    }

    public ScalarField(Matrix<Double> matrix) {
        super(matrix.width, matrix.height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setElement(x, y, matrix.getElement(x, y));
            }
        }
    }

    public ScalarField(BufferedImage img, int cropLeft, int cropTop, int cropWidth, int cropHeight) {  // Image file
        super(cropWidth, cropHeight);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int color = img.getRGB(cropLeft + x, cropTop + y);

//                int a = (color >> 24) & 0xff;
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                double grayscaleValue = (r + g + b) / 3.0;

                setElement(x, y, grayscaleValue);
            }
        }
    }

    public DoubleStream asDoubleStream() {
        return asStream().mapToDouble(Double::doubleValue);
    }

    public double getSum() {
        return asDoubleStream().sum();
    }

    public double getMax() throws Exception {
        OptionalDouble optionalMax = asDoubleStream().max();
        if (optionalMax.isPresent()) {
            return optionalMax.getAsDouble();
        } else {
            throw new Exception("Field is empty");
        }
    }

//    public ScalarField negation() {
//        return new ScalarField(getMapped(x -> -x));
//    }

    public ScalarField plus(ScalarField s) throws Exception {
        return new ScalarField(getZippedWith(s, Double::sum));
    }

    public ScalarField minus(ScalarField s) throws Exception {
        return new ScalarField(getZippedWith(s, (a, b) -> a - b));
    }

    public ScalarField times(double factor) {
        return new ScalarField(getMapped(x -> x * factor));
    }

    public ScalarField divideBy(double divisor) {
        return times(1/divisor);
    }

    public BufferedImage drawImage() {
        try {
            ScalarField scaled = times(255 / getMax());

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int val = (int) Math.round(scaled.getElement(x, y));

                    Color c;

                    if (0 <= val && val < 256) {  // Within valid range
                        c = new Color(val, val, val);
                    } else {
                        c = Color.red;
                    }

                    image.setRGB(x, y, c.getRGB());
                }
            }

            return image;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
