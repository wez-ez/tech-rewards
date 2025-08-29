package advance_programming_assignment;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class RewardsClient extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public RewardsClient() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            // Step II: Request a connection to the server
            socket = new Socket("localhost", 5000);
            System.out.println("Connection to server is fine.");

            // Initialize input/output streams
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Step III: Ready and send data to server
            String customerId = getValidatedCustomerId();
            double amountSpent = getValidatedAmount();
            out.println(customerId + "," + amountSpent);

            // Step IV: Take the results of points and discount
            String response = in.readLine();
            if (response == null) {
                System.out.println("No server response: error. Please ensure that the server is active and responding to requests.");
                return;
            }

            String[] results = response.split(",");
            if (results.length < 2) {
                System.out.println("Invalid server response format.");
                return;
            }

            int points = Integer.parseInt(results[0]);
            double discount = Double.parseDouble(results[1]);

            // Step V: Display results
            System.out.println("Customer ID: " + customerId);
            System.out.println("Amount Spent: " + amountSpent + " OMR");
            System.out.println("Reward Points: " + points);
            System.out.println("Discount Value: " + discount + " OMR");

            // Step VI: Close connection
            closeConnection();

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error parsing server response: " + e.getMessage());
        }
    }

    private String getValidatedCustomerId() {
        String customerId;
        while (true) {
            System.out.println("Enter Customer Identification number (range: 1-999): ");
            customerId = scanner.nextLine();
            try {
                int id = Integer.parseInt(customerId);
                if (id >= 1 && id <= 999) {
                    return customerId; // Return numeric ID as-is
                } else {
                    System.out.println("Customer ID must be between 1 and 999.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Customer ID. Enter a number between 1 and 999.");
            }
        }
    }

    private double getValidatedAmount() {
        double amount;
        while (true) {
            System.out.println("Please enter the Amount Spent in OMR: ");
            try {
                amount = Double.parseDouble(scanner.nextLine());
                if (amount >= 0) {
                    break;
                } else {
                    System.out.println("Amount must be a non-negative number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid number.");
            }
        }
        return amount;
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Connection to the server has been closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        RewardsClient client = new RewardsClient();
        client.start();
    }
}
