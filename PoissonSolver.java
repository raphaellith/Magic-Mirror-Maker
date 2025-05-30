import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PoissonSolver {
    /*
    REFERENCES:
    https://math.libretexts.org/Bookshelves/Scientific_Computing_Simulations_and_Modeling/Scientific_Computing_(Chasnov)/I%3A_Numerical_Methods/6%3A_Finite_Difference_Approximation
    https://math.libretexts.org/Bookshelves/Scientific_Computing_Simulations_and_Modeling/Scientific_Computing_(Chasnov)/I%3A_Numerical_Methods/7%3A_Iterative_Methods
    */
    public static int updates = 0;

    public static ScalarField solvePoisson(ScalarField f,
                                           PoissonBoundaryConditions boundaryConditions, PoissonMethod method,
                                           double stabilisationThreshold) {
        ScalarField phi = new ScalarField(f.width, f.height);  // Initialised to zeroes

        // Check whether this is trivial
        // Solving is trivial when the matrix is small enough
        int minSideLen = Math.min(phi.width, phi.height);
        switch (boundaryConditions) {
            case PoissonBoundaryConditions.DIRICHLET -> { if (minSideLen <= 2) { return phi; } }
            case PoissonBoundaryConditions.NEUMANN -> { if (minSideLen == 1) { return phi; } }
        }

        updates = 0;

        // Repeatedly "update" phi until it has stabilised
        Optional<ScalarField> optionalPhi;
        switch (method) {
            case PoissonMethod.JACOBI -> {
                while ((optionalPhi = updateViaJacobi(phi, f, boundaryConditions, stabilisationThreshold)).isPresent()) {
                    phi = optionalPhi.get();
                    System.out.println(updates++);
                }
            }
            case PoissonMethod.GAUSS_SEIDEL -> {
                while ((optionalPhi = updateViaGaussSeidei(phi, f, boundaryConditions, stabilisationThreshold)).isPresent()) {
                    phi = optionalPhi.get();
                    System.out.println(updates++);
                }
            }
        }

        return phi;
    }

    private static List<Double> getNeighboursOfScalarFieldElement(ScalarField field, int x, int y, boolean mirrorAtBoundary) {
        if (!mirrorAtBoundary) {
            return field.getNeighboursOfElementAt(x, y);
        }

        int xLeft = x == 0 ? 1 : x - 1;
        int xRight = x == field.width - 1 ? x - 1 : x + 1;
        int yAbove = y == 0 ? 1 : y - 1;
        int yBelow = y == field.height - 1 ? y - 1 : y + 1;

        return Arrays.asList(
                field.getElement(xRight, y),
                field.getElement(x, yBelow),
                field.getElement(xLeft, y),
                field.getElement(x, yAbove)
        );
    }

    private static HashMap<String, Integer> getXYRangeForUpdate(ScalarField phi, PoissonBoundaryConditions boundaryConditions) {
        HashMap<String, Integer> ranges = new HashMap<>();

        // xMin and yMin are inclusive
        // xMax and yMax are exclusive

        if (boundaryConditions == PoissonBoundaryConditions.DIRICHLET) {
            ranges.put("xMin", 1);
            ranges.put("xMax", phi.width - 1);
            ranges.put("yMin", 1);
            ranges.put("yMax", phi.height - 1);
        } else {  // NEUMANN
            ranges.put("xMin", 0);
            ranges.put("xMax", phi.width);
            ranges.put("yMin", 0);
            ranges.put("yMax", phi.height);
        }

        return ranges;
    }

    private static double getUpdatedValueAt(int x, int y, ScalarField phi, ScalarField f, PoissonBoundaryConditions boundaryConditions) {
        List<Double> neighbours = getNeighboursOfScalarFieldElement(phi, x, y, boundaryConditions == PoissonBoundaryConditions.NEUMANN);

        double sumOfNeighbours = neighbours.stream().mapToDouble(d -> d).sum();

        return (sumOfNeighbours - f.getElement(x, y)) / 4d;
    }

    private static Optional<ScalarField> updateViaJacobi(ScalarField phi, ScalarField f, PoissonBoundaryConditions boundaryConditions, double stabilisationThreshold) {
        /*
        Returns:
        - Optional containing the new updated version of phi, if it has not stabilised
        - Optional.empty(), if it has already stabilised
        */

        boolean stabilised = true;

        ScalarField newPhi = new ScalarField(phi);

        HashMap<String, Integer> ranges = getXYRangeForUpdate(phi, boundaryConditions);

        for (int x = ranges.get("xMin"); x < ranges.get("xMax"); x++) {
            for (int y = ranges.get("yMin"); y < ranges.get("yMax"); y++) {
                double newValue = getUpdatedValueAt(x, y, phi, f, boundaryConditions);

                if (Math.abs(phi.getElement(x, y) - newValue) > stabilisationThreshold) {
                    stabilised = false;
                }

                newPhi.setElement(x, y, newValue);
            }
        }

        return stabilised ? Optional.empty() : Optional.of(newPhi);
    }

    private static Optional<ScalarField> updateViaGaussSeidei(ScalarField phi, ScalarField f, PoissonBoundaryConditions boundaryConditions, double stabilisationThreshold) {
        /*
        Returns:
        - Optional containing the new updated version of phi, if it has not stabilised
        - Optional.empty(), if it has already stabilised
        */

        boolean stabilised = true;

        phi = new ScalarField(phi);

        HashMap<String, Integer> ranges = getXYRangeForUpdate(phi, boundaryConditions);

        for (int x = ranges.get("xMin"); x < ranges.get("xMax"); x++) {
            for (int y = ranges.get("yMin"); y < ranges.get("yMax"); y++) {
                double newValue = getUpdatedValueAt(x, y, phi, f, boundaryConditions);

                if (Math.abs(phi.getElement(x, y) - newValue) > stabilisationThreshold) {
                    stabilised = false;
                }

                phi.setElement(x, y, newValue);
            }
        }

        return stabilised ? Optional.empty() : Optional.of(phi);
    }

    public static void main(String[] args) {  // Test
        try {
            ScalarField testField = solvePoisson(new ScalarField(6, 6), PoissonBoundaryConditions.DIRICHLET, PoissonMethod.GAUSS_SEIDEL, 1e-10);
            System.out.println(testField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
