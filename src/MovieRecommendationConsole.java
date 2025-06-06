import java.sql.*;
import java.util.Scanner;

public class MovieRecommendationConsole {

    static final String DB_URL = "jdbc:mysql://localhost:3306/moviedb";
    static final String USER = "root";
    static final String PASS = "Rr@8754737944";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMovie Recommendation Console App");
            System.out.println("1. View All Movies");
            System.out.println("2. Search Movies");
            System.out.println("3. Insert New Movie");
            System.out.println("4. Delete Movie");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> fetchAllMovies();
                case 2 -> searchMovies(scanner);
                case 3 -> insertMovie(scanner);
                case 4 -> deleteMovie(scanner);
                case 5 -> {
                    System.out.println("Thank you for using the app!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }

    public static void fetchAllMovies() {
        String query = "SELECT * FROM movies";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nAll Movies:");
            while (rs.next()) {
                System.out.printf("ID: %d, Title: %s, Genre: %s, Language: %s, Director: %s%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("language"),
                        rs.getString("director"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
    }

    public static void searchMovies(Scanner scanner) {
        System.out.print("Enter search term (title/genre/language/director): ");
        String search = scanner.nextLine();

        String query = "SELECT * FROM movies WHERE title LIKE ? OR genre LIKE ? OR language LIKE ? OR director LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String likeSearch = "%" + search + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, likeSearch);
            }

            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            System.out.println("\nSearch Results:");
            while (rs.next()) {
                found = true;
                System.out.printf("ID: %d, Title: %s, Genre: %s, Language: %s, Director: %s%n",
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("language"),
                        rs.getString("director"));
            }

            if (!found) {
                System.out.println("No movies found matching your search.");
            }
        } catch (SQLException e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }

    public static void insertMovie(Scanner scanner) {
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
            } else {
                System.out.println("Failed to insert movie.");
            }
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    public static void deleteMovie(Scanner scanner) {
        System.out.print("Enter movie ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        String deleteQuery = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Movie deleted successfully!");
                reorderMovieIds(conn);  // Call reorder here
            } else {
                System.out.println("No movie found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
    }

    // Reorder IDs after delete
    public static void reorderMovieIds(Connection conn) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
        conn.setAutoCommit(false);

        // Create temp table with same structure but empty
        stmt.execute("CREATE TABLE movies_temp LIKE movies");

        // Remove any data from temp table
        stmt.execute("TRUNCATE TABLE movies_temp");

        // Insert data from old table ordered by id
        stmt.execute("INSERT INTO movies_temp (title, genre, language, director) " +
                     "SELECT title, genre, language, director FROM movies ORDER BY id");

        // Drop old table
        stmt.execute("DROP TABLE movies");

        // Rename temp table to original
        stmt.execute("RENAME TABLE movies_temp TO movies");

        conn.commit();
    } catch (SQLException e) {
        conn.rollback();
        throw e;
    } finally {
        conn.setAutoCommit(true);
    }
}
}
