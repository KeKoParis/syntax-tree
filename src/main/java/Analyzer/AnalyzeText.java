package Analyzer;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.util.*;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class AnalyzeText {

    public static String analyze(String fileName) throws IOException {
        long a = System.currentTimeMillis();
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
            return "";
        }

        // Create the parser
        Parser parser = ParserFactory.create(parserModel);

        // Create the tokenizer
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);

        // Create the POS tagger
        POSTaggerME posTagger = new POSTaggerME(posModel);

        // Parse the text
        String text = ReadHTML.readHTML(fileName);
        String[] tokens = tokenizer.tokenize(text);
        String[] tags = posTagger.tag(tokens);
        opennlp.tools.parser.Parse[] topParses = ParserTool.parseLine(text, parser, 1);

        StringBuilder stringBuilder = new StringBuilder();
        // Print the syntactic tree
        for (opennlp.tools.parser.Parse p : topParses) {
            stringBuilder.append("Relations");
            syntax_analysis(stringBuilder);
            stringBuilder.append("Syntactic Tree:\n");
            stringBuilder.append(p.toString() + "\n");
            stringBuilder.append("Constituents:\n");
            printConstituents(p, 0, stringBuilder);
            stringBuilder.append("Tokens:\n");
            stringBuilder.append(printTokens(tokens, tags, text));

        }
        System.out.printf("time: %d\n", System.currentTimeMillis() - a);
        return stringBuilder.toString();
    }

    private static StringBuilder printConstituents(opennlp.tools.parser.Parse p, int indent, StringBuilder stringBuilder) {
        StringBuilder sb = stringBuilder;
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        opennlp.tools.util.Span span = p.getSpan();
        sb.append(p.getType()).append(" (").append(span.getStart()).append(", ").append(span.getEnd()).append(")").append("\n");
        System.out.println(sb);
        for (opennlp.tools.parser.Parse child : p.getChildren()) {
            sb = printConstituents(child, indent + 1, sb);
        }
        return sb;
    }

    private static StringBuilder printTokens(String[] tokens, String[] tags, String text) {
        int start = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            int end = start + tokens[i].length();
            String gender = getGender(tags[i]);
            String tense = getTense(tags[i]);
            stringBuilder.append(tokens[i] + " (" + start + ", " + end + ") - " + tags[i] + " - Gender: " + gender + ", Tense: " + tense + "\n");
            System.out.println(tokens[i] + " (" + start + ", " + end + ") - " + tags[i] + " - Gender: " + gender + ", Tense: " + tense);
            start = end + 1;
        }
        return stringBuilder;
    }

    public static void syntax_analysis(StringBuilder stringBuilder) {
        // Input text
        String inputText = "London is the capital of the UK. Paris is the capital of France.";

        // Set up Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, depparse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Process the input text
        Annotation document = new Annotation(inputText);
        pipeline.annotate(document);

        // Get the syntactic dependencies
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);

            // Iterate over the dependencies
            for (SemanticGraphEdge edge : dependencies.edgeIterable()) {
                IndexedWord gov = edge.getGovernor();
                IndexedWord dep = edge.getDependent();

                // Get the entities and the dependency type
                String subject = gov.word();
                String predicate = edge.getRelation().getShortName();
                String object = dep.word();

                // Print the semantic relationship
                stringBuilder.append("Subject: " + subject + "\n");
                stringBuilder.append("Predicate: " + predicate+ "\n");
                stringBuilder.append("Object: " + object+ "\n\n");
            }
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