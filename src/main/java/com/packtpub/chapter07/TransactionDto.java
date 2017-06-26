package com.packtpub.chapter07;

public class TransactionDto {

    private String targetId;
    private String targetPayPalId;
    private double amount;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetPayPalId() {
        return targetPayPalId;
    }

    public void setTargetPayPalId(String targetPayPalId) {
        this.targetPayPalId = targetPayPalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
