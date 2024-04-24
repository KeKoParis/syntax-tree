package controllers;

import Analyzer.AnalyzeText;
import Analyzer.ReadHTML;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Controller {
    @FXML
    public TextArea helpText;
    @FXML
    public TextField textField;
    @FXML
    public TextArea textArea;
    @FXML
    private Button analyzeButton;

    @FXML
    public void initialize(){
        try {
            try {
                File input = new File("help.txt");
                Scanner scanner = new Scanner(input);
                StringBuilder stringBuilder=new StringBuilder();
                while(scanner.hasNextLine()){
                    stringBuilder.append(scanner.nextLine() + "\n");
                }
                helpText.appendText(stringBuilder.toString());
            } catch (java.io.IOException e) {
                System.out.println(e);
                throw e;
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }

    @FXML
    public void onAnalyzeButtonClick(ActionEvent event) {
        textArea.clear();
        try {
            String text = AnalyzeText.analyze(textField.getText());
            this.textArea(text);
        } catch (IOException e) {
            System.out.println(e);
        }


    }

    public void textArea(String text) {
        textArea.appendText(text);
    }

    public void onSaveButtonClick(ActionEvent event) {
        String text = textArea.getText();
        String fileName = text.substring(16, 25) + ".txt";
        try {

            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(text);
            fileWriter.flush();


        } catch (IOException e) {
            System.out.println(e);
        }
    }


}
