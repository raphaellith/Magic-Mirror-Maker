import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;


public class ScalarField extends Matrix<Double> implements ExportableToCSV {
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

    public ScalarField(String csvFileName) throws Exception {
        this(getMatrixOfDoublesFromCSVFile(csvFileName));
    }

    private static Matrix<Double> getMatrixOfDoublesFromCSVFile(String csvFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(csvFileName));

        // Create a tempMatrix (not of class Matrix) that is resizable; read elements from CSV file
        ArrayList<double[]> tempMatrix = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            double[] newRow = Arrays.stream(line.split(",")).mapToDouble(Double::valueOf).toArray();
            if (!tempMatrix.isEmpty() && tempMatrix.getFirst().length != newRow.length) {
                throw new Exception("Scalar field cannot be read from CSV as the row lengths are inconsistent");
            }
            tempMatrix.add(newRow);
        }

        // Convert ArrayList to actual Matrix object
        Matrix<Double> matrix = new Matrix<>(tempMatrix.getFirst().length, tempMatrix.size());
        for (int y = 0; y < matrix.height; y++) {
            for (int x = 0; x < matrix.width; x++) {
                matrix.setElement(x, y, tempMatrix.get(y)[x]);
            }
        }
        return matrix;
    }

    public DoubleStream asDoubleStream() {
        return asStream().mapToDouble(Double::doubleValue);
    }

    public double getSum() {
        return asDoubleStream().sum();
    }

//    public double getMax() throws Exception {
//        OptionalDouble optionalMax = asDoubleStream().max();
//        if (optionalMax.isPresent()) {
//            return optionalMax.getAsDouble();
//        } else {
//            throw new Exception("Field is empty");
//        }
//    }

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

    @Override
    public String toCSVString() {
        // Displays doubles row by row
        StringBuilder result = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.append(getElement(x, y));
                if (x != width - 1) {
                    result.append(", ");
                }
            }
            result.append('\n');
        }

        return result.toString();
    }
}
