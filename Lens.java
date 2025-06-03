import java.util.*;

public class Lens extends VectorField {
    public Lens(int horizontalNumOfCells, int verticalNumOfCells) {
        // Width = horizontalNumOfCells + 1
        // Height = verticalNumOfCells + 1

        super(horizontalNumOfCells + 1, verticalNumOfCells + 1);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, new Vector2D(x, y));
            }
        }
    }

    public Lens(VectorField vectorField) {
        super(vectorField.width, vectorField.height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, vectorField.getElement(x, y));
            }
        }
    }

    public Lens(String csvFileName) throws Exception {
        this(new VectorField(csvFileName));
    }

    public ScalarField getCellAreas() {
        // Returns a scalar field representing the area of each lens cell

        ScalarField result = new ScalarField(width - 1, height - 1);

        ArrayList<Vector2D> cellVertices = new ArrayList<>();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                cellVertices.clear();

                cellVertices.add(getElement(x, y));
                cellVertices.add(getElement(x+1, y));
                cellVertices.add(getElement(x+1, y+1));
                cellVertices.add(getElement(x, y+1));

                double area = AreaCalculator.getPolygonArea(cellVertices);

                result.setElement(x, y, area);
            }
        }

        return result;
    }

    public double getTotalArea() {
        return width * height;
    }

    public ScalarField getLoss(ScalarField brightnesses) throws Exception {
        // Given the target brightnesses from the image, computes the loss of this lens
        ScalarField brightnessPercentages = brightnesses.divideBy(brightnesses.getSum());
        ScalarField areaPercentages = getCellAreas().divideBy(getTotalArea());
        return brightnessPercentages.minus(areaPercentages);
    }

    public void marchPointsBasedOnVelocityField(VectorField velField) throws Exception {
        marchPointsBasedOnVelocityField(velField, 0.5);
    }

    public void marchPointsBasedOnVelocityField(VectorField velField, double extent) throws Exception {
        // Morph the lens based on the velocity field by morphing the lens vertices
        // "extent" is a double value between 0 and 1, describing the extent to which the lens is morphed

        VectorField negativeVelField = getAndPrepareNegativeVelocityFieldForMarching(velField);

        VectorField resultantField = new VectorField(width, height);

        // Determine how much to march
        double minT = Double.POSITIVE_INFINITY;

        ArrayList<Pair<Integer, Integer>[]> triangleCoordGroups = getAllTriangleCoordGroups();

        for (Pair<Integer, Integer>[] triangleCoordGroup : triangleCoordGroups) {
            int x0 = triangleCoordGroup[0].first();
            int y0 = triangleCoordGroup[0].second();
            int x1 = triangleCoordGroup[1].first();
            int y1 = triangleCoordGroup[1].second();
            int x2 = triangleCoordGroup[2].first();
            int y2 = triangleCoordGroup[2].second();

            Vector2D p0 = getElement(x0, y0);
            Vector2D p1 = getElement(x1, y1);
            Vector2D p2 = getElement(x2, y2);

            Vector2D v0 = negativeVelField.getElement(x0, y0);
            Vector2D v1 = negativeVelField.getElement(x1, y1);
            Vector2D v2 = negativeVelField.getElement(x2, y2);

            double t = minTimeToReduceAreaEnclosedByMovingPointsToZero(p0, p1, p2, v0, v1, v2).orElse(0);
            if (DoubleUtil.isNonzero(t)) {
                minT = Math.min(minT, t);
            }
        }

        assert Double.isFinite(minT);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                resultantField.setElement(x, y, getElement(x, y).plus(negativeVelField.getElement(x, y).scaled(minT * extent)));
            }
        }


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, resultantField.getElement(x, y));
            }
        }
    }

    private VectorField getAndPrepareNegativeVelocityFieldForMarching(VectorField velField) throws Exception {
        if (!((velField.width + 1 == width) && (velField.height + 1 == height))) {
            throw new Exception("Cannot march lens cell vertices based on velocity field if sizes are incompatible");
        }

        VectorField negativeVelField = new VectorField(
                velField.negated()
                        .copiedInto(new VectorField(velField.width + 1, velField.height + 1))
        );

        // Handle right and bottom boundaries
        for (int x = 0; x < width - 1; x++) {
            negativeVelField.setElement(x, height - 1, negativeVelField.getElement(x, height - 2));
        }
        for (int y = 0; y < height; y++) {
            negativeVelField.setElement(width - 1, y, negativeVelField.getElement(width - 2, y));
        }

        for (int x = 0; x < width; x++) {
            negativeVelField.getElement(x, 0).setY(0);
            negativeVelField.getElement(x, height - 1).setY(0);
        }
        for (int y = 0; y < height; y++) {
            negativeVelField.getElement(0, y).setX(0);
            negativeVelField.getElement(width - 1, y).setX(0);
        }

        return negativeVelField;
    }

    private OptionalDouble minTimeToReduceAreaEnclosedByMovingPointsToZero(Vector2D p1, Vector2D p2, Vector2D p3, Vector2D v1, Vector2D v2, Vector2D v3) {
        // Consider three moving points p1, p2 and p3, each with velocity v1, v2 and v3 respectively
        // Returns the minimum amount of time required for the area of the triangle p1-p2-p3 to drop to zero.
        // We reduce this to a simpler version of the problem where p1 is set to be the coordinate system's origin

        return minTimeToReduceAreaEnclosedByMovingPointsToZero(p2.minus(p1), p3.minus(p1), v2.minus(v1), v3.minus(v1));

    }

    private OptionalDouble minTimeToReduceAreaEnclosedByMovingPointsToZero(Vector2D p1, Vector2D p2, Vector2D v1, Vector2D v2) {
        // Consider two moving points p1 and p2, each with velocity v1 and v2 respectively
        // Returns the minimum amount of time required for the area of the triangle O-p1-p2 to drop to zero,
        // where O is the origin.

        // Note that the area of the triangle O-p1-p2 equals half of the abs value of
        // the determinant of the matrix with columns p1 and p2, which allows us to derive a quadratic equation in t
        // Hence the following computations

        double a = v1.getX() * v2.getY() - v1.getY() * v2.getX();
        double b = p1.getX() + v2.getY() + p2.getY() * v1.getX() - p2.getX() * v1.getY() - p1.getY() * v2.getX();
        double c = p1.getX() * p2.getY() - p2.getX() * p1.getY();

        if (DoubleUtil.isZero(a)) {  // Linear equation
            double solution = -c/b;
            if (solution < 0 || DoubleUtil.isZero(solution)) {
                return OptionalDouble.empty();
            } else {
                return OptionalDouble.of(solution);
            }

        }

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return OptionalDouble.empty();
        }

        // Two distinct roots
        // We return the smaller root that is positive

        double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);

        boolean root1Invalid = (root1 < 0) || DoubleUtil.isZero(root1);
        boolean root2Invalid = (root2 < 0) || DoubleUtil.isZero(root2);

        if (root1Invalid && root2Invalid) {
            return OptionalDouble.empty();
        }

        if (root1Invalid) {
            return OptionalDouble.of(root2);
        }

        if (root2Invalid) {
            return OptionalDouble.of(root1);
        }

        return OptionalDouble.of(Math.min(root1, root2));
    }
}
