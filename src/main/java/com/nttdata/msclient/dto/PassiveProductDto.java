package com.nttdata.msclient.dto;

import lombok.Data;

@Data
public class PassiveProductDto {
    private String id;
    private String name;
    private double balance;
    private boolean commissionFree;
    private int monthlyMovementLimit;
}
