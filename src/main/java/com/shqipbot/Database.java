package com.shqipbot;

import java.sql.*;
import java.util.*;

public class Database {
    private Connection connection;
    
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:shqipbot.db");
            
            // Tabela e përdoruesve
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
            
            // Tabela e inventarit
            String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (" +
                                  "user_id TEXT, " +
                                  "item_name TEXT, " +
                                  "quantity INTEGER DEFAULT 1, " +
                                  "PRIMARY KEY (user_id, item_name)" +
                                  ")";
            connection.createStatement().execute(inventorySql);
            
            // Tabela e roleve VIP të personalizuara
            String rolesSql = "CREATE TABLE IF NOT EXISTS purchased_roles (" +
                              "user_id TEXT, " +
                              "role_name TEXT, " +
                              "custom_role_name TEXT, " +
                              "role_id TEXT, " +
                              "purchased_at INTEGER, " +
                              "expires_at INTEGER, " +
                              "duration TEXT, " +
                              "status TEXT DEFAULT 'pending', " +
                              "PRIMARY KEY (user_id, role_name)" +
                              ")";
            connection.createStatement().execute(rolesSql);
            
            // Tabela e Admin-ave
            String adminSql = "CREATE TABLE IF NOT EXISTS admins (" +
                              "user_id TEXT PRIMARY KEY, " +
                              "purchased_at INTEGER, " +
                              "expires_at INTEGER" +
                              ")";
            connection.createStatement().execute(adminSql);
            
            System.out.println("✅ Databaza u lidh!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== PËRDORUESIT ====================
    
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
    
    // ==================== BALANCE (XEHP) ====================
    
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
    
    // ==================== BANKA ====================
    
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
    
    // ==================== DEPOZITA BONUS ====================
    
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
    
    // ==================== LEADERBOARD ====================
    
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
    
    // ==================== ITEM SHOP (INVENTAR) ====================
    
    public void addItem(String userId, String itemName) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO inventory (user_id, item_name) VALUES (?, ?)"
            );
            stmt.setString(1, userId);
            stmt.setString(2, itemName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getInventory(String userId) {
        List<String> items = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT item_name FROM inventory WHERE user_id = ?"
            );
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    public boolean hasItem(String userId, String itemName) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM inventory WHERE user_id = ? AND item_name = ?"
            );
            stmt.setString(1, userId);
            stmt.setString(2, itemName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // ==================== ROLET TË PERSONALIZUARA ====================
    
    public void addPurchasedRole(String userId, String roleName, String customRoleName, String roleId, long expiresAt, String duration) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO purchased_roles (user_id, role_name, custom_role_name, role_id, purchased_at, expires_at, duration, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'pending')"
            );
            stmt.setString(1, userId);
            stmt.setString(2, roleName);
            stmt.setString(3, customRoleName);
            stmt.setString(4, roleId);
            stmt.setLong(5, System.currentTimeMillis());
            stmt.setLong(6, expiresAt);
            stmt.setString(7, duration);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasActiveRole(String userId, String roleName) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM purchased_roles WHERE user_id = ? AND role_name = ? AND expires_at > ? AND status = 'approved'"
            );
            stmt.setString(1, userId);
            stmt.setString(2, roleName);
            stmt.setLong(3, System.currentTimeMillis());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getActiveRoles(String userId) {
        List<String> roles = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT custom_role_name FROM purchased_roles WHERE user_id = ? AND expires_at > ? AND status = 'approved'"
            );
            stmt.setString(1, userId);
            stmt.setLong(2, System.currentTimeMillis());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("custom_role_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    public List<Map<String, String>> getPendingRoles() {
        List<Map<String, String>> pending = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT user_id, custom_role_name, duration FROM purchased_roles WHERE status = 'pending'"
            );
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> role = new HashMap<>();
                role.put("user_id", rs.getString("user_id"));
                role.put("custom_role_name", rs.getString("custom_role_name"));
                role.put("duration", rs.getString("duration"));
                pending.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pending;
    }
    
    public void approveRole(String userId, String customRoleName) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE purchased_roles SET status = 'approved' WHERE user_id = ? AND custom_role_name = ?"
            );
            stmt.setString(1, userId);
            stmt.setString(2, customRoleName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ==================== ADMIN (GLOBAL) ====================
    
    public void addAdmin(String userId, long expiresAt) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO admins (user_id, purchased_at, expires_at) VALUES (?, ?, ?)"
            );
            stmt.setString(1, userId);
            stmt.setLong(2, System.currentTimeMillis());
            stmt.setLong(3, expiresAt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isAdmin(String userId) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM admins WHERE user_id = ? AND expires_at > ?"
            );
            stmt.setString(1, userId);
            stmt.setLong(2, System.currentTimeMillis());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getActiveAdmins() {
        List<String> admins = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT user_id FROM admins WHERE expires_at > ?"
            );
            stmt.setLong(1, System.currentTimeMillis());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                admins.add(rs.getString("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}