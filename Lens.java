import java.util.ArrayList;

public class Lens extends VectorField {
    public Lens(int horizontalNumOfCells, int verticalNumOfCells) {
        super(horizontalNumOfCells + 1, verticalNumOfCells + 1);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, new Vector2D(x, y));
            }
        }
    }

    public ScalarField getCellAreas() {
        ScalarField result = new ScalarField(width - 1, height - 1);

        ArrayList<Vector2D> cellVertices = new ArrayList<>();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                cellVertices.clear();

                // Counterclockwise
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
}
