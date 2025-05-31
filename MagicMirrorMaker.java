import java.awt.image.BufferedImage;

public class MagicMirrorMaker {
//    public static void main(String[] args) {
//        try {
//            VectorField velField = new VectorField("Files/velField0.csv");
//            Lens lens = new Lens(4, 4);;
//            System.out.println(lens);
//            lens.marchPointsBasedOnVelocityField(new VectorField(4, 4));
//            lens.exportToCSV(createCSVFileName("lens", iterations));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        try {
            BufferedImage image = ImageHandler.loadImage("Files/UncroppedImage.jpg");
            image = ImageHandler.scaleBy(image, 0.25);

            ScalarField targetBrightnesses = new ScalarField(image, 162, 15, 256, 256);

            Lens lens = new Lens(targetBrightnesses.width, targetBrightnesses.height);

            int iterations = 0;
            while (iterations < 5) {
                ScalarField loss = lens.getLoss(targetBrightnesses);
                loss.exportToCSV(createCSVFileName("loss", iterations));

                /*
                TESTS FOR DIFFERENT OVERCORRECTION VALUES

                Value       Max diff after 10000 updates
                1.0         4.00 E-7
                1.0625      3.27 E-7
                1.125       2.98 E-7
                1.1875      3.20 E-7
                1.25        3.36 E-7
                1.5         3.54 E-7
                1.75        5.5 E-7
                1.9         1.48 E-6
                1.94        2.52 E-6
                 */

                ScalarField phi = PoissonSolver.solvePoisson(loss, 1.125, PoissonBoundaryConditions.NEUMANN, PoissonMethod.GAUSS_SEIDEL, 1e-10, 150000);
                phi.exportToCSV(createCSVFileName("poissonSolution", iterations));

                VectorField velField = GradientCalculator.getGradient(phi);  // = gradient(phi)

                velField.exportToCSV(createCSVFileName("velField", iterations));

                lens.marchPointsBasedOnVelocityField(velField);
                lens.exportToCSV(createCSVFileName("lens", iterations));

                iterations++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long endTime = System.nanoTime();
            long runtime = (endTime - startTime) / 1000000000;  // in seconds
            System.out.println("FINAL RUNTIME: " + runtime + " secs");
        }
    }

    public static String createCSVFileName(String identifier, int iterations) {
        return "Files/" + identifier + iterations + ".csv";
    }
}
