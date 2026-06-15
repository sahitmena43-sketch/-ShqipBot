package com.shqipbot;

public class User {
    private String id;
    private String username;
    private int balance;
    private long depozitaFundit;
    
    public User(String id, String username, int balance, long depozitaFundit) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.depozitaFundit = depozitaFundit;
    }
    
    public String getId() { return id; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }
    public long getDepozitaFundit() { return depozitaFundit; }
}