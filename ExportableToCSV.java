import java.io.*;

public interface ExportableToCSV {  // For classes that can be exported as a CSV file
    String toCSVString();

    default void exportToCSV(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(toCSVString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
