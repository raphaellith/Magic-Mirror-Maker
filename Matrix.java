import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Matrix<T> {
    protected int width;
    protected int height;
    protected T[][] matrix;

    @SuppressWarnings("unchecked")
    public Matrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = (T[][]) new Object[width][height];
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[\n");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.append(getElement(x, y));
                result.append('\t');
            }
            result.append('\n');
        }
        result.append("]");
        return result.toString();
    }

//    public boolean validIndices(int x, int y) {
//        return 0 <= x && x < width && 0 <= y && y < height;
//    }

    public <T2> boolean hasSameShapeAs(Matrix<T2> otherMatrix) {
        return width == otherMatrix.width && height == otherMatrix.height;
    }

//    public Matrix<T> copy() {
//        Matrix<T> copy = new Matrix<>(width, height);
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                setElement(x, y, matrix[y][x]);
//            }
//        }
//        return copy;
//    }

//    public void map(Function<T, T> func) {
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                setElement(x, y, func.apply(matrix[y][x]));
//            }
//        }
//    }

    public <R> Matrix<R> getMapped(Function<T, R> func) {
        Matrix<R> result = new Matrix<>(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setElement(x, y, func.apply(matrix[y][x]));
            }
        }
        return result;
    }

    public <T2, R> Matrix<R> getZippedWith(Matrix<T2> otherMatrix, BiFunction<T, T2, R> func) throws Exception {
        if (!hasSameShapeAs(otherMatrix)) {
            throw new Exception("Cannot perform zip operation on matrices of different sizes");
        }

        Matrix<R> result = new Matrix<>(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                R funcOutput = func.apply(getElement(x, y), otherMatrix.getElement(x, y));
                result.setElement(x, y, funcOutput);
            }
        }

        return result;
    }

    public T getElement(int x, int y) {
        return matrix[y][x];
    }

    public void setElement(int x, int y, T val) {
        matrix[y][x] = val;
    }

    public void setNthRow(int y, T val) {
        for (int x = 0; x < width; x++) {
            setElement(x, y, val);
        }
    }

    public void setNthColumn(int x, T val) {
        for (int y = 0; y < height; y++) {
            setElement(x, y, val);
        }
    }

    public List<T> getNeighboursOfElementAt(int x, int y) {
        return Arrays.asList(
            getElement(x + 1, y),
            getElement(x, y + 1),
            getElement(x - 1, y),
            getElement(x, y - 1)
        );
    }

    public Stream<T> asStream() {
        return Arrays.stream(matrix).flatMap(Arrays::stream);
    }
}
