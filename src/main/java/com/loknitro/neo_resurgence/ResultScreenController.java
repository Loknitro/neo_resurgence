package com.loknitro.neo_resurgence;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResultScreenController {
    private List<String> data;
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void getData(List<String> data) {
        this.data = data;
    }

    @FXML
    private Button btnExport;
    @FXML
    private void exportCSV () {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        Stage stage = (Stage)btnExport.getScene().getWindow();
        File file = fc.showSaveDialog(stage);

        if (file != null) {
            String fileName = file.getAbsolutePath();
            writeStringListToCsv(fileName, data);
        } else {
            showAlert(Alert.AlertType.WARNING, "Save Cancelled", null, "File save operation was cancelled.");
        }

    }

    private void writeStringListToCsv(String fileName, List<String> data) {
        try (
                // Use FileOutputStream to write bytes, then OutputStreamWriter to convert chars to bytes with UTF-8
                FileOutputStream fos = new FileOutputStream(fileName);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8); // Specify UTF-8 here
                PrintWriter printWriter = new PrintWriter(osw)
        ) {
            for (String line : data) {
                printWriter.println(line);
            }
            System.out.println("Data successfully written to " + fileName + " with UTF-8 encoding.");

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
