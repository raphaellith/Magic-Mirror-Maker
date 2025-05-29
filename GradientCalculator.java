public class GradientCalculator {
    // REFERENCE:
    // https://math.libretexts.org/Bookshelves/Scientific_Computing_Simulations_and_Modeling/Scientific_Computing_(Chasnov)/I%3A_Numerical_Methods/6%3A_Finite_Difference_Approximation

    public static VectorField getGradient(ScalarField field) {
        VectorField gradient = new VectorField(field.width, field.height);

        for (int y = 0; y < field.height; y++) {
            for (int x = 0; x < field.width; x++) {
                gradient.setElement(x, y, new Vector2D(
                        getPartialDerivativeWithRespectToX(field, x, y),
                        getPartialDerivativeWithRespectToY(field, x, y)
                ));
            }
        }

        return gradient;
    }

    private static double getPartialDerivativeWithRespectToX(ScalarField field, int x, int y) {
        // On left boundary
        if (x == 0) {
            return (
                    -3 * field.getElement(x, y)
                    + 4 * field.getElement(x + 1, y)
                    - field.getElement(x + 2, y)
            ) / 2.0;
        }

        // On right boundary
        if (x == field.width - 1) {
            return (
                    3 * field.getElement(x, y)
                    - 4 * field.getElement(x - 1, y)
                    + field.getElement(x - 2, y)
            ) / 2.0;
        }

        return (field.getElement(x + 1, y) - field.getElement(x - 1, y)) / 2.0;
    }

    private static double getPartialDerivativeWithRespectToY(ScalarField field, int x, int y) {
        // On top boundary
        if (y == 0) {
            return (
                    -3 * field.getElement(x, y)
                            + 4 * field.getElement(x, y + 1)
                            - field.getElement(x, y + 2)
            ) / 2.0;
        }

        // On bottom boundary
        if (y == field.height - 1) {
            return (
                    3 * field.getElement(x, y)
                            - 4 * field.getElement(x, y - 1)
                            + field.getElement(x, y - 2)
            ) / 2.0;
        }

        return (field.getElement(x, y + 1) - field.getElement(x, y - 1)) / 2.0;
    }
}
