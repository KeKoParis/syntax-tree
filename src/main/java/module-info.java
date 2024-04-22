module com.example.lab4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires stanford.corenlp;
    requires org.apache.opennlp.tools;

    opens controllers to javafx.fxml;
    exports controllers;
}