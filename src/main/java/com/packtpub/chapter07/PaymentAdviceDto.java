package com.packtpub.chapter07;

public class PaymentAdviceDto {

    private final double amount;
    private final String targetPaypalId;
    private final String desc;

    public PaymentAdviceDto (double amount, String targetPaypalId, String desc){
        this.amount = amount;
        this.targetPaypalId = targetPaypalId;
        this.desc = desc;
    }

    public double getAmount() {
        return amount;
    }

    public String getTargetPaypalId() {
        return targetPaypalId;
    }

    public String getDesc() {
        return desc;
    }


}
