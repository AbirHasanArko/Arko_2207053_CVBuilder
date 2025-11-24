package com.cvbuilder.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles all database operations for the CV Builder application.
 * Provides CRUD operations for CV data using SQLite.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:cvbuilder.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    /**
     * Get singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Initialize database and create tables if they don't exist
     */
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create all necessary tables
     */
    private void createTables() throws SQLException {
        String cvTable = """
            CREATE TABLE IF NOT EXISTS cv_data (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL,
                phone TEXT,
                address TEXT,
                education TEXT,
                skills TEXT,
                work_experience TEXT,
                projects TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(cvTable);
            System.out.println("Tables created successfully.");
        }
    }

    /**
     * Add a new CV to the database
     */
    public int addCV(CVData cvData) {
        String sql = """
            INSERT INTO cv_data (name, email, phone, address, education, skills, work_experience, projects)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cvData.getName());
            pstmt.setString(2, cvData.getEmail());
            pstmt.setString(3, cvData.getPhone());
            pstmt.setString(4, cvData.getAddress());
            pstmt.setString(5, cvData.getEducation());
            pstmt.setString(6, cvData.getSkills());
            pstmt.setString(7, cvData.getWorkExperience());
            pstmt.setString(8, cvData.getProjects());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Use SQLite's last_insert_rowid() function to get the generated ID
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        System.out.println("CV added successfully with ID: " + id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding CV: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get a CV by ID
     */
    public CVData getCVById(int id) {
        String sql = "SELECT * FROM cv_data WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCVDataFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving CV: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all CVs from the database
     */
    public List<CVData> getAllCVs() {
        List<CVData> cvList = new ArrayList<>();
        String sql = "SELECT * FROM cv_data ORDER BY updated_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cvList.add(extractCVDataFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving CVs: " + e.getMessage());
            e.printStackTrace();
        }
        return cvList;
    }

    /**
     * Update an existing CV
     */
    public boolean updateCV(CVData cvData) {
        String sql = """
            UPDATE cv_data
            SET name = ?, email = ?, phone = ?, address = ?, education = ?,
                skills = ?, work_experience = ?, projects = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, cvData.getName());
            pstmt.setString(2, cvData.getEmail());
            pstmt.setString(3, cvData.getPhone());
            pstmt.setString(4, cvData.getAddress());
            pstmt.setString(5, cvData.getEducation());
            pstmt.setString(6, cvData.getSkills());
            pstmt.setString(7, cvData.getWorkExperience());
            pstmt.setString(8, cvData.getProjects());
            pstmt.setInt(9, cvData.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("CV updated successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating CV: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a CV by ID
     */
    public boolean deleteCV(int id) {
        String sql = "DELETE FROM cv_data WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("CV deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting CV: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search CVs by name
     */
    public List<CVData> searchCVsByName(String name) {
        List<CVData> cvList = new ArrayList<>();
        String sql = "SELECT * FROM cv_data WHERE name LIKE ? ORDER BY updated_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cvList.add(extractCVDataFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching CVs: " + e.getMessage());
            e.printStackTrace();
        }
        return cvList;
    }

    /**
     * Get total count of CVs in database
     */
    public int getTotalCVCount() {
        String sql = "SELECT COUNT(*) as count FROM cv_data";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting CV count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Extract CVData object from ResultSet
     */
    private CVData extractCVDataFromResultSet(ResultSet rs) throws SQLException {
        CVData cvData = new CVData();
        cvData.setId(rs.getInt("id"));
        cvData.setName(rs.getString("name"));
        cvData.setEmail(rs.getString("email"));
        cvData.setPhone(rs.getString("phone"));
        cvData.setAddress(rs.getString("address"));
        cvData.setEducation(rs.getString("education"));
        cvData.setSkills(rs.getString("skills"));
        cvData.setWorkExperience(rs.getString("work_experience"));
        cvData.setProjects(rs.getString("projects"));
        cvData.setCreatedAt(rs.getTimestamp("created_at"));
        cvData.setUpdatedAt(rs.getTimestamp("updated_at"));
        return cvData;
    }

    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the active connection (for testing purposes)
     */
    public Connection getConnection() {
        return connection;
    }
}