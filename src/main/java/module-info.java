module com.example.lab4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;
    requires stanford.corenlp;
    requires org.apache.opennlp.tools;
    requires org.joda.time;
    requires json.simple;

    opens controllers to javafx.fxml;
    exports controllers;
}