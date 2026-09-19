import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the user's trading account: cash balance, current
 * stock holdings, and complete transaction history. Also handles
 * saving/loading this data to a local file so progress persists
 * between sessions.
 */
public class Portfolio {

    public static final double STARTING_CASH = 10000.0;
    private static final String SAVE_FILE = "portfolio_data.txt";

    private double cashBalance;
    private final Map<String, Holding> holdings;
    private final List<Transaction> transactionHistory;

    public Portfolio() {
        this.cashBalance = STARTING_CASH;
        this.holdings = new LinkedHashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    /** Attempts to buy shares. Returns false if funds are insufficient or quantity is invalid. */
    public boolean buyStock(Stock stock, int quantity) {
        if (quantity <= 0) return false;
        double cost = stock.getCurrentPrice() * quantity;
        if (cost > cashBalance) return false;

        cashBalance -= cost;
        String symbol = stock.getSymbol();
        if (holdings.containsKey(symbol)) {
            holdings.get(symbol).addShares(quantity, stock.getCurrentPrice());
        } else {
            holdings.put(symbol, new Holding(symbol, quantity, stock.getCurrentPrice()));
        }
        transactionHistory.add(new Transaction(Transaction.Type.BUY, symbol, quantity, stock.getCurrentPrice()));
        return true;
    }

    /** Attempts to sell shares. Returns false if the user doesn't own enough shares. */
    public boolean sellStock(Stock stock, int quantity) {
        String symbol = stock.getSymbol();
        Holding holding = holdings.get(symbol);
        if (quantity <= 0 || holding == null || quantity > holding.getQuantity()) return false;

        double proceeds = stock.getCurrentPrice() * quantity;
        cashBalance += proceeds;
        holding.removeShares(quantity);
        if (holding.getQuantity() == 0) holdings.remove(symbol);

        transactionHistory.add(new Transaction(Transaction.Type.SELL, symbol, quantity, stock.getCurrentPrice()));
        return true;
    }

    public double getCashBalance() { return cashBalance; }
    public Map<String, Holding> getHoldings() { return holdings; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    /** Current market value of all held shares, using live prices from the market. */
    public double getHoldingsValue(StockMarket market) {
        double total = 0;
        for (Holding h : holdings.values()) {
            Stock s = market.getStock(h.getSymbol());
            if (s != null) total += s.getCurrentPrice() * h.getQuantity();
        }
        return total;
    }

    public double getTotalValue(StockMarket market) {
        return cashBalance + getHoldingsValue(market);
    }

    public double getTotalProfitLoss(StockMarket market) {
        return getTotalValue(market) - STARTING_CASH;
    }

    /** Saves cash, holdings and transaction history to a plain text file. */
    public void saveToFile() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("CASH," + cashBalance);
            for (Holding h : holdings.values()) {
                writer.println("HOLDING," + h.getSymbol() + "," + h.getQuantity() + "," + h.getAverageCost());
            }
            for (Transaction t : transactionHistory) {
                writer.println(t.toCSV());
            }
        }
    }

    /** Loads a previously saved portfolio, replacing current in-memory state. */
    public void loadFromFile() throws IOException {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            throw new FileNotFoundException("No saved portfolio found yet.");
        }

        double loadedCash = STARTING_CASH;
        Map<String, Holding> loadedHoldings = new LinkedHashMap<>();
        List<Transaction> loadedHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.split(",");
                switch (p[0]) {
                    case "CASH":
                        loadedCash = Double.parseDouble(p[1]);
                        break;
                    case "HOLDING":
                        loadedHoldings.put(p[1], new Holding(p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3])));
                        break;
                    case "TRANSACTION":
                        Transaction.Type type = Transaction.Type.valueOf(p[1]);
                        String symbol = p[2];
                        int qty = Integer.parseInt(p[3]);
                        double price = Double.parseDouble(p[4]);
                        String time = p[6];
                        loadedHistory.add(new Transaction(type, symbol, qty, price, time));
                        break;
                    default:
                        break;
                }
            }
        }

        this.cashBalance = loadedCash;
        this.holdings.clear();
        this.holdings.putAll(loadedHoldings);
        this.transactionHistory.clear();
        this.transactionHistory.addAll(loadedHistory);
    }
}
