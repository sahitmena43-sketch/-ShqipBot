package com.shqipbot;

import java.sql.*;

public class Database {
    private Connection connection;
    
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:shqipbot.db");
            String sql = "CREATE TABLE IF NOT EXISTS perdoruesit (" +
                         "id TEXT PRIMARY KEY, " +
                         "username TEXT, " +
                         "balance INTEGER DEFAULT 100, " +
                         "depozitaFundit INTEGER DEFAULT 0" +
                         ")";
            connection.createStatement().execute(sql);
            System.out.println("✅ Databaza u lidh!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void krijoPerdorues(String id, String username) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO perdoruesit (id, username) VALUES (?, ?)"
            );
            stmt.setString(1, id);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int merrBalance(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT balance FROM perdoruesit WHERE id = ?"
            );
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 100;
    }
    
    public void shtoPara(String id, int shuma) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET balance = balance + ? WHERE id = ?"
            );
            stmt.setInt(1, shuma);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void zbritPara(String id, int shuma) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET balance = MAX(0, balance - ?) WHERE id = ?"
            );
            stmt.setInt(1, shuma);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateDepozita(String id, long timestamp) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET depozitaFundit = ? WHERE id = ?"
            );
            stmt.setLong(1, timestamp);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public long merrDepozitaFundit(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT depozitaFundit FROM perdoruesit WHERE id = ?"
            );
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("depozitaFundit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}