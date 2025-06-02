public class Test {
    public static void main(String[] args) {
        try {
            Lens lens = new Lens(256, 256);
            VectorField velField = new VectorField("Files/velField0.csv");
            lens.marchPointsBasedOnVelocityField(velField);
            lens.exportToCSV("Files/march.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
