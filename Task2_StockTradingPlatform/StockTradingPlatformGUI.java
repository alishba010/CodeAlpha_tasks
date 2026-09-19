import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * CodeAlpha Java Programming Internship - Task 2
 * Stock Trading Platform
 *
 * A simulated stock trading desktop app built with Java Swing.
 * Features:
 *  - Live-looking market data that ticks every few seconds
 *  - Buy / Sell shares with real balance validation
 *  - Portfolio tracking with average cost, market value and P/L
 *  - Full transaction history log
 *  - Save / Load portfolio to a local file (simple file-based persistence)
 */
public class StockTradingPlatformGUI extends JFrame {

    // ---- Theme colors ----
    private static final Color BG_DARK = new Color(24, 26, 32);
    private static final Color PANEL_DARK = new Color(33, 36, 45);
    private static final Color CARD_DARK = new Color(40, 43, 54);
    private static final Color BORDER_COLOR = new Color(58, 62, 75);
    private static final Color TEXT_LIGHT = new Color(230, 230, 235);
    private static final Color TEXT_MUTED = new Color(150, 155, 165);
    private static final Color ACCENT_BLUE = new Color(88, 143, 255);
    private static final Color GREEN = new Color(76, 201, 130);
    private static final Color RED = new Color(240, 92, 92);

    private final StockMarket market = new StockMarket();
    private final Portfolio portfolio = new Portfolio();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

    private DefaultTableModel marketModel;
    private DefaultTableModel holdingsModel;
    private DefaultTableModel historyModel;

    private JTable marketTable;
    private JComboBox<String> symbolCombo;
    private JSpinner quantitySpinner;
    private JLabel statusLabel;

    private JLabel cashValueLabel;
    private JLabel holdingsValueLabel;
    private JLabel totalValueLabel;
    private JLabel plValueLabel;

    private Timer marketTimer;

    public StockTradingPlatformGUI() {
        super("CodeAlpha - Stock Trading Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(12, 12));

        initHeader();
        initCenter();
        initFooterHistory();

        refreshMarketTable();
        refreshHoldingsTable();
        refreshSummary();
        startMarketSimulation();
    }

    // ---------------------------------------------------------------- HEADER
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARK);
        header.setBorder(new EmptyBorder(16, 20, 8, 20));

        JLabel title = new JLabel("Stock Trading Platform");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT_LIGHT);

        JLabel subtitle = new JLabel("CodeAlpha Java Programming Internship  \u2022  Task 2");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(BG_DARK);
        titleBox.add(title);
        titleBox.add(subtitle);

        JButton saveBtn = darkButton("Save Portfolio", ACCENT_BLUE);
        JButton loadBtn = darkButton("Load Portfolio", CARD_DARK);
        saveBtn.addActionListener(e -> saveAction());
        loadBtn.addActionListener(e -> loadAction());

        JPanel btnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBox.setBackground(BG_DARK);
        btnBox.add(loadBtn);
        btnBox.add(saveBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnBox, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    // ---------------------------------------------------------------- CENTER
    private void initCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(0, 20, 0, 20));

        center.add(buildMarketPanel(), BorderLayout.CENTER);
        center.add(buildRightPanel(), BorderLayout.EAST);

        add(center, BorderLayout.CENTER);
    }

    private JPanel buildMarketPanel() {
        JPanel panel = cardPanel("Live Market Data");

        String[] cols = {"Symbol", "Company", "Price", "Change", "Change %"};
        marketModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        marketTable = buildDarkTable(marketModel);
        marketTable.getColumnModel().getColumn(3).setCellRenderer(new SignedColorRenderer());
        marketTable.getColumnModel().getColumn(4).setCellRenderer(new SignedColorRenderer());

        JScrollPane scroll = new JScrollPane(marketTable);
        scroll.getViewport().setBackground(CARD_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(BG_DARK);
        right.setPreferredSize(new Dimension(360, 0));

        right.add(buildTradePanel());
        right.add(Box.createRigidArea(new Dimension(0, 12)));
        right.add(buildSummaryPanel());
        right.add(Box.createRigidArea(new Dimension(0, 12)));
        right.add(buildHoldingsPanel());

        return right;
    }

    private JPanel buildTradePanel() {
        JPanel panel = cardPanel("Trade");
        panel.setMaximumSize(new Dimension(360, 190));
        panel.setPreferredSize(new Dimension(360, 190));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_DARK);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 4, 6, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;

        symbolCombo = new JComboBox<>();
        for (Stock s : market.getAllStocks()) symbolCombo.addItem(s.getSymbol());
        styleCombo(symbolCombo);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
        styleSpinner(quantitySpinner);

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.4;
        form.add(mutedLabel("Symbol"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        form.add(symbolCombo, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.4;
        form.add(mutedLabel("Quantity"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        form.add(quantitySpinner, gc);

        JButton buyBtn = darkButton("BUY", GREEN);
        JButton sellBtn = darkButton("SELL", RED);
        buyBtn.addActionListener(e -> tradeAction(true));
        sellBtn.addActionListener(e -> tradeAction(false));

        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setBackground(CARD_DARK);
        btnRow.add(buyBtn);
        btnRow.add(sellBtn);

        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2;
        form.add(btnRow, gc);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);
        gc.gridy = 3;
        form.add(statusLabel, gc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = cardPanel("Portfolio Summary");
        panel.setMaximumSize(new Dimension(360, 175));
        panel.setPreferredSize(new Dimension(360, 175));

        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 10));
        grid.setBackground(CARD_DARK);

        cashValueLabel = valueLabel();
        holdingsValueLabel = valueLabel();
        totalValueLabel = valueLabel();
        plValueLabel = valueLabel();

        grid.add(mutedLabel("Cash Balance"));
        grid.add(cashValueLabel);
        grid.add(mutedLabel("Holdings Value"));
        grid.add(holdingsValueLabel);
        grid.add(mutedLabel("Total Value"));
        grid.add(totalValueLabel);
        grid.add(mutedLabel("Total Profit / Loss"));
        grid.add(plValueLabel);

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildHoldingsPanel() {
        JPanel panel = cardPanel("My Holdings");

        String[] cols = {"Symbol", "Qty", "Avg Cost", "Mkt Value", "P/L"};
        holdingsModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable holdingsTable = buildDarkTable(holdingsModel);
        holdingsTable.getColumnModel().getColumn(4).setCellRenderer(new SignedColorRenderer());

        JScrollPane scroll = new JScrollPane(holdingsTable);
        scroll.getViewport().setBackground(CARD_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------------- FOOTER
    private void initFooterHistory() {
        JPanel panel = cardPanel("Transaction History");
        panel.setBorder(new EmptyBorder(10, 20, 16, 20));
        panel.setPreferredSize(new Dimension(0, 190));

        String[] cols = {"Type", "Symbol", "Qty", "Price/Share", "Total", "Date & Time"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = buildDarkTable(historyModel);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(new SignedColorRenderer());

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.getViewport().setBackground(CARD_DARK);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        add(panel, BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------- ACTIONS
    private void tradeAction(boolean isBuy) {
        String symbol = (String) symbolCombo.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();
        Stock stock = market.getStock(symbol);
        if (stock == null) return;

        boolean success = isBuy ? portfolio.buyStock(stock, quantity) : portfolio.sellStock(stock, quantity);

        if (success) {
            String action = isBuy ? "Bought" : "Sold";
            setStatus(action + " " + quantity + " share(s) of " + symbol
                    + " @ " + currency.format(stock.getCurrentPrice()), GREEN);
        } else {
            String reason = isBuy ? "Insufficient cash balance." : "You don't own enough shares to sell.";
            setStatus("Trade failed: " + reason, RED);
        }

        refreshHoldingsTable();
        refreshHistoryTable();
        refreshSummary();
    }

    private void saveAction() {
        try {
            portfolio.saveToFile();
            setStatus("Portfolio saved successfully.", GREEN);
            JOptionPane.showMessageDialog(this, "Portfolio saved to portfolio_data.txt",
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save portfolio:\n" + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAction() {
        try {
            portfolio.loadFromFile();
            refreshHoldingsTable();
            refreshHistoryTable();
            refreshSummary();
            setStatus("Portfolio loaded successfully.", GREEN);
            JOptionPane.showMessageDialog(this, "Portfolio loaded from portfolio_data.txt",
                    "Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Load Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startMarketSimulation() {
        marketTimer = new Timer(3000, e -> {
            market.updateAllPrices();
            refreshMarketTable();
            refreshHoldingsTable();
            refreshSummary();
        });
        marketTimer.start();
    }

    // ---------------------------------------------------------------- REFRESH
    private void refreshMarketTable() {
        int selectedRow = marketTable.getSelectedRow();
        marketModel.setRowCount(0);
        for (Stock s : market.getAllStocks()) {
            marketModel.addRow(new Object[]{
                    s.getSymbol(),
                    s.getCompanyName(),
                    currency.format(s.getCurrentPrice()),
                    formatSigned(s.getChangeAmount()),
                    formatSigned(s.getChangePercent()) + "%"
            });
        }
        if (selectedRow >= 0 && selectedRow < marketModel.getRowCount()) {
            marketTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void refreshHoldingsTable() {
        holdingsModel.setRowCount(0);
        for (Holding h : portfolio.getHoldings().values()) {
            Stock s = market.getStock(h.getSymbol());
            double currentPrice = (s != null) ? s.getCurrentPrice() : h.getAverageCost();
            double marketValue = currentPrice * h.getQuantity();
            double pl = (currentPrice - h.getAverageCost()) * h.getQuantity();

            holdingsModel.addRow(new Object[]{
                    h.getSymbol(),
                    h.getQuantity(),
                    currency.format(h.getAverageCost()),
                    currency.format(marketValue),
                    formatSigned(pl)
            });
        }
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        // newest first
        var history = portfolio.getTransactionHistory();
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction t = history.get(i);
            historyModel.addRow(new Object[]{
                    t.getType(),
                    t.getSymbol(),
                    t.getQuantity(),
                    currency.format(t.getPricePerShare()),
                    currency.format(t.getTotal()),
                    t.getFormattedTime()
            });
        }
    }

    private void refreshSummary() {
        double cash = portfolio.getCashBalance();
        double holdingsVal = portfolio.getHoldingsValue(market);
        double total = portfolio.getTotalValue(market);
        double pl = portfolio.getTotalProfitLoss(market);

        cashValueLabel.setText(currency.format(cash));
        holdingsValueLabel.setText(currency.format(holdingsVal));
        totalValueLabel.setText(currency.format(total));
        plValueLabel.setText(formatSigned(pl));
        plValueLabel.setForeground(pl >= 0 ? GREEN : RED);
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private String formatSigned(double value) {
        String sign = value >= 0 ? "+" : "";
        return sign + String.format("%.2f", value);
    }

    // ---------------------------------------------------------------- UI HELPERS
    private JPanel cardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(CARD_DARK);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_LIGHT);
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("-");
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private JButton darkButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(PANEL_DARK);
        combo.setForeground(TEXT_LIGHT);
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.getEditor().getComponent(0).setBackground(PANEL_DARK);
        spinner.getEditor().getComponent(0).setForeground(TEXT_LIGHT);
    }

    private JTable buildDarkTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setBackground(CARD_DARK);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionBackground(ACCENT_BLUE.darker());
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        table.getTableHeader().setBackground(PANEL_DARK);
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        return table;
    }

    /** Colors numeric text green if it starts with '+' or is a BUY, red otherwise. */
    private class SignedColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            String text = String.valueOf(value);
            if (!isSelected) {
                if (text.startsWith("+") || text.equalsIgnoreCase("BUY")) {
                    c.setForeground(GREEN);
                } else if (text.startsWith("-") || text.equalsIgnoreCase("SELL")) {
                    c.setForeground(RED);
                } else {
                    c.setForeground(TEXT_LIGHT);
                }
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatformGUI().setVisible(true));
    }
}
