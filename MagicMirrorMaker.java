import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MagicMirrorMaker {
    public static void main(String[] args) {
        BufferedImage image;
        try {
            File imgFile = new File("UncroppedImage.jpg");
            image = ImageIO.read(imgFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assert image != null;

        ScalarField brightnesses = new ScalarField(image, 650, 60, 1024, 1024);

        brightnesses.divideBy(brightnesses.getSum());

//        brightnesses.drawImage("saved.png");

        Lens lens = new Lens(brightnesses.width, brightnesses.height);
    }
}
