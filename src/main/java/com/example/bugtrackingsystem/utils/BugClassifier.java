package com.example.bugtrackingsystem.utils;
import org.apache.commons.text.similarity.CosineSimilarity;
import java.util.Map;
import java.util.*;


public class BugClassifier {
    // Define categories and associated keywords
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();

    static {
        CATEGORY_KEYWORDS.put("UI Issue", Arrays.asList("layout", "button", "dropdown", "CSS", "alignment"));
        CATEGORY_KEYWORDS.put("Performance Bug", Arrays.asList("slow", "lag", "freeze", "delay", "high CPU", "high memory", "crash" , "crashes"));
        CATEGORY_KEYWORDS.put("Security Bug", Arrays.asList("vulnerability", "SQL injection", "unauthorized", "breach", "password"));
        CATEGORY_KEYWORDS.put("Database Error", Arrays.asList("query", "MySQL", "PostgreSQL", "data loss", "null value", "constraint error"));
        CATEGORY_KEYWORDS.put("Feature Request", Arrays.asList("suggestion", "new feature", "enhancement", "upgrade"));
    }

    public static String classifyBug(String title, String description) {
        String text = (title + " " + description).toLowerCase();

        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    return entry.getKey();  // Return the first matched category
                }
            }
        }
        return "Other"; // Default category if query dont match any of the above
    }


    private final CosineSimilarity cosineSimilarity = new CosineSimilarity();

    public double computeSimilarity(Map<CharSequence, Integer> vector1, Map<CharSequence, Integer> vector2) {
        return cosineSimilarity.cosineSimilarity(vector1, vector2);
    }

    public Map<CharSequence, Integer> textToVector(String text) {
        Map<CharSequence, Integer> vector = new HashMap<>();
        for (String word : text.toLowerCase().split("\\s+")) {
            vector.put(word, vector.getOrDefault(word, 0) + 1);
        }
        return vector;
    }
}
