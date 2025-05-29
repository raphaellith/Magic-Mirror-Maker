import java.io.FileWriter;
import java.io.IOException;

public interface ExportableToCSV {
    String toCSVString();
    default void exportToCSV(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(toCSVString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
