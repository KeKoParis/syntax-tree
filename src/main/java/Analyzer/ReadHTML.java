package Analyzer;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

public class ReadHTML {
    public static String readHTML(String fileName) throws java.io.IOException {
        try {
            File input = new File(fileName);
            Document htmlDocument = Jsoup.parse(input, "utf-8");
            return htmlDocument.text();
        } catch (java.io.IOException e) {
            System.out.println(e);
            throw e;
        }
    }
}
