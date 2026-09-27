import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class BankingService {

    private Map<String, Account> accounts;

    public BankingService() {

        accounts = new HashMap<>();

    }


    // ================= CREATE ACCOUNT =================

    public Account createAccount(
            String name,
            String phone,
            String email,
            double initialDeposit) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name is required"
            );
        }

        if (initialDeposit < 0) {
            throw new IllegalArgumentException(
                    "Initial deposit cannot be negative"
            );
        }


        String accountNumber;

        do {

            accountNumber =
                    "SB" +
                    String.format(
                            "%08d",
                            (int)(Math.random() * 100000000)
                    );

        } while (accounts.containsKey(accountNumber));


        Account account =
                new Account(
                        accountNumber,
                        name,
                        phone,
                        email,
                        initialDeposit
                );


        accounts.put(
                accountNumber,
                account
        );


        return account;
    }


    // ================= FIND ACCOUNT =================

    public Account getAccount(
            String accountNumber) {

        Account account =
                accounts.get(accountNumber);


        if (account == null) {

            throw new IllegalArgumentException(
                    "Account not found"
            );

        }


        return account;
    }


    // ================= DEPOSIT =================

    public Account deposit(
            String accountNumber,
            double amount) {

        Account account =
                getAccount(accountNumber);

        account.deposit(amount);

        return account;
    }


    // ================= WITHDRAW =================

    public Account withdraw(
            String accountNumber,
            double amount) {

        Account account =
                getAccount(accountNumber);

        account.withdraw(amount);

        return account;
    }


    // ================= BALANCE =================

    public double checkBalance(
            String accountNumber) {

        Account account =
                getAccount(accountNumber);

        return account.getBalance();
    }


    // ================= PASSBOOK =================

    public List<Transaction> getPassbook(
            String accountNumber) {

        Account account =
                getAccount(accountNumber);

        return account.getTransactions();
    }
}