import java.util.ArrayList;
import java.util.List;

public class Account {

    private String accountNumber;
    private String name;
    private String phone;
    private String email;

    private double balance;
    private double totalDeposit;
    private double totalWithdraw;
    private double dailyWithdraw;

    private List<Transaction> transactions;


    public Account(
            String accountNumber,
            String name,
            String phone,
            String email,
            double initialDeposit) {

        this.accountNumber = accountNumber;
        this.name = name;
        this.phone = phone;
        this.email = email;

        this.balance = initialDeposit;
        this.totalDeposit = initialDeposit;
        this.totalWithdraw = 0;
        this.dailyWithdraw = 0;

        transactions = new ArrayList<>();

        // Initial deposit transaction
        transactions.add(
                new Transaction(
                        "Account Opening",
                        initialDeposit,
                        balance
                )
        );
    }


    // ================= GETTERS =================

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getBalance() {
        return balance;
    }

    public double getTotalDeposit() {
        return totalDeposit;
    }

    public double getTotalWithdraw() {
        return totalWithdraw;
    }

    public double getDailyWithdraw() {
        return dailyWithdraw;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }


    // ================= DEPOSIT =================

    public void deposit(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero"
            );
        }

        balance = balance + amount;

        totalDeposit = totalDeposit + amount;

        transactions.add(
                new Transaction(
                        "Deposit",
                        amount,
                        balance
                )
        );
    }


    // ================= WITHDRAW =================

    public void withdraw(double amount) {

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero"
            );
        }

        if (amount > balance) {
            throw new IllegalArgumentException(
                    "Insufficient balance"
            );
        }

        if (dailyWithdraw + amount > 20000) {
            throw new IllegalArgumentException(
                    "Daily withdrawal limit of Rs.20,000 exceeded"
            );
        }

        balance = balance - amount;

        totalWithdraw = totalWithdraw + amount;

        dailyWithdraw = dailyWithdraw + amount;

        transactions.add(
                new Transaction(
                        "Withdrawal",
                        amount,
                        balance
                )
        );
    }
}