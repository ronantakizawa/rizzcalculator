package model;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import view.ChatView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class ChatModel {
    private String csvFile;
    private String cpuName;
    private String preference; // CPU preference
    private int score;  // Score variable
    private Timer timer;  // Timer for adding score every 10 seconds
    private int messageCount = 0;  // Track how many responses the CPU has made
    private StanfordCoreNLP pipeline; // NLP Pipeline

    public ChatModel(String cpuName, String preference) {
        this.cpuName = sanitizeFileName(cpuName);  // Sanitize CPU name for file
        this.preference = preference;
        this.score = 0;  // Initialize score to 0
        this.timer = new Timer(true);  // Timer runs as a daemon thread

        // Create the "Chat" directory if it doesn't exist
        File dir = new File("Chat");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // Set the CSV file path to "Chat/CPUName.csv"
        this.csvFile = "Chat/" + this.cpuName + ".csv";  // Use sanitized name

        // Create the CSV file if it doesn't exist
        File file = new File(csvFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logError("Error creating chat file: " + e.getMessage());
            }
        }

        // Initialize Stanford NLP Pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,sentiment");
        this.pipeline = new StanfordCoreNLP(props);

        // Start the timer to add 1 to score every 10 seconds
        startScoreTimer();
    }

    // Save the chat in the CSV file and update score
    public void saveMessage(String sender, String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            score++;  // Increment score for every message saved
            String formattedMessage = sender + "," + message + "," + timestamp + "," + score + "\n";
            writer.write(formattedMessage);

            // Perform sentiment analysis on user messages
            if ("User".equals(sender)) {
                String sentiment = analyzeSentiment(message);
                System.out.println("Sentiment analysis: " + sentiment);
            }

        } catch (IOException e) {
            logError("Error saving message: " + e.getMessage());
        }
    }

    // Display the chat history from the CSV file
    public void displayChatHistory(ChatView view) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {  // Check for score column
                    String sender = parts[0];
                    String message = parts[1];
                    // Display the message in the view
                    view.displayMessage(sender, message);
                    // Load the score from the file
                    score = Integer.parseInt(parts[3]);  // Set the score from the file
                }
            }
        } catch (IOException e) {
            logError("No previous chat history found: " + e.getMessage());
        }
    }

    // Method to return a CPU response based on message count
    public String getCPUResponse() {
        String response;

        if (messageCount == 0) {
            // First response should be "Hey!"
            response = "How's it going?";
        } else if (messageCount <= 2) {
            // Next 3 responses should be basic and generic
            response = getGenericResponse();
        } else {
            // After that, respond with texts that reveal the CPU's preference
            response = getPreferenceResponse();
        }

        messageCount++;  // Increment the response count
        return response;
    }

    // Generate basic generic responses
    private String getGenericResponse() {
        List<String> basicResponses = new ArrayList<>();
        basicResponses.add("Interesting. What else is new?");
        basicResponses.add("Great. You know I love your outfit today!");
        basicResponses.add("Thats sick! btw Awsome weather today huh?");
        basicResponses.add("Cool! btw what sort of stuff are you into?");
        basicResponses.add("ok...");

        Random rand = new Random();
        return basicResponses.get(rand.nextInt(basicResponses.size()));
    }

    // Generate responses revealing the CPU's preference, some without mentioning it
    private String getPreferenceResponse() {
        List<String> preferenceResponses = new ArrayList<>();
        
        // Responses that include the CPU's preference
        preferenceResponses.add("Awesome. You know, I could really go for some " + preference + " right now.");
        preferenceResponses.add("Talking to you makes me think of " + preference + ".");
        preferenceResponses.add("You know something about you reminds me of " + preference + ".");
        
        // Responses that do not include the CPU's preference
        preferenceResponses.add("lol");
        preferenceResponses.add("nahhhh");
        preferenceResponses.add("I think ur lying");
        preferenceResponses.add("bruhhh");
        preferenceResponses.add("jk lol");

        Random rand = new Random();
        return preferenceResponses.get(rand.nextInt(preferenceResponses.size()));
    }

    public String getCpuName() {
        return cpuName;
    }

    public int getScore() {
        return score;  // Getter for score
    }

    // Start a timer to increase score by 1 every 10 seconds
    private void startScoreTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                score++;
            }
        }, 10000, 10000);  // Start after 10 seconds, repeat every 10 seconds
    }

    // Perform sentiment analysis using Stanford CoreNLP
    // Perform sentiment analysis using Stanford CoreNLP and adjust score
private String analyzeSentiment(String text) {
    System.out.println(text);
    Annotation annotation = new Annotation(text);
    pipeline.annotate(annotation);
    String sentiment = "Neutral";

    for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
        Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
        int sentimentClass = RNNCoreAnnotations.getPredictedClass(tree);

        // Map sentiment class to a human-readable string and adjust score
       System.out.println(sentimentClass);
        switch (sentimentClass) {
            case 0:
                sentiment = "Very Negative";
                score -= 2;  // Subtract more for very negative sentiment
                break;
            case 1:
                sentiment = "Negative";
                score -= 1;  // Subtract for negative sentiment
                break;
            case 2:
                sentiment = "Neutral";
                score -= 1;  // Subtract for neutral sentiment as well
                break;
            case 3:
                sentiment = "Positive";
                score += 1;  // Add for positive sentiment
                break;
            case 4:
                sentiment = "Very Positive";
                score += 2;  // Add more for very positive sentiment
                break;
            default:
                sentiment = "Unknown";
                break;
        }
    }
    return sentiment;
}


    // Utility to sanitize file names (remove special characters)
    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    // Utility to log errors (replace with a logging framework in a real app)
    private void logError(String message) {
        System.err.println(message);
    }
}
