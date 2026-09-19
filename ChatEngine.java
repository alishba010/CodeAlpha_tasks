import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The chatbot's response engine. Takes raw user input, runs it through
 * NLP preprocessing, scores it against every trained intent in the
 * knowledge base, and returns the best matching response - or a
 * fallback if nothing matches confidently enough.
 */
public class ChatEngine {

    private static final double CONFIDENCE_THRESHOLD = 0.4;

    private final KnowledgeBase knowledgeBase;
    private final Random random = new Random();
    private String lastUnmatchedInput = null;

    public ChatEngine(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    /** Non-null right after a reply where the bot didn't understand the user's message. */
    public String getLastUnmatchedInput() {
        return lastUnmatchedInput;
    }

    public String respond(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            return "Please type something so I can help you.";
        }

        Set<String> inputTokens = NLPProcessor.tokenSet(userInput);
        Intent bestIntent = null;
        double bestScore = 0.0;

        for (Intent intent : knowledgeBase.getIntents()) {
            double score = intent.bestScore(inputTokens);
            if (score > bestScore) {
                bestScore = score;
                bestIntent = intent;
            }
        }

        if (bestIntent != null && bestScore >= CONFIDENCE_THRESHOLD) {
            lastUnmatchedInput = null;
            List<String> responses = bestIntent.getResponses();
            return responses.get(random.nextInt(responses.size()));
        }

        lastUnmatchedInput = userInput;
        return "I'm not sure I understand that yet. Would you like to teach me the answer?";
    }
}
