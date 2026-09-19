import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lightweight NLP preprocessing utility used by the chatbot.
 * Performs the classic first steps of a text-understanding pipeline:
 * normalization, tokenization and stop-word removal, so that only
 * the meaningful keywords of a sentence are used for intent matching.
 */
public class NLPProcessor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "are", "am", "was", "were", "be", "been", "being",
            "i", "you", "he", "she", "it", "we", "they", "me", "my", "your", "his", "her",
            "its", "our", "their", "to", "of", "in", "on", "at", "for", "with", "about",
            "do", "does", "did", "can", "could", "will", "would", "should", "what", "how",
            "when", "where", "why", "who", "which", "please", "tell", "and", "or", "but",
            "so", "this", "that", "these", "those", "have", "has", "had"
    ));

    private NLPProcessor() { }

    /** Lowercases, strips punctuation, and splits text into raw word tokens. */
    public static List<String> rawTokenize(String text) {
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9' ]", " ");
        List<String> tokens = new ArrayList<>();
        for (String t : cleaned.split("\\s+")) {
            if (!t.isBlank()) tokens.add(t.trim());
        }
        return tokens;
    }

    /** Tokenizes text and removes common stop words, leaving only meaningful keywords. */
    public static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokenize(text)) {
            if (!STOP_WORDS.contains(t)) {
                tokens.add(t);
            }
        }
        return tokens;
    }

    /** Convenience method returning the keyword tokens as a Set, for overlap comparisons. */
    public static Set<String> tokenSet(String text) {
        return new HashSet<>(tokenize(text));
    }
}
