import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageHandler {
    public static BufferedImage loadImage(String filePath) throws IOException {
        return ImageIO.read(new File(filePath));
    }

    public static BufferedImage cropImage(BufferedImage in, int cropLeft, int cropTop, int cropWidth, int cropHeight) {
        return in.getSubimage(cropLeft, cropTop, cropWidth, cropHeight);
    }

    public static void saveImage(BufferedImage image, String filePath) throws IOException {
        String[] substrings = filePath.split("\\.");
        String format = substrings[substrings.length - 1];
        ImageIO.write(image, format, new File(filePath));
    }

    public static BufferedImage toGreyscale(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getRGB(x, y);

//                int first = (color >> 24) & 0xff;
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                int greyscale = (r + g + b) / 3;
                Color c = new Color(greyscale, greyscale, greyscale);

                image.setRGB(x, y, c.getRGB());
            }
        }
        return image;
    }

    public static BufferedImage scaleBy(BufferedImage image, double scale) {
        return resize(image, (int) Math.round(image.getWidth() * scale), (int) Math.round(image.getHeight() * scale));
    }

    public static BufferedImage resize(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
