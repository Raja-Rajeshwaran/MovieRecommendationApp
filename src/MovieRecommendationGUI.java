import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MovieRecommendationGUI extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviedb";
    private static final String USER = "root";
    private static final String PASS = "Rr@8754737944";

    private final JTextField titleField, genreField, languageField, directorField, searchField;
    private final DefaultTableModel tableModel;
    private final JTable movieTable;

    public MovieRecommendationGUI() {
        setTitle("Movie Recommendation GUI App");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Input panel for adding movies
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add New Movie"));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        inputPanel.add(genreField);

        inputPanel.add(new JLabel("Language:"));
        languageField = new JTextField();
        inputPanel.add(languageField);

        inputPanel.add(new JLabel("Director:"));
        directorField = new JTextField();
        inputPanel.add(directorField);

        JButton insertBtn = new JButton("Insert Movie");
        inputPanel.add(insertBtn);

        // Delete button
        JButton deleteBtn = new JButton("Delete Selected Movie");
        inputPanel.add(deleteBtn);

        add(inputPanel, BorderLayout.NORTH);

        // Table for movies
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Genre", "Language", "Director"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movieTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(movieTable);
        add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchBtn);

        JButton fetchAllBtn = new JButton("Fetch All");
        searchPanel.add(fetchAllBtn);

        add(searchPanel, BorderLayout.SOUTH);

        // Event handlers
        insertBtn.addActionListener(e -> insertMovie());
        deleteBtn.addActionListener(e -> deleteSelectedMovie());
        searchBtn.addActionListener(e -> searchMovies());
        fetchAllBtn.addActionListener(e -> fetchAllMovies());

        // Load all movies initially
        fetchAllMovies();
    }

    private void fetchAllMovies() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM movies";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("language"),
                        rs.getString("director")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching movies: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchMovies() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search term.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        String query = "SELECT * FROM movies WHERE title LIKE ? OR genre LIKE ? OR language LIKE ? OR director LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String likeSearch = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, likeSearch);
            }

            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("language"),
                        rs.getString("director")
                });
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No movies found matching your search.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertMovie() {
        String title = titleField.getText().trim();
        String genre = genreField.getText().trim();
        String language = languageField.getText().trim();
        String director = directorField.getText().trim();

        if (title.isEmpty() || genre.isEmpty() || language.isEmpty() || director.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String insertQuery = "INSERT INTO movies (title, genre, language, director) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, title);
            pstmt.setString(2, genre);
            pstmt.setString(3, language);
            pstmt.setString(4, director);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Movie inserted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                fetchAllMovies();
                titleField.setText("");
                genreField.setText("");
                languageField.setText("");
                directorField.setText("");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Insert error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a movie to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int movieId = (int) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected movie?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String deleteQuery = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, movieId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                reorderMovieIds(conn);  // Reorder IDs after deletion
                JOptionPane.showMessageDialog(this, "Movie deleted and IDs reordered successfully!",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                fetchAllMovies();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reorderMovieIds(Connection conn) throws SQLException {
        conn.setAutoCommit(false);

        try (Statement stmt = conn.createStatement()) {
            // Create temporary table like movies
            stmt.execute("CREATE TABLE movies_temp LIKE movies");

            // Copy data without IDs so auto_increment will assign sequential IDs
            stmt.execute("INSERT INTO movies_temp (title, genre, language, director) " +
                    "SELECT title, genre, language, director FROM movies ORDER BY id");

            // Drop old movies table
            stmt.execute("DROP TABLE movies");

            // Rename temp table to movies
            stmt.execute("RENAME TABLE movies_temp TO movies");

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MovieRecommendationGUI gui = new MovieRecommendationGUI();
            gui.setVisible(true);
        });
    }
}
