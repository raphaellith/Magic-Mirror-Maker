import java.util.*;

public class Lens extends VectorField {
    public Lens(int horizontalNumOfCells, int verticalNumOfCells) {
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
        ScalarField brightnessPercentages = brightnesses.divideBy(brightnesses.getSum());
        ScalarField areaPercentages = getCellAreas().divideBy(getTotalArea());
        return brightnessPercentages.minus(areaPercentages);
    }

    public void marchPointsBasedOnVelocityField(VectorField velField) throws Exception {
        if (!((velField.width + 1 == width) && (velField.height + 1 == height))) {
            throw new Exception("Cannot march lens cell vertices based on velocity field if sizes are incompatible");
        }

        VectorField negativeVelField = new VectorField(
                velField
                        .negated()
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

        // March
        // Length = 0.25 * (average length of side incident to the point in question)
        double proportion = 0.25;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Vector2D> neighbouringVectors = getNeighboursOfElementAt(x, y);

                int finalX = x;
                int finalY = y;

                OptionalDouble optionalAverageLength = neighbouringVectors
                        .stream()
                        .map(vector -> vector.minus(getElement(finalX, finalY)))
                        .mapToDouble(Vector2D::length)
                        .average();
                double averageLength;

                if (optionalAverageLength.isPresent()) {
                    averageLength = optionalAverageLength.getAsDouble();
                } else {
                    throw new Exception("Lens vertex has no neighbours");
                }

                Vector2D change = negativeVelField
                        .getElement(x, y).scaledToLength(averageLength * proportion)  // Optional<Vector2D>
                        .orElse(Vector2D.zeroVector());

                setElement(x, y, getElement(x, y).plus(change));
            }
        }

    }
}
