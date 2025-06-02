import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class VectorField extends Matrix<Vector2D> implements ExportableToCSV {
    public VectorField(int width, int height) {
        super(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, Vector2D.zeroVector());
            }
        }
    }

    public VectorField(Matrix<Vector2D> matrix) {
        super(matrix.width, matrix.height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setElement(x, y, matrix.getElement(x, y));
            }
        }
    }

    public VectorField(String csvFileName) throws Exception {
        this(getMatrixOfVectorsFromCSVFile(csvFileName));
    }

    private static Matrix<Vector2D> getMatrixOfVectorsFromCSVFile(String csvFileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(csvFileName));

        int xMax = 0;
        int yMax = 0;
        ArrayList<HashMap<String, Number>> rows = new ArrayList<>();  // x -> y -> vector elements

        String line;
        while ((line = reader.readLine()) != null) {
            String[] items = line.split(", ");

            HashMap<String, Number> rowData = new HashMap<>();

            int x = Integer.parseInt(items[0]);
            int y = Integer.parseInt(items[1]);
            double vx = Double.parseDouble(items[2]);
            double vy = Double.parseDouble(items[3]);
            xMax = Math.max(xMax, x);
            yMax = Math.max(yMax, y);

            rowData.put("x", x);
            rowData.put("y", y);
            rowData.put("vx", vx);
            rowData.put("vy", vy);

            rows.add(rowData);
        }

        Matrix<Vector2D> matrix = new Matrix<>(xMax + 1, yMax + 1);

        for (HashMap<String, Number> rowData : rows) {
            matrix.setElement(
                    (int) rowData.get("x"),
                    (int) rowData.get("y"),
                    new Vector2D((double) rowData.get("vx"), (double) rowData.get("vy"))
                    );
        }

        return matrix;
    }

    public VectorField negated() {
        return new VectorField(getMapped(Vector2D::negated));
    }

    @Override
    public String toCSVString() {
        // xPos, yPos, vectorX, vectorY

        StringBuilder result = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Vector2D vector = getElement(x, y);
                result.append(x);
                result.append(", ");
                result.append(y);
                result.append(", ");
                result.append(vector.getX());
                result.append(", ");
                result.append(vector.getY());
                result.append('\n');
            }
        }

        return result.toString();
    }
}
