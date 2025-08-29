package advance_programming_assignment;

import java.io.*;
import java.net.*;
import java.sql.*;

public class RewardsServer extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Connection dbConnection;

    public RewardsServer() {
        try {
            dbConnection = DriverManager.getConnection(
                "jdbc:derby://localhost:1527/ap_assignment", "tech_rewards", "1212");
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started on port 5000");
        } catch (SQLException | IOException e) {
            System.out.println("Server Setup Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket, dbConnection).start();
            } catch (IOException e) {
                System.out.println("Server Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private Connection dbConnection;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, Connection dbConnection) {
            this.socket = socket;
            this.dbConnection = dbConnection;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String input = in.readLine();
                if (input == null) {
                    System.out.println("No input received from client");
                    out.println("0,0");
                    return;
                }
                String[] data = input.split(",");
                if (data.length != 2) {
                    System.out.println("Invalid input format: " + input);
                    out.println("0,0");
                    return;
                }
                String customerId = data[0];
                double amountSpent;
                try {
                    amountSpent = Double.parseDouble(data[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount format: " + data[1]);
                    out.println("0,0");
                    return;
                }

                String query = "SELECT point_rate, baisa_per_point FROM rewards_config WHERE customer_id = ?";
                try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                    try {
                        stmt.setInt(1, Integer.parseInt(customerId));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid customer_id format: " + customerId);
                        out.println("0,0");
                        return;
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int pointRate;
                            double baisaPerPoint;
                            try {
                                pointRate = rs.getInt("point_rate");
                                baisaPerPoint = rs.getDouble("baisa_per_point");
                                if (rs.wasNull()) {
                                    System.out.println("Null value found for point_rate or baisa_per_point for customer_id: " + customerId);
                                    out.println("0,0");
                                    return;
                                }
                            } catch (SQLException e) {
                                System.out.println("Error reading database values for customer_id: " + customerId + ", Error: " + e.getMessage());
                                out.println("0,0");
                                return;
                            }
                            int points = (int) (amountSpent * pointRate);
                            double discount = (points * baisaPerPoint) / 1000;
                            // Step vii: Send results to client
                            out.println(points + "," + discount);
                            System.out.println("Processed for " + customerId + ": Points=" + points + ", Discount=" + discount);
                        } else {
                            System.out.println("No configuration found for customer_id: " + customerId);
                            out.println("0,0");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Database error for customer_id: " + customerId + ", Error: " + e.getMessage());
                    e.printStackTrace();
                    out.println("0,0");
                }
            } catch (IOException e) {
                System.out.println("Client Handler IO Error: " + e.getMessage());
                e.printStackTrace();
                out.println("0,0");
            } finally {
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client connection: " + e.getMessage());
                }
            }
        }
    }

    public void closeConnection() {
        try {
            if (dbConnection != null) dbConnection.close();
            if (serverSocket != null) serverSocket.close();
            System.out.println("Server connections closed.");
        } catch (SQLException | IOException e) {
            System.out.println("Error closing server connections: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RewardsServer server = new RewardsServer();
        server.start();
    }
}
