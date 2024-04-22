package Analyzer;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;

public class AnalyzeText {
    public static void analyze() {
        ParserModel parserModel;
        TokenizerModel tokenizerModel;
        POSModel posModel;
        try (FileInputStream parserModelIn = new FileInputStream("en-parser-chunking.bin");
             FileInputStream tokenizerModelIn = new FileInputStream("en-token.bin");
             FileInputStream posModelIn = new FileInputStream("en-pos-maxent.bin")) {
            parserModel = new ParserModel(parserModelIn);
            tokenizerModel = new TokenizerModel(tokenizerModelIn);
            posModel = new POSModel(posModelIn);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Create the parser
        Parser parser = ParserFactory.create(parserModel);

        // Create the tokenizer
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);

        // Create the POS tagger
        POSTaggerME posTagger = new POSTaggerME(posModel);

        // Parse the text
        String text = "The quick brown Paul jumps over the lazy dog.";
        String[] tokens = tokenizer.tokenize(text);
        String[] tags = posTagger.tag(tokens);
        opennlp.tools.parser.Parse[] topParses = ParserTool.parseLine(text, parser, 1);

        // Print the syntactic tree
        for (opennlp.tools.parser.Parse p : topParses) {
            System.out.println("Syntactic Tree:");
            System.out.println(p.toString());
            System.out.println("Constituents:");
            printConstituents(p, 0);
            System.out.println("Tokens:");
            printTokens(tokens, tags, text);
        }
    }

    private static void printConstituents(opennlp.tools.parser.Parse p, int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        opennlp.tools.util.Span span = p.getSpan();
        sb.append(p.getType()).append(" (").append(span.getStart()).append(", ").append(span.getEnd()).append(")");
        System.out.println(sb.toString());
        for (opennlp.tools.parser.Parse child : p.getChildren()) {
            printConstituents(child, indent + 1);
        }
    }

    private static void printTokens(String[] tokens, String[] tags, String text) {
        int start = 0;
        for (int i = 0; i < tokens.length; i++) {
            int end = start + tokens[i].length();
            String gender = getGender(tags[i]);
            String tense = getTense(tags[i]);
            System.out.println(tokens[i] + " (" + start + ", " + end + ") - " + tags[i] + " - Gender: " + gender + ", Tense: " + tense);
            start = end + 1;
        }
    }

    private static String getGender(String tag) {
        switch (tag) {
            case "NN":
            case "NNS":
                return "Neuter";
            case "NNP":
            case "NNPS":
                return "Proper";
            case "PRP":
            case "PRP$":
                return "Personal";
            default:
                return "Unknown";
        }
    }

    private static String getTense(String tag) {
        switch (tag) {
            case "VB":
                return "Infinitive";
            case "VBD":
                return "Past";
            case "VBG":
                return "Gerund";
            case "VBN":
                return "Past Participle";
            case "VBP":
                return "Present";
            case "VBZ":
                return "Present 3rd Person Singular";
            default:
                return "Unknown";
        }
    }
}