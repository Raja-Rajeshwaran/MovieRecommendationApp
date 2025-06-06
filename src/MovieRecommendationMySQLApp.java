import java.sql.*;
import java.util.Scanner;

public class MovieRecommendationMySQLApp {

    static final String DB_URL = "jdbc:mysql://localhost:3306/MovieDB";
    static final String USER = "root"; // Change if your username is different
    static final String PASS = "Rr@8754737944"; // Change if your password is set

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Movie Recommendation App (MySQL Version)");

        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Filter By Genre");
            System.out.println("2. Filter By Language");
            System.out.println("3. Filter By Director");
            System.out.println("4. Show All Movies");
            System.out.println("5. Insert New Movie");
            System.out.println("6. Delete Movie By ID");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            String filterColumn, filterValue;

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter genre: ");
                    filterColumn = "genre";
                    filterValue = scanner.nextLine();
                    showMovies(filterColumn, filterValue);
                }
                case 2 -> {
                    System.out.print("Enter language: ");
                    filterColumn = "language";
                    filterValue = scanner.nextLine();
                    showMovies(filterColumn, filterValue);
                }
                case 3 -> {
                    System.out.print("Enter director: ");
                    filterColumn = "director";
                    filterValue = scanner.nextLine();
                    showMovies(filterColumn, filterValue);
                }
                case 4 -> showMovies(null, null);
                case 5 -> insertMovie(scanner);
                case 6 -> deleteMovie(scanner);
                case 7 -> {
                    System.out.println("Thank you for using the app! 🎉");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void showMovies(String column, String value) {
        String query;

        if (column == null || value == null) {
            query = "SELECT * FROM movies ORDER BY id";
        } else {
            query = "SELECT * FROM movies WHERE " + column + " = ? ORDER BY id";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (column != null && value != null) {
                stmt.setString(1, value);
            }

            ResultSet rs = stmt.executeQuery();
            boolean found = false;

            System.out.println("\nRecommended Movies:");
            int i = 1;
            while (rs.next()) {
                found = true;
                System.out.println(i + ". ID: " + rs.getInt("id") + " | " + rs.getString("title") + " | Genre: " +
                        rs.getString("genre") + ", Language: " + rs.getString("language") +
                        ", Director: " + rs.getString("director"));
                i++;
            }

            if (!found) {
                System.out.println("No movies found for your preference.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void insertMovie(Scanner scanner) {
        System.out.println("\nInsert a New Movie:");

        System.out.print("Enter movie title: ");
        String title = scanner.nextLine();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();

        System.out.print("Enter language: ");
        String language = scanner.nextLine();

        System.out.print("Enter director: ");
        String director = scanner.nextLine();

        String insertQuery = "INSERT INTO movies (title, genre, language, director) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            pstmt.setString(3, language);
            pstmt.setString(4, director);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Movie inserted successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error inserting movie: " + e.getMessage());
        }
    }

    public static void deleteMovie(Scanner scanner) {
        System.out.print("Enter the ID of the movie to delete: ");
        int movieId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String deleteQuery = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, movieId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Movie deleted successfully. Reordering IDs...");
                reorderMovieIds(conn);
                System.out.println("IDs reordered successfully.");
            } else {
                System.out.println("No movie found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting movie: " + e.getMessage());
        }
    }

    private static void reorderMovieIds(Connection conn) throws SQLException {
        // Disable auto-commit for transaction
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {
            // Create temporary table with same structure
            stmt.execute("CREATE TABLE movies_temp LIKE movies");

            // Copy data without IDs so auto_increment will assign sequential IDs
            stmt.execute("INSERT INTO movies_temp (title, genre, language, director) " +
                    "SELECT title, genre, language, director FROM movies ORDER BY id");

            // Drop old movies table
            stmt.execute("DROP TABLE movies");

            // Rename temp table to movies
            stmt.execute("RENAME TABLE movies_temp TO movies");

            // Commit changes
            conn.commit();
        } catch (SQLException e) {
            // Rollback on error
            conn.rollback();
            throw e;
        } finally {
            // Restore auto-commit
            conn.setAutoCommit(true);
        }
    }
}
