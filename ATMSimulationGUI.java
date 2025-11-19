import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// Interface
interface ATMOperations {
    void deposit(double amount);
    void withdraw(double amount);
    void checkBalance();
}

// Main Class with GUI
public class ATMSimulationGUI extends JFrame {
    
    static java.util.List<Account> accounts = new ArrayList<>();
    static final String FILE_NAME = "accounts.txt";
    static final String ADMIN_FILE = "admin.txt";
    static String adminPassword = "admin123";

    // Account class with Encapsulation and Polymorphism
    static class Account implements ATMOperations {
        private String accountNumber;
        private String name;
        private String pin;
        private double balance;

        public Account(String accountNumber, String name, String pin, double balance) {
            this.accountNumber = accountNumber;
            this.name = name;
            this.pin = pin;
            this.balance = balance;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getName() { return name; }
        public String getPin() { return pin; }
        public double getBalance() { return balance; }
        public void setPin(String newPin) { this.pin = newPin; }

        @Override
        public void deposit(double amount) {
            if (amount > 0) {
                balance += amount;
            }
        }

        @Override
        public void withdraw(double amount) {
            if (amount > 0 && amount <= balance) {
                balance -= amount;
            }
        }

        @Override
        public void checkBalance() {
            JOptionPane.showMessageDialog(null, "Balance: ₹" + balance);
        }

        @Override
        public String toString() {
            return accountNumber + "," + name + "," + pin + "," + balance;
        }
    }

    // Admin class using Inheritance
    static class Admin extends Account {
        public Admin(String acc, String name, String pin, double bal) {
            super(acc, name, pin, bal);
        }

        public void addAccount(Account acc) {
            accounts.add(acc);
        }

        public void deleteAccount(String accNo) {
            accounts.removeIf(acc -> acc.getAccountNumber().equals(accNo));
        }

        public void updateAccount(String accNo, String newPin) {
            for (Account acc : accounts) {
                if (acc.getAccountNumber().equals(accNo)) {
                    acc.setPin(newPin);
                }
            }
        }
    }

    // GUI Logic
    public ATMSimulationGUI() {
        loadAdminPassword();
        loadAccounts();
        showMainMenu();
    }

    void showMainMenu() {
        setTitle("ATM Simulation System");
        setLayout(new GridLayout(4, 1));
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton adminBtn = new JButton("Admin Login");
        JButton userBtn = new JButton("User Login");
        JButton resetBtn = new JButton("Factory Reset");
        JButton exitBtn = new JButton("Exit");

        adminBtn.addActionListener(e -> showAdminLogin());
        userBtn.addActionListener(e -> showUserLogin());
        resetBtn.addActionListener(e -> factoryReset());
        exitBtn.addActionListener(e -> {
            saveAccounts();
            System.exit(0);
        });

        add(adminBtn);
        add(userBtn);
        add(resetBtn);
        add(exitBtn);

        setVisible(true);
    }

    void showAdminLogin() {
        String pass = JOptionPane.showInputDialog("Enter Admin Password:");
        if (pass != null && pass.equals(adminPassword)) {
            showAdminMenu();
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect Password");
        }
    }

    void showAdminMenu() {
        String[] options = {
            "Add Account", "Delete Account", "Update PIN", "View Accounts", "Change Admin Password", "Back"
        };
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "Admin Menu", "Admin",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            switch (choice) {
                case 0: 
                    String accNo = JOptionPane.showInputDialog("Account No:");
                    String name = JOptionPane.showInputDialog("Name:");
                    String pin = JOptionPane.showInputDialog("PIN:");
                    double bal = Double.parseDouble(JOptionPane.showInputDialog("Initial Balance:"));
                    new Admin("0000", "admin", "0000", 0).addAccount(new Account(accNo, name, pin, bal));
                    saveAccounts();
                    JOptionPane.showMessageDialog(null, "Account Added");
                    break;
                case 1:
                    String delAcc = JOptionPane.showInputDialog("Account No to Delete:");
                    new Admin("0000", "admin", "0000", 0).deleteAccount(delAcc);
                    saveAccounts();
                    JOptionPane.showMessageDialog(null, "Account Deleted");
                    break;
                case 2:
                    String upAcc = JOptionPane.showInputDialog("Account No to Update:");
                    String newPin = JOptionPane.showInputDialog("New PIN:");
                    new Admin("0000", "admin", "0000", 0).updateAccount(upAcc, newPin);
                    saveAccounts();
                    JOptionPane.showMessageDialog(null, "PIN Updated");
                    break;
                case 3:
                    StringBuilder sb = new StringBuilder("Accounts List:\n");
                    for (Account acc : accounts) {
                        sb.append("Acc: ").append(acc.getAccountNumber())
                          .append(" | Name: ").append(acc.getName())
                          .append(" | Bal: ₹").append(acc.getBalance()).append("\n");
                    }
                    JOptionPane.showMessageDialog(null, sb.toString());
                    break;
                case 4:
                    String newPass = JOptionPane.showInputDialog("New Admin Password:");
                    adminPassword = newPass;
                    saveAdminPassword();
                    JOptionPane.showMessageDialog(null, "Admin Password Updated");
                    break;
            }
        } while (choice != 5);
    }

    void showUserLogin() {
        String accNo = JOptionPane.showInputDialog("Enter Account No:");
        String pin = JOptionPane.showInputDialog("Enter PIN:");

        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accNo) && acc.getPin().equals(pin)) {
                showUserMenu(acc);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid Login");
    }

    void showUserMenu(Account acc) {
        String[] options = { "Deposit", "Withdraw", "Check Balance", "Change PIN", "Exit" };
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(null, "User Menu", "Welcome " + acc.getName(),
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);
            switch (choice) {
                case 0:
                    double dep = Double.parseDouble(JOptionPane.showInputDialog("Amount to deposit:"));
                    acc.deposit(dep);
                    saveAccounts();
                    break;
                case 1:
                    double wit = Double.parseDouble(JOptionPane.showInputDialog("Amount to withdraw:"));
                    acc.withdraw(wit);
                    saveAccounts();
                    break;
                case 2:
                    acc.checkBalance();
                    break;
                case 3:
                    String newPin = JOptionPane.showInputDialog("New PIN:");
                    acc.setPin(newPin);
                    saveAccounts();
                    JOptionPane.showMessageDialog(null, "PIN Changed");
                    break;
            }
        } while (choice != 4);
    }

    void factoryReset() {
        int confirm = JOptionPane.showConfirmDialog(null, "This will erase all data. Proceed?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            accounts.clear();
            new File(FILE_NAME).delete();
            adminPassword = "admin123";
            saveAdminPassword();
            JOptionPane.showMessageDialog(null, "Factory Reset Completed");
        }
    }

    // File Handling
    static void loadAdminPassword() {
        File f = new File(ADMIN_FILE);
        if (!f.exists()) {
            saveAdminPassword();
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine();
            if (line != null && !line.trim().isEmpty()) {
                adminPassword = line.trim();
            }
        } catch (IOException e) { }
    }

    static void saveAdminPassword() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ADMIN_FILE))) {
            bw.write(adminPassword);
        } catch (IOException e) { }
    }

    static void loadAccounts() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length == 4) {
                    accounts.add(new Account(d[0], d[1], d[2], Double.parseDouble(d[3])));
                }
            }
        } catch (IOException e) { }
    }

    static void saveAccounts() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Account acc : accounts) {
                bw.write(acc.toString());
                bw.newLine();
            }
        } catch (IOException e) { }
    }

    public static void main(String[] args) {
        new ATMSimulationGUI();
    }
}