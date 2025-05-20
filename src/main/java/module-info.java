module com.loknitro.neo_resurgence {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.loknitro.neo_resurgence to javafx.fxml;
    exports com.loknitro.neo_resurgence;
}