package com.shqipbot;

import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection;
    
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:shqipbot.db");
            String sql = "CREATE TABLE IF NOT EXISTS perdoruesit (" +
                         "id TEXT PRIMARY KEY, " +
                         "username TEXT, " +
                         "balance INTEGER DEFAULT 1000, " +
                         "bank INTEGER DEFAULT 0, " +
                         "depozitaFundit INTEGER DEFAULT 0, " +
                         "weekly_earnings INTEGER DEFAULT 0, " +
                         "monthly_earnings INTEGER DEFAULT 0" +
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
    
    public User merrPerdorues(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM perdoruesit WHERE id = ?"
            );
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("balance"),
                    rs.getInt("bank"),
                    rs.getLong("depozitaFundit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        krijoPerdorues(id, null);
        return new User(id, null, 1000, 0, 0);
    }
    
    public User merrPerdoruesNgaUsername(String username) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM perdoruesit WHERE username = ?"
            );
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("balance"),
                    rs.getInt("bank"),
                    rs.getLong("depozitaFundit")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
        return 1000;
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
    
    public int merrBank(String id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT bank FROM perdoruesit WHERE id = ?"
            );
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("bank");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public void shtoBank(String id, int shuma) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET bank = bank + ? WHERE id = ?"
            );
            stmt.setInt(1, shuma);
            stmt.setString(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void zbritBank(String id, int shuma) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET bank = MAX(0, bank - ?) WHERE id = ?"
            );
            stmt.setInt(1, shuma);
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
    
    public List<User> getTopBalances() {
        List<User> topUsers = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM perdoruesit ORDER BY balance DESC LIMIT 10"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                topUsers.add(new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("balance"),
                    rs.getInt("bank"),
                    rs.getLong("depozitaFundit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topUsers;
    }
    
    public List<User> getWeeklyTop() {
        List<User> topUsers = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM perdoruesit ORDER BY weekly_earnings DESC LIMIT 10"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                topUsers.add(new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("balance"),
                    rs.getInt("bank"),
                    rs.getLong("depozitaFundit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topUsers;
    }
    
    public List<User> getMonthlyTop() {
        List<User> topUsers = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM perdoruesit ORDER BY monthly_earnings DESC LIMIT 10"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                topUsers.add(new User(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("balance"),
                    rs.getInt("bank"),
                    rs.getLong("depozitaFundit")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topUsers;
    }
    
    public void updateWeeklyEarnings(String userId, int amount) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET weekly_earnings = COALESCE(weekly_earnings, 0) + ? WHERE id = ?"
            );
            stmt.setInt(1, amount);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateMonthlyEarnings(String userId, int amount) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE perdoruesit SET monthly_earnings = COALESCE(monthly_earnings, 0) + ? WHERE id = ?"
            );
            stmt.setInt(1, amount);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}