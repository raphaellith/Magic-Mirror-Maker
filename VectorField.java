public class VectorField extends Matrix<Vector2D> {
    public VectorField(int width, int height) {
        super(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setElement(x, y, Vector2D.zeroVector());
            }
        }
    }

//    public void setVectorX(int vectorPosX, int vectorPosY, double xCoordInVector) {
//        getElement(vectorPosX, vectorPosY).setX(xCoordInVector);
//    }
//
//    public void setVectorY(int vectorPosX, int vectorPosY, double yCoordInVector) {
//        getElement(vectorPosX, vectorPosY).setY(yCoordInVector);
//    }


//    @Override
//    public BufferedImage drawImage() {
//        int unitDist = 1000 / (Math.max(width, height) + 1);
//
//        try {
//            BufferedImage image = new BufferedImage((width+1) * unitDist, (height+1) * unitDist, BufferedImage.TYPE_INT_RGB);
//
//            Graphics2D graphics = image.createGraphics();
//
//            graphics.transform(AffineTransform.getTranslateInstance(unitDist, unitDist));
//            graphics.transform(AffineTransform.getScaleInstance(unitDist, unitDist));
//
//            graphics.setStroke();
//
//
//            for (int y = 0; y < height; y++) {
//                for (int x = 0; x < width; x++) {
//                    Color c = new Color(100, 0, 0, 255);
//
//                    Vector2D vector = getElement(x, y).normalised().scaled(0.8);
//
//                    graphics.setPaint(c);
//
//                    graphics.drawLine(x, y, (int) Math.round(x + vector.getX()), (int) Math.round(y + vector.getY()));
//
//                    image.setRGB(x, y, c.getRGB());
//                }
//            }
//
//
//
//            return image;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
