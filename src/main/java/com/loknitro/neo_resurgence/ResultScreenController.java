package com.loknitro.neo_resurgence;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ResultScreenController {
    @FXML
    private AnchorPane resultPane;
    @FXML
    private Button btnExport;

    private static final PauseTransition buttonTimer = new PauseTransition(Duration.seconds(3));


    String person;
    private List<String> data;
    public void setData(List<String> data) {
        this.data = data;
    }
    @FXML
    public void initialize() {
        buttonTimer.setOnFinished(event -> btnExport.setVisible(true));
        resultPane.setOnMousePressed(event -> {
            if (btnExport.isVisible()) {
                return;
            }
            buttonTimer.playFromStart();
        });
        resultPane.setOnMouseReleased(event -> {
            if (btnExport.isVisible()) {
                return;
            }
            buttonTimer.stop();
        });
    }

    @FXML
    private void exportCSV () {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Definir o número da Pessoa");
        dialog.setHeaderText("Digite somente o número para associar à pessoa que fez o teste!");
        dialog.initOwner(resultPane.getScene().getWindow());
        Optional<String> personNumber = dialog.showAndWait();

        personNumber.ifPresent(e -> person = e);


        FileChooser fc = new FileChooser();
        fc.setTitle("Exportar Xlxs");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Xlsx Files", "*.xlsx"));
        Stage stage = (Stage)btnExport.getScene().getWindow();
        File file = fc.showSaveDialog(stage);



        if (file != null) {
            String fileName = file.getAbsolutePath();
            writeStringListToXlsx(fileName, data);
        } else {
            showAlert();
        }

    }
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText("Exportação de arquivo cancelada.");
        alert.showAndWait();
    }
    private void writeStringListToXlsx(String fileName, List<String> data) {
        String[] headers = {"FASE", "OBJETO", "TIPO DE INTERAÇÃO", "TEMPO(minutos)", "LATÊNCIA(segundos)", "RESULTADO", "S1", "S2"};
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.createSheet("Relatório de Ações");


            CellStyle mainHeaderStyle = workbook.createCellStyle();
            mainHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            mainHeaderStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 200, (byte) 230, (byte) 255}, null)); // Azul claro (RGB)
            mainHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font mainHeaderFont = workbook.createFont();
            mainHeaderFont.setBold(true);
            mainHeaderFont.setFontHeightInPoints((short) 12);
            mainHeaderStyle.setFont(mainHeaderFont);

            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            String headerText = "PUC GOIÁS/LAEC " + currentDate + "\n - Experimento de Ressurgência\n - Pessoa " + person;

            Row mainHeaderRow = sheet.createRow(0);
            Cell mainHeaderCell = mainHeaderRow.createCell(0);
            mainHeaderCell.setCellValue(headerText);
            mainHeaderCell.setCellStyle(mainHeaderStyle);

            sheet.addMergedRegion(new CellRangeAddress(
                    0,
                    0,
                    0,
                    headers.length - 1
            ));

            CellStyle columnHeaderStyle = workbook.createCellStyle();
            Font columnHeaderFont = workbook.createFont();
            columnHeaderFont.setBold(true);
            columnHeaderStyle.setFont(columnHeaderFont);

            Row columnHeaderRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = columnHeaderRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(columnHeaderStyle);
            }

            int rowNum = 2;
            for (String dataString : data) {
                String[] content = dataString.split(";");
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < content.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(content[i]);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
