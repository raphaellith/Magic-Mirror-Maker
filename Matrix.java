import java.util.ArrayList;
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

    public Matrix(Matrix<T> m) {
        this(m.width, m.height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                matrix[y][x] = m.getElement(x, y);
            }
        }
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

    public boolean validIndices(int x, int y) {
        return 0 <= x && x < width && 0 <= y && y < height;
    }

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

    //    public void setNthRow(int y, T val) {
//        for (int x = 0; x < width; x++) {
//            setElement(x, y, val);
//        }
//    }
//
//    public void setNthColumn(int x, T val) {
//        for (int y = 0; y < height; y++) {
//            setElement(x, y, val);
//        }
//    }

    public List<T> getNeighboursOfElementAt(int x, int y) {
        return Stream.of(
                new Pair<>(x + 1, y),
                new Pair<>(x, y + 1),
                new Pair<>(x - 1, y),
                new Pair<>(x, y - 1)
        )
                .filter(pair -> validIndices(pair.first(), pair.second()))
                .map(pair -> getElement(pair.first(), pair.second()))
                .toList();
    }

//    public boolean setMatrix(T[][] otherMatrix) {
//        if (matrix.length == height && Arrays.stream(matrix).allMatch(ts -> ts.length == width)) {
//            for (int y = 0; y < height; y++) {
//                System.arraycopy(otherMatrix[y], 0, matrix[y], 0, width);
//            }
//            return true;
//        }
//        return false;
//    }

    public Matrix<T> copiedInto(Matrix<T> otherMatrix) {
        // Copies elements into first copy of the otherMatrix, not necessarily of the same size.
        // If this matrix is smaller than the otherMatrix, the other elements in the otherMatrix are unbothered.
        // If this matrix is larger than the otherMatrix, the surplus elements are ignored.

        otherMatrix = new Matrix<>(otherMatrix);

        for (int y = 0; y < Math.min(height, otherMatrix.height); y++) {
            for (int x = 0; x < Math.min(width, otherMatrix.width); x++) {
                otherMatrix.setElement(x, y, getElement(x, y));
            }
        }
        return otherMatrix;
    }

    public Stream<T> asStream() {
        return Arrays.stream(matrix).flatMap(Arrays::stream);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Pair<Integer, Integer>[]> getAllSquareCoordGroups() {
        // Returns a list of Pair<Integer, Integer> arrays, each containing four coordinates that in the matrix forms a
        // 1x1 square.
        // Each Pair<Integer, Integer> array describes squares in clockwise order, starting with the top left corner

        ArrayList<Pair<Integer, Integer>[]> result = new ArrayList<>();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                Pair<Integer, Integer>[] squareVertices = new Pair[] {
                        new Pair<>(x, y),
                        new Pair<>(x+1, y),
                        new Pair<>(x+1, y+1),
                        new Pair<>(x, y+1)
                };

                result.add(squareVertices);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Pair<Integer, Integer>[]> getAllTriangleCoordGroups() {
        /*
        Returns a list of Pair<Integer, Integer> arrays, each containing three elements that in the matrix forms a
        triangle, laid out as follows:
        __________
        | /| /| /| ...
        |/_|/_|/_|
        | /| /| /| ...
        |/_|/_|/_|
            :
            :
         */

        ArrayList<Pair<Integer, Integer>[]> result = new ArrayList<>();

        ArrayList<Pair<Integer, Integer>[]> squareCoordGroups = getAllSquareCoordGroups();

        for (Pair<Integer, Integer>[] square : squareCoordGroups) {
            Pair<Integer, Integer>[] upperTriangleCoordGroup = new Pair[] {square[0], square[1], square[3]};
            Pair<Integer, Integer>[] lowerTriangleCoordGroup = new Pair[] {square[1], square[2], square[3]};
            result.add(upperTriangleCoordGroup);
            result.add(lowerTriangleCoordGroup);
        }

        return result;
    }
}
