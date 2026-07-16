package com.shqipbot;

public class User {
    private String id;
    private String username;
    private int balance;
    private int bank;
    private long depozitaFundit;
    private int weeklyEarnings;
    private int monthlyEarnings;
    
    public User(String id, String username, int balance, int bank, long depozitaFundit) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.bank = bank;
        this.depozitaFundit = depozitaFundit;
        this.weeklyEarnings = 0;
        this.monthlyEarnings = 0;
    }
    
    public String getId() { return id; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }
    public int getBank() { return bank; }
    public long getDepozitaFundit() { return depozitaFundit; }
    public int getWeeklyEarnings() { return weeklyEarnings; }
    public int getMonthlyEarnings() { return monthlyEarnings; }
    
    public void setBalance(int balance) { this.balance = balance; }
    public void setBank(int bank) { this.bank = bank; }
    public void setDepozitaFundit(long depozitaFundit) { this.depozitaFundit = depozitaFundit; }
    public void setWeeklyEarnings(int weeklyEarnings) { this.weeklyEarnings = weeklyEarnings; }
    public void setMonthlyEarnings(int monthlyEarnings) { this.monthlyEarnings = monthlyEarnings; }
    
    public int getTotalMoney() {
        return balance + bank;
    }
}