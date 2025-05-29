import java.util.ArrayList;

public class AreaCalculator {
    public static double getPolygonArea(ArrayList<Vector2D> vertices) {
        // Assumes vertices are in clockwise or counterclockwise order

        // Shoelace formula
        int numOfVertices = vertices.size();

        double area = 0;

        for (int i = 0; i < numOfVertices; i++) {
            int j = (i + 1) % numOfVertices;

            Vector2D currVertex = vertices.get(i);
            Vector2D nextVertex = vertices.get(j);

            area += currVertex.getX() * nextVertex.getY() - currVertex.getY() * nextVertex.getX();
        }

        area /= 2;

        return Math.abs(area);
    }
}
