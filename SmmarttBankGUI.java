import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


class BankAccount {
    private String accountNumber;
    private String holderName;
    private String pin;
    private double balance;
    private ArrayList<String> transactionHistory;

    public BankAccount(String accountNumber, String holderName, String pin) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add("Account created with balance Rs. 0.0");
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public boolean validatePin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "⚠️ Invalid deposit amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        balance += amount;
        transactionHistory.add("Deposited: Rs. " + amount);
        JOptionPane.showMessageDialog(null, "✅ Rs. " + amount + " deposited successfully.");
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "⚠️ Invalid withdrawal amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (amount > balance) {
            JOptionPane.showMessageDialog(null, "⚠️ Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            balance -= amount;
            transactionHistory.add("Withdrew: Rs. " + amount);
            JOptionPane.showMessageDialog(null, "✅ Rs. " + amount + " withdrawn successfully.");
        }
    }

    public void showTransactionHistory() {
        StringBuilder history = new StringBuilder("📜 Transaction History for " + holderName + ":\n");
        for (String record : transactionHistory) {
            history.append(" - ").append(record).append("\n");
        }
        JOptionPane.showMessageDialog(null, history.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }

    public void transfer(BankAccount receiver, double amount) {
        if (amount <= 0 || amount > balance) {
            JOptionPane.showMessageDialog(null, "⚠️ Transfer failed. Check amount and balance.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.balance -= amount;
        receiver.balance += amount;
        this.transactionHistory.add("Transferred Rs. " + amount + " to " + receiver.getHolderName());
        receiver.transactionHistory.add("Received Rs. " + amount + " from " + this.getHolderName());
        JOptionPane.showMessageDialog(null, "✅ Transferred Rs. " + amount + " to " + receiver.getHolderName() + ".");
    }
}

public class SmmarttBankGUI {
    private static ArrayList<BankAccount> accounts = new ArrayList<>();
    private static BankAccount currentUser ;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmmarttBankGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("SmartBank – Console Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login to Account");
        JButton exitButton = new JButton("Exit");

        createAccountButton.addActionListener(e -> createAccount());
        loginButton.addActionListener(e -> login());
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(createAccountButton);
        panel.add(loginButton);
        panel.add(exitButton);
        
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void createAccount() {
        JTextField nameField = new JTextField();
        JTextField pinField = new JTextField();
        Object[] message = {
            "Enter your name:", nameField,
            "Choose a 4-digit PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Create New Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String pin = pinField.getText();
            String accountNumber = "SB" + (1000 + accounts.size());
            BankAccount newAcc = new BankAccount(accountNumber, name, pin);
            accounts.add(newAcc);
            JOptionPane.showMessageDialog(null, "✅ Account created successfully!\nYour Account Number: " + accountNumber);
        }
    }

    private static void login() {
        JTextField accNoField = new JTextField();
        JTextField pinField = new JTextField();
        Object[] message = {
            "Enter Account Number:", accNoField,
            "Enter PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login to Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String accNo = accNoField.getText();
            String pin = pinField.getText();

            for (BankAccount acc : accounts) {
                if (acc.getAccountNumber().equals(accNo) && acc.validatePin(pin)) {
                    currentUser  = acc;
                    userMenu();
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "❌ Invalid account number or PIN.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void userMenu() {
        while (true) {
            String[] options = {"Check Balance", "Deposit Money", "Withdraw Money", "Transfer Funds", "View Transaction History", "Logout"};
            int choice = JOptionPane.showOptionDialog(null, "Account Menu for " + currentUser .getHolderName(),
                    "User  Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0 -> JOptionPane.showMessageDialog(null, "💰 Current Balance: Rs. " + currentUser .getBalance());
                case 1 -> {
                    String amount = JOptionPane.showInputDialog("Enter amount to deposit: Rs.");
                    if (amount != null) {
                        currentUser .deposit(Double.parseDouble(amount));
                    }
                }
                case 2 -> {
                    String amount = JOptionPane.showInputDialog("Enter amount to withdraw: Rs.");
                    if (amount != null) {
                        currentUser .withdraw(Double.parseDouble(amount));
                    }
                }
                case 3 -> {
                    JTextField receiverField = new JTextField();
                    JTextField amountField = new JTextField();
                    Object[] transferMessage = {
                        "Enter recipient's Account Number:", receiverField,
                        "Enter amount to transfer: Rs.", amountField
                    };

                    int transferOption = JOptionPane.showConfirmDialog(null, transferMessage, "Transfer Funds", JOptionPane.OK_CANCEL_OPTION);
                    if (transferOption == JOptionPane.OK_OPTION) {
                        String receiverNo = receiverField.getText();
                        double amount = Double.parseDouble(amountField.getText());
                        BankAccount receiver = null;
                        for (BankAccount acc : accounts) {
                            if (acc.getAccountNumber().equals(receiverNo)) {
                                receiver = acc;
                                break;
                            }
                        }
                        if (receiver == null) {
                            JOptionPane.showMessageDialog(null, "❌ Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            currentUser .transfer(receiver, amount);
                        }
                    }
                }
                case 4 -> currentUser .showTransactionHistory();
                case 5 -> {
                    JOptionPane.showMessageDialog(null, "🔒 Logged out successfully.");
                    currentUser  = null;
                    return;
                }
                default -> JOptionPane.showMessageDialog(null, "⚠️ Invalid option. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
