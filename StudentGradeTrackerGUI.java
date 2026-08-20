import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class StudentGradeTrackerGUI extends JFrame {

    // ---------- Color Palette (deep teal + warm gold accents) ----------
    private static final Color BG_DARK      = new Color(24, 33, 39);   // window background
    private static final Color PANEL_DARK   = new Color(31, 43, 51);   // card background
    private static final Color ACCENT_GOLD  = new Color(212, 163, 74); // primary accent
    private static final Color ACCENT_TEAL  = new Color(74, 158, 158); // secondary accent
    private static final Color TEXT_LIGHT   = new Color(230, 232, 230);
    private static final Color TEXT_MUTED   = new Color(150, 162, 168);
    private static final Color ROW_ALT      = new Color(37, 50, 59);
    private static final Color ROW_BASE     = new Color(31, 43, 51);
    private static final Color DANGER       = new Color(196, 90, 90);

    private static final Font FONT_HEADER   = new Font("Georgia", Font.BOLD, 22);
    private static final Font FONT_LABEL    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD     = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_TABLE    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_STAT_VAL = new Font("Georgia", Font.BOLD, 18);
    private static final Font FONT_STAT_LBL = new Font("Segoe UI", Font.PLAIN, 11);

    private ArrayList<Student> students = new ArrayList<>();

    private JTextField nameField;
    private JTextField scoreField;

    private DefaultTableModel tableModel;
    private JTable studentTable;

    private JLabel countValue, avgValue, highValue, lowValue;

    public StudentGradeTrackerGUI() {
        setTitle("Grade Tracker  •  Student Performance Dashboard");
        setSize(760, 560);
        setMinimumSize(new Dimension(680, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_DARK);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        root.add(buildHeader());
        root.add(Box.createVerticalStrut(16));
        root.add(buildInputCard());
        root.add(Box.createVerticalStrut(16));
        root.add(buildTableCard());
        root.add(Box.createVerticalStrut(16));
        root.add(buildStatsRow());

        add(root, BorderLayout.CENTER);
    }

    // ---------------- Header ----------------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel title = new JLabel("Student Grade Tracker");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_LIGHT);

        JLabel subtitle = new JLabel("Record scores, monitor class performance");
        subtitle.setFont(FONT_LABEL);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setBackground(BG_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBlock.add(title);
        titleBlock.add(subtitle);

        header.add(titleBlock, BorderLayout.WEST);

        JPanel underline = new JPanel();
        underline.setBackground(ACCENT_GOLD);
        underline.setPreferredSize(new Dimension(60, 3));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);
        wrapper.add(header, BorderLayout.CENTER);
        JPanel underlineHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        underlineHolder.setBackground(BG_DARK);
        underlineHolder.add(underline);
        wrapper.add(underlineHolder, BorderLayout.SOUTH);

        return wrapper;
    }

    // ---------------- Input Card ----------------
    private RoundedPanel buildInputCard() {
        RoundedPanel card = new RoundedPanel(PANEL_DARK, 14);
        card.setLayout(new GridBagLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLbl = styledLabel("STUDENT NAME");
        gbc.gridx = 0;
        card.add(nameLbl, gbc);

        nameField = styledTextField(14);
        gbc.gridx = 1;
        card.add(nameField, gbc);

        JLabel scoreLbl = styledLabel("SCORE (0-100)");
        gbc.gridx = 2;
        card.add(scoreLbl, gbc);

        scoreField = styledTextField(6);
        gbc.gridx = 3;
        card.add(scoreField, gbc);

        JButton addBtn = pillButton("+ Add", ACCENT_GOLD, BG_DARK);
        addBtn.addActionListener(this::addStudent);
        gbc.gridx = 4;
        card.add(addBtn, gbc);

        JButton removeBtn = pillButton("Remove", DANGER, TEXT_LIGHT);
        removeBtn.addActionListener(this::removeSelectedStudent);
        gbc.gridx = 5;
        card.add(removeBtn, gbc);

        return card;
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JTextField styledTextField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(FONT_LABEL);
        field.setForeground(TEXT_LIGHT);
        field.setBackground(new Color(41, 54, 63));
        field.setCaretColor(ACCENT_GOLD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 76, 86), 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private JButton pillButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ---------------- Table Card ----------------
    private RoundedPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(PANEL_DARK, 14);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel cardTitle = new JLabel("CLASS RECORDS");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cardTitle.setForeground(ACCENT_TEAL);
        cardTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        card.add(cardTitle, BorderLayout.NORTH);

        String[] columns = {"Name", "Score", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_BASE : ROW_ALT);
                } else {
                    c.setBackground(new Color(74, 158, 158, 90));
                }
                c.setForeground(TEXT_LIGHT);
                return c;
            }
        };
        studentTable.setFont(FONT_TABLE);
        studentTable.setRowHeight(30);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setSelectionBackground(new Color(74, 158, 158, 90));
        studentTable.setBackground(ROW_BASE);
        studentTable.setBorder(null);
        studentTable.setFillsViewportHeight(true);

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(41, 54, 63));
        header.setForeground(ACCENT_GOLD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_GOLD));
        header.setPreferredSize(new Dimension(0, 32));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ROW_BASE);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    // ---------------- Stats Row ----------------
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(BG_DARK);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        countValue = new JLabel("0");
        avgValue = new JLabel("-");
        highValue = new JLabel("-");
        lowValue = new JLabel("-");

        row.add(statCard("STUDENTS", countValue, ACCENT_TEAL));
        row.add(statCard("AVERAGE", avgValue, ACCENT_GOLD));
        row.add(statCard("HIGHEST", highValue, new Color(122, 178, 122)));
        row.add(statCard("LOWEST", lowValue, DANGER));

        return row;
    }

    private RoundedPanel statCard(String label, JLabel valueLabel, Color accent) {
        RoundedPanel card = new RoundedPanel(PANEL_DARK, 12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_STAT_LBL);
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(FONT_STAT_VAL);
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);

        return card;
    }

    // ---------------- Logic ----------------
    private void addStudent(ActionEvent e) {
        String name = nameField.getText().trim();
        String scoreText = scoreField.getText().trim();

        if (name.isEmpty() || scoreText.isEmpty()) {
            showToast("Please enter both name and score.");
            return;
        }

        double score;
        try {
            score = Double.parseDouble(scoreText);
            if (score < 0 || score > 100) {
                showToast("Score must be between 0 and 100.");
                return;
            }
        } catch (NumberFormatException ex) {
            showToast("Score must be a valid number.");
            return;
        }

        Student student = new Student(name, score);
        students.add(student);
        tableModel.addRow(new Object[]{student.getName(), student.getScore(), student.getGrade()});

        nameField.setText("");
        scoreField.setText("");
        nameField.requestFocus();

        updateSummary();
    }

    private void removeSelectedStudent(ActionEvent e) {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            showToast("Select a student row first.");
            return;
        }
        students.remove(selectedRow);
        tableModel.removeRow(selectedRow);
        updateSummary();
    }

    private void updateSummary() {
        countValue.setText(String.valueOf(students.size()));

        if (students.isEmpty()) {
            avgValue.setText("-");
            highValue.setText("-");
            lowValue.setText("-");
            return;
        }

        double sum = 0;
        double highest = students.get(0).getScore();
        double lowest = students.get(0).getScore();

        for (Student s : students) {
            sum += s.getScore();
            if (s.getScore() > highest) highest = s.getScore();
            if (s.getScore() < lowest) lowest = s.getScore();
        }

        double average = sum / students.size();

        avgValue.setText(String.format("%.1f", average));
        highValue.setText(String.format("%.1f", highest));
        lowValue.setText(String.format("%.1f", lowest));
    }

    private void showToast(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // ---------------- Rounded Panel Helper ----------------
    static class RoundedPanel extends JPanel {
        private final Color bg;
        private final int radius;

        RoundedPanel(Color bg, int radius) {
            this.bg = bg;
            this.radius = radius;
            setOpaque(false);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            StudentGradeTrackerGUI gui = new StudentGradeTrackerGUI();
            gui.setVisible(true);
        });
    }
}
