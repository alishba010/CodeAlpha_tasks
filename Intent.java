import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents one "intent" the chatbot has been trained to recognize
 * (for example: greeting, farewell, asking the bot's name). Each
 * intent has several example phrases (patterns) and one or more
 * possible responses.
 */
public class Intent {

    private final String tag;
    private final List<Set<String>> patternKeywords;
    private final List<String> responses;

    public Intent(String tag, List<String> patterns, List<String> responses) {
        this.tag = tag;
        this.responses = responses;
        this.patternKeywords = new ArrayList<>();
        for (String pattern : patterns) {
            patternKeywords.add(new HashSet<>(NLPProcessor.tokenize(pattern)));
        }
    }

    public String getTag() { return tag; }
    public List<String> getResponses() { return responses; }

    /**
     * Compares the given input keywords against every trained pattern for this
     * intent and returns the best match score, from 0.0 (no overlap) to 1.0
     * (the input covers the whole pattern).
     */
    public double bestScore(Set<String> inputTokens) {
        double best = 0.0;
        for (Set<String> pattern : patternKeywords) {
            if (pattern.isEmpty()) continue;
            long overlap = pattern.stream().filter(inputTokens::contains).count();
            double score = (double) overlap / pattern.size();
            if (score > best) best = score;
        }
        return best;
    }
}
