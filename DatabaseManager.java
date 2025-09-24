import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Manages all database operations for the Student Journal application.
 */
public class DatabaseManager {

    // --- Database Connection Details ---
    // IMPORTANT: You MUST change these values to match your own database setup.
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/student_journal_db";
    private static final String USER = "postgres"; // The user you created (likely 'postgres')
    private static final String PASS = "password"; // The password you set for that user

    /**
     * Establishes a connection to the PostgreSQL database.
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    /**
     * Saves a journal entry to the database's 'journal_entries' table.
     * @param content The text content of the journal entry.
     * @return true if the entry was saved successfully, false otherwise.
     */
    public boolean saveEntry(String content) {
        // SQL query to insert a new row. Using '?' prevents SQL injection attacks.
        String sql = "INSERT INTO journal_entries(entry_date, content) VALUES(?, ?)";

        // try-with-resources ensures the connection is automatically closed
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the values for the two placeholders ('?')
            // 1. The first placeholder is the current timestamp
            pstmt.setTimestamp(1, new Timestamp(new Date().getTime()));
            
            // 2. The second placeholder is the journal text
            pstmt.setString(2, content);

            // Execute the query to perform the insert
            pstmt.executeUpdate();
            
            return true; // Return true to indicate success

        } catch (SQLException e) {
            System.err.println("Database Save Error: " + e.getMessage());
            e.printStackTrace(); // Print full error details for debugging
            return false; // Return false to indicate failure
        }
    }
}


