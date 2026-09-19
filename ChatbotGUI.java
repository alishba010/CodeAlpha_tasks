import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

/**
 * CodeAlpha Java Programming Internship - Task 3
 * Artificial Intelligence Chatbot ("CodeBot")
 *
 * A Java Swing chat application that uses lightweight NLP (tokenizing,
 * stop-word removal, keyword-overlap scoring) to match user messages
 * against a trained set of intents and reply with the best answer.
 * When the bot doesn't recognize a question, the user can teach it
 * the correct answer, which is saved to disk so the bot keeps learning.
 */
public class ChatbotGUI extends JFrame {

    // ---- Theme colors (kept consistent with the other CodeAlpha tasks) ----
    private static final Color BG_DARK = new Color(24, 26, 32);
    private static final Color PANEL_DARK = new Color(33, 36, 45);
    private static final Color CARD_DARK = new Color(40, 43, 54);
    private static final Color BORDER_COLOR = new Color(58, 62, 75);
    private static final Color TEXT_LIGHT = new Color(230, 230, 235);
    private static final Color TEXT_MUTED = new Color(150, 155, 165);
    private static final Color ACCENT_BLUE = new Color(88, 143, 255);
    private static final Color BOT_BUBBLE = new Color(50, 54, 66);
    private static final Color USER_BUBBLE = new Color(74, 125, 235);

    private final KnowledgeBase knowledgeBase = new KnowledgeBase();
    private final ChatEngine engine = new ChatEngine(knowledgeBase);

    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private JLabel typingLabel;

    public ChatbotGUI() {
        super("CodeAlpha - AI Chatbot (CodeBot)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 760);
        setMinimumSize(new Dimension(420, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        initHeader();
        initChatArea();
        initInputBar();

        addBotMessage("Hi! I'm CodeBot \uD83E\uDD16. Ask me something - try \"who are you\" or \"tell me a joke\".");
    }

    // ---------------------------------------------------------------- HEADER
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_DARK);
        header.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("CodeBot");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT_LIGHT);

        JLabel subtitle = new JLabel("CodeAlpha Java Programming Internship \u2022 Task 3");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JButton clearBtn = new JButton("Clear Chat");
        clearBtn.setFocusPainted(false);
        clearBtn.setBackground(CARD_DARK);
        clearBtn.setForeground(TEXT_LIGHT);
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint();
            addBotMessage("Chat cleared. How can I help you now?");
        });

        header.add(titleBox, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    // ---------------------------------------------------------------- CHAT AREA
    private void initChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BG_DARK);
        chatPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.getViewport().setBackground(BG_DARK);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(chatScrollPane, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------- INPUT BAR
    private void initInputBar() {
        JPanel bottom = new JPanel(new BorderLayout(8, 4));
        bottom.setBackground(PANEL_DARK);
        bottom.setBorder(new EmptyBorder(10, 14, 14, 14));

        typingLabel = new JLabel(" ");
        typingLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        typingLabel.setForeground(TEXT_MUTED);
        typingLabel.setBorder(new EmptyBorder(0, 4, 4, 0));

        inputField = new JTextField();
        inputField.setBackground(CARD_DARK);
        inputField.setForeground(TEXT_LIGHT);
        inputField.setCaretColor(TEXT_LIGHT);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)));
        inputField.addActionListener(e -> sendMessage());

        sendButton = new JButton("Send");
        sendButton.setFocusPainted(false);
        sendButton.setBackground(ACCENT_BLUE);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        sendButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());

        JPanel fieldRow = new JPanel(new BorderLayout(8, 0));
        fieldRow.setOpaque(false);
        fieldRow.add(inputField, BorderLayout.CENTER);
        fieldRow.add(sendButton, BorderLayout.EAST);

        bottom.add(typingLabel, BorderLayout.NORTH);
        bottom.add(fieldRow, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------- CHAT LOGIC
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        addUserMessage(text);
        inputField.setText("");
        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        typingLabel.setText("CodeBot is typing...");

        Timer timer = new Timer(650, e -> {
            String reply = engine.respond(text);
            addBotMessage(reply);
            typingLabel.setText(" ");
            sendButton.setEnabled(true);
            inputField.setEnabled(true);
            inputField.requestFocusInWindow();

            if (engine.getLastUnmatchedInput() != null) {
                offerToLearn(engine.getLastUnmatchedInput());
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /** When the bot doesn't understand a message, offers to be taught the correct answer. */
    private void offerToLearn(String question) {
        int choice = JOptionPane.showConfirmDialog(this,
                "I don't know how to answer:\n\"" + question + "\"\n\nWould you like to teach me the correct response?",
                "Teach CodeBot", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            String answer = JOptionPane.showInputDialog(this,
                    "What should I say when someone asks:\n\"" + question + "\"?",
                    "Teach CodeBot", JOptionPane.PLAIN_MESSAGE);

            if (answer != null && !answer.isBlank()) {
                try {
                    knowledgeBase.teach(question, answer.trim());
                    addBotMessage("Got it! I'll remember that for next time.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Sorry, I couldn't save that: " + ex.getMessage(),
                            "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // ---------------------------------------------------------------- MESSAGE RENDERING
    private void addUserMessage(String text) {
        addBubble(new ChatMessage(ChatMessage.Sender.USER, text));
    }

    private void addBotMessage(String text) {
        addBubble(new ChatMessage(ChatMessage.Sender.BOT, text));
    }

    private void addBubble(ChatMessage message) {
        boolean isUser = message.getSender() == ChatMessage.Sender.USER;

        JPanel row = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        BubblePanel bubble = new BubblePanel(isUser ? USER_BUBBLE : BOT_BUBBLE);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(10, 14, 8, 14));

        JLabel textLabel = new JLabel(wrapHtml(message.getText()));
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel timeLabel = new JLabel(message.getTime());
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(255, 255, 255, 160));
        timeLabel.setAlignmentX(isUser ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        timeLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        bubble.add(textLabel);
        bubble.add(timeLabel);

        row.add(bubble);
        chatPanel.add(row);
        chatPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = chatScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private String wrapHtml(String text) {
        String escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><body style='width: 260px'>" + escaped + "</body></html>";
    }

    /** A simple rounded-rectangle panel used to draw chat bubbles. */
    private static class BubblePanel extends JPanel {
        private final Color bubbleColor;

        BubblePanel(Color bubbleColor) {
            this.bubbleColor = bubbleColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bubbleColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatbotGUI().setVisible(true));
    }
}
