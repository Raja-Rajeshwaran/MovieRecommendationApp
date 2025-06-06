import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import models.User;
import repository.ThreatRepository;
import repository.UserRepository;
import service.ThreatService;
import service.UserService;

public class SecuraNetApplication {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/securanet";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Rr@8754737944"; //  Change this to your MySQL password!

    @SuppressWarnings({"CallToPrintStackTrace", "ConvertToTryWithResources"})
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            UserRepository userRepository = new UserRepository(connection);
            ThreatRepository threatRepository = new ThreatRepository(connection);

            UserService userService = new UserService(userRepository);
            ThreatService threatService = new ThreatService(threatRepository);

            Scanner scanner = new Scanner(System.in);
            User loggedInUser = null;

            OUTER:
            while (true) {
                if (loggedInUser == null) {
                    System.out.println("\n=== SecuraNet - Cyber Threat Management ===");
                    System.out.println("1. Register");
                    System.out.println("2. Login");
                    System.out.println("0. Exit");
                    System.out.print("Choose option: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1 ->                             {
                                System.out.print("Username: ");
                                String username = scanner.nextLine();
                                System.out.print("Password: ");
                                String password = scanner.nextLine();
                                System.out.print("Role (ADMIN/USER): ");
                                String role = scanner.nextLine().toUpperCase();
                                boolean registered = userService.register(username, password, role);
                                if (registered) {
                                    System.out.println("User registered successfully!");
                                } else {
                                    System.out.println("Registration failed.");
                                }                                  }
                        case 2 ->                             {
                                System.out.print("Username: ");
                                String username = scanner.nextLine();
                                System.out.print("Password: ");
                                String password = scanner.nextLine();
                                loggedInUser = userService.login(username, password);
                                if (loggedInUser != null) {
                                    System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());
                                } else {
                                    System.out.println("Invalid credentials.");
                                }                                  }
                        case 0 -> {
                            System.out.println("Goodbye!");
                            break OUTER;
                        }
                        default -> {
                        }
                    }
                } else {
                    System.out.println("\n=== SecuraNet - Dashboard (" + loggedInUser.getRole() + ") ===");
                    System.out.println("1. View Threats");
                    System.out.println("2. Add Threat");
                    System.out.println("3. Delete Threat");
                    if (loggedInUser.getRole().equals("ADMIN")) {
                        System.out.println("4. View Users");
                    }
                    System.out.println("5. Logout");
                    System.out.print("Choose option: ");
                    int option = scanner.nextInt();
                    scanner.nextLine();

                    if (option == 1) {
                        threatService.getAllThreats().forEach(threat -> {
                            System.out.println("ID: " + threat.getId());
                            System.out.println("Title: " + threat.getTitle());
                            System.out.println("Description: " + threat.getDescription());
                            System.out.println("Severity: " + threat.getSeverity());
                            System.out.println("----------------------");
                        });

                    } else if (option == 2) {
                        System.out.print("Threat Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Threat Description: ");
                        String description = scanner.nextLine();
                        System.out.print("Threat Severity (LOW/MEDIUM/HIGH): ");
                        String severity = scanner.nextLine().toUpperCase();

                        boolean added = threatService.addThreat(title, description, severity);
                        if (added) {
                            System.out.println("Threat added successfully!");
                        } else {
                            System.out.println("Failed to add threat.");
                        }

                    } else if (option == 3) {
                        System.out.print("Enter Threat ID to delete: ");
                        int threatId = scanner.nextInt();
                        scanner.nextLine();

                        boolean deleted = threatService.deleteThreat(threatId);
                        if (deleted) {
                            System.out.println("Threat deleted successfully.");
                        } else {
                            System.out.println("Threat deletion failed.");
                        }

                    } else if (option == 4 && loggedInUser.getRole().equals("ADMIN")) {
                        userService.getAllUsers().forEach(user -> {
                            System.out.println("ID: " + user.getId() + ", Username: " + user.getUsername() + ", Role: " + user.getRole());
                        });

                    } else if (option == 5) {
                        loggedInUser = null;
                        System.out.println("Logged out successfully.");
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
            }
            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
