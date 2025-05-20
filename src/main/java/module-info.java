module com.loknitro.neo_resurgence {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.media;

    opens com.loknitro.neo_resurgence to javafx.fxml;
    exports com.loknitro.neo_resurgence;
}