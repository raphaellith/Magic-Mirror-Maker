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
    private static double maxDiff = -1;
    private static int solutionID = 0;

    public static ScalarField solvePoisson(ScalarField f, PoissonBoundaryConditions boundaryConditions,
                                           PoissonMethod method, double stabilisationThreshold, int maxUpdates) {
        return solvePoisson(f, -1, boundaryConditions, method, stabilisationThreshold, maxUpdates);
    }

    public static ScalarField solvePoisson(ScalarField f, double overcorrectionFactor,
                                           PoissonBoundaryConditions boundaryConditions, PoissonMethod method,
                                           double stabilisationThreshold, int maxUpdates) {
        ScalarField phi = new ScalarField(f.width, f.height);  // Initialised to zeroes

        // Check whether this is trivial
        // Solving is trivial when the matrix is small enough
        int minSideLen = Math.min(phi.width, phi.height);
        switch (boundaryConditions) {
            case PoissonBoundaryConditions.DIRICHLET -> { if (minSideLen <= 2) { return phi; } }
            case PoissonBoundaryConditions.NEUMANN -> { if (minSideLen == 1) { return phi; } }
        }

        int updates = 0;

        // Repeatedly "update" phi until it has stabilised
        Optional<ScalarField> optionalPhi;
        while ((optionalPhi = update(phi, f, overcorrectionFactor, boundaryConditions, method, stabilisationThreshold)).isPresent() && updates < maxUpdates) {
            phi = optionalPhi.get();
            updates++;

            if (updates % 50 == 0) {
                System.out.println(solutionID + "\tUpdate " + updates + "\t\tMax diff = " + maxDiff);
            }
        }

        solutionID++;

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

    private static double getUpdatedValueAt(int x, int y, ScalarField phi, ScalarField f, double oldValue, double overcorrectionFactor, PoissonBoundaryConditions boundaryConditions) {
        List<Double> neighbours = getNeighboursOfScalarFieldElement(phi, x, y, boundaryConditions == PoissonBoundaryConditions.NEUMANN);

        double sumOfNeighbours = neighbours.stream().mapToDouble(d -> d).sum();
        double result = (sumOfNeighbours - f.getElement(x, y)) / 4d;

        if (overcorrectionFactor < 0) {
            return result;
        }
        double correction = result - oldValue;
        return oldValue + correction * overcorrectionFactor;
    }

    private static Optional<ScalarField> update(ScalarField phi, ScalarField f, double overcorrectionFactor, PoissonBoundaryConditions boundaryConditions, PoissonMethod method, double stabilisationThreshold) {
        /*
        Returns:
        - Optional containing the new updated version of phi, if it has not stabilised
        - Optional.empty(), if it has already stabilised
        */

        boolean stabilised = true;
        maxDiff = 0d;

        ScalarField newPhi;
        if (method == PoissonMethod.JACOBI) {
            newPhi = new ScalarField(phi);
        } else {  // GAUSS_SEIDEI - no need for auxiliary array
            newPhi = phi;
        }

        HashMap<String, Integer> ranges = getXYRangeForUpdate(phi, boundaryConditions);

        for (int x = ranges.get("xMin"); x < ranges.get("xMax"); x++) {
            for (int y = ranges.get("yMin"); y < ranges.get("yMax"); y++) {
                double oldValue;
                double newValue;
                double diff;

                if (method == PoissonMethod.JACOBI) {
                    oldValue = phi.getElement(x, y);
                    newValue = getUpdatedValueAt(x, y, phi, f, oldValue, overcorrectionFactor, boundaryConditions);
                } else {
                    oldValue = newPhi.getElement(x, y);
                    newValue = getUpdatedValueAt(x, y, newPhi, f, oldValue, overcorrectionFactor, boundaryConditions);
                }

                diff = Math.abs(newValue - oldValue);
                maxDiff = Math.max(maxDiff, diff);

                if (diff > stabilisationThreshold) {
                    stabilised = false;
                }

                newPhi.setElement(x, y, newValue);
            }
        }

        return stabilised ? Optional.empty() : Optional.of(newPhi);
    }
}
