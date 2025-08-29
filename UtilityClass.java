package advance_programming_assignment;
import java.util.HashMap;
import java.util.Scanner;

public class RewardsMenu {
    private static HashMap<String, Double> transactions = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nTech Rewards Menu:");
            System.out.println("1. Add Transaction");
            System.out.println("2. Remove Transaction");
            System.out.println("3. Display Transactions");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    removeTransaction();
                    break;
                case 3:
                    displayTransactions();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addTransaction() {
        System.out.print("Enter Customer ID (e.g., C001): ");
        String customerId = scanner.nextLine();
        if (!customerId.matches("C[0-9]{3}")) {
            System.out.println("Invalid Customer ID. Must be in format CXXX.");
            return;
        }

        System.out.print("Enter Amount Spent (OMR): ");
        try {
            double amount = Double.parseDouble(scanner.nextLine());
            if (amount >= 0) {
                transactions.put(customerId, amount);
                System.out.println("Transaction added for " + customerId);
            } else {
                System.out.println("Amount must be non-negative.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Enter a valid number.");
        }
    }

    private static void removeTransaction() {
        System.out.print("Enter Customer ID to remove: ");
        String customerId = scanner.nextLine();
        if (transactions.remove(customerId) != null) {
            System.out.println("Transaction removed for " + customerId);
        } else {
            System.out.println("No transaction found for " + customerId);
        }
    }

    private static void displayTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            System.out.println("Customer Transactions:");
            for (String customerId : transactions.keySet()) {
                System.out.println("Customer ID: " + customerId + ", Amount: " + transactions.get(customerId) + " OMR");
            }
        }
    }
}
