import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Holds everything the chatbot "knows": a set of built-in trained
 * intents (frequently asked questions) plus any custom question/answer
 * pairs the user has taught it. Custom entries are saved to a local
 * file so the bot keeps learning across sessions.
 */
public class KnowledgeBase {

    private static final String CUSTOM_FILE = "custom_knowledge.txt";

    private final List<Intent> intents = new ArrayList<>();

    public KnowledgeBase() {
        loadDefaultIntents();
        loadCustomIntents();
    }

    private void addIntent(String tag, List<String> patterns, List<String> responses) {
        intents.add(new Intent(tag, patterns, responses));
    }

    /** The bot's built-in training data: frequently asked questions and their answers. */
    private void loadDefaultIntents() {
        addIntent("greeting",
                Arrays.asList("hello", "hi", "hey", "good morning", "good evening", "good afternoon", "hi there", "hello there"),
                Arrays.asList("Hello! How can I help you today?", "Hi there! What can I do for you?", "Hey! Ask me anything."));

        addIntent("farewell",
                Arrays.asList("bye", "goodbye", "see you", "see you later", "talk to you later", "exit", "quit"),
                Arrays.asList("Goodbye! Have a great day.", "See you later! Feel free to come back anytime.", "Bye! Take care."));

        addIntent("thanks",
                Arrays.asList("thanks", "thank you", "appreciate it", "thanks a lot", "thank you so much"),
                Arrays.asList("You're welcome!", "Anytime! Happy to help.", "No problem at all."));

        addIntent("bot_name",
                Arrays.asList("what is your name", "who are you", "your name", "what should i call you"),
                Arrays.asList("I'm CodeBot, a chatbot built for the CodeAlpha internship task.", "You can call me CodeBot!"));

        addIntent("bot_purpose",
                Arrays.asList("what can you do", "what do you do", "your purpose", "how can you help", "help me"),
                Arrays.asList("I can chat with you and answer frequently asked questions. Try asking about CodeAlpha, my creator, or just say hello!",
                        "I'm here to answer FAQs and have a simple conversation with you."));

        addIntent("creator",
                Arrays.asList("who made you", "who created you", "who built you", "who is your developer", "who is your creator"),
                Arrays.asList("I was built as part of the CodeAlpha Java Programming Internship, Task 3.", "An intern at CodeAlpha built me for this project!"));

        addIntent("mood",
                Arrays.asList("how are you", "how are you doing", "how do you feel", "are you okay"),
                Arrays.asList("I'm just a program, but I'm running great! How about you?", "Doing well, thanks for asking!"));

        addIntent("age",
                Arrays.asList("how old are you", "your age", "when were you created", "when were you made"),
                Collections.singletonList("I was just created for this internship task, so I'm brand new!"));

        addIntent("joke",
                Arrays.asList("tell me a joke", "make me laugh", "say something funny", "know any jokes"),
                Arrays.asList("Why do programmers prefer dark mode? Because light attracts bugs!",
                        "Why did the Java developer wear glasses? Because they couldn't C#!",
                        "I would tell you a UDP joke, but you might not get it."));

        addIntent("codealpha_info",
                Arrays.asList("what is codealpha", "tell me about codealpha", "codealpha internship", "about codealpha"),
                Arrays.asList("CodeAlpha is an organization that offers virtual internships in fields like Java programming, web development, and AI.",
                        "CodeAlpha runs internship programs where interns complete real projects to gain hands-on experience."));

        addIntent("time",
                Arrays.asList("what time is it", "current time", "tell me the time"),
                Collections.singletonList("I don't have a live clock connected, but your system clock will have the exact time!"));

        addIntent("date",
                Arrays.asList("what is the date", "today's date", "what day is it"),
                Collections.singletonList("I can't check a live calendar, but your device can tell you today's date!"));

        addIntent("chatbot_tech",
                Arrays.asList("how do you work", "how were you built", "are you ai", "do you use machine learning", "are you a real ai"),
                Collections.singletonList("I use simple NLP techniques \u2014 tokenizing your message and matching keywords against trained intents to pick the best response."));

        addIntent("compliment",
                Arrays.asList("you are smart", "good job", "well done", "you are awesome", "nice bot", "you are helpful"),
                Arrays.asList("Thank you, that means a lot!", "I appreciate that!"));
    }

    /** Loads any question/answer pairs the user previously taught the bot. */
    private void loadCustomIntents() {
        File file = new File(CUSTOM_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length == 2) {
                    count++;
                    addIntent("custom_" + count,
                            Collections.singletonList(parts[0]),
                            Collections.singletonList(parts[1]));
                }
            }
        } catch (IOException ignored) {
            // if the file can't be read, the bot simply continues with its default training
        }
    }

    /** Teaches the bot a new question/answer pair, in memory and saved to disk for next time. */
    public void teach(String question, String answer) throws IOException {
        addIntent("custom_" + System.currentTimeMillis(),
                Collections.singletonList(question),
                Collections.singletonList(answer));

        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOM_FILE, true))) {
            writer.println(question + "|||" + answer);
        }
    }

    public List<Intent> getIntents() { return intents; }
}
