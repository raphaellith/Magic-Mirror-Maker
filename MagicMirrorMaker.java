import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MagicMirrorMaker {
    public static void main(String[] args) {
        try {
            BufferedImage image;
            File imgFile = new File("UncroppedImage.jpg");
            image = ImageIO.read(imgFile);

            // Resize to half size
            BufferedImage resizedImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, image.getWidth() / 2, image.getHeight() / 2, null);
            graphics2D.dispose();
            image = resizedImage;

            ScalarField targetBrightnesses = new ScalarField(image, 325, 30, 512, 512);

            Lens lens = new Lens(targetBrightnesses.width, targetBrightnesses.height);

            ScalarField loss = lens.getLoss(targetBrightnesses);
//            loss.exportToCSV("test.csv");

            ScalarField phi = PoissonSolver.solvePoisson(loss, PoissonBoundaryConditions.NEUMANN, PoissonMethod.GAUSS_SEIDEL, 1e-9);

            phi.exportToCSV("poissonTest.csv");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
