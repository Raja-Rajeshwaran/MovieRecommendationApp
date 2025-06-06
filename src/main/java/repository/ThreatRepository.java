package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Threat;

public class ThreatRepository {

    private Connection connection;

    public ThreatRepository(Connection connection) {
        this.connection = connection;
    }

    // Add a new threat
    public boolean addThreat(Threat threat) {
        String sql = "INSERT INTO threats (title, description, severity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, threat.getTitle());
            stmt.setString(2, threat.getDescription());
            stmt.setString(3, threat.getSeverity());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fetch all threats
    public List<Threat> getAllThreats() {
        List<Threat> threats = new ArrayList<>();
        String sql = "SELECT * FROM threats";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                threats.add(new Threat(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("severity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return threats;
    }

    // Delete threat by ID
    public boolean deleteThreat(int id) {
        String sql = "DELETE FROM threats WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
