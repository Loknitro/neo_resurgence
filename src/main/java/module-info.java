module com.loknitro.neo_resurgence {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires javafx.base;

    opens com.loknitro.neo_resurgence to javafx.fxml;
    exports com.loknitro.neo_resurgence;
}