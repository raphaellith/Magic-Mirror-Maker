import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Visualiser {
    public static void writeToImage(Visualisable vis, String fileName) {
        try {
            BufferedImage image = vis.drawImage();

            File outputFile = new File(fileName);

            String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
            ImageIO.write(image, fileExtension, outputFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
