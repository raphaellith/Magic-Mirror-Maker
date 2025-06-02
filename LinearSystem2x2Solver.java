import java.util.Optional;

public class LinearSystem2x2Solver {  // Solves linear equations in two unknowns
    public static LinearSystem2x2Solution solveLinearSystem(Vector2D xCoefficients, Vector2D yCoefficients, Vector2D constants) {
        double delta = getDiscriminantOfMatrixFormedBy(xCoefficients, yCoefficients);

        double deltaX = getDiscriminantOfMatrixFormedBy(constants, yCoefficients);
        double deltaY = getDiscriminantOfMatrixFormedBy(xCoefficients, constants);

        if (DoubleUtil.isZero(delta)) {
            boolean solutionIsInfinite = DoubleUtil.isZero(deltaX) && DoubleUtil.isZero(deltaY); // rather than nonexistent
            return new LinearSystem2x2Solution(Optional.empty(), !solutionIsInfinite, solutionIsInfinite);
        }

        return new LinearSystem2x2Solution(
                Optional.of(new Vector2D(
                        deltaX / delta,
                        deltaY / delta
                )),
                false,
                false
        );
    }

    private static double getDiscriminantOfMatrixFormedBy(Vector2D col1, Vector2D col2) {
        return col1.getX() * col2.getY() - col2.getX() * col1.getY();
    }
}
