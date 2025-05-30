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

    public VectorField negated() {
        return new VectorField(getMapped(Vector2D::negated));
    }
}
