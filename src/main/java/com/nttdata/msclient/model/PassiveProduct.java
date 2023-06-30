package com.nttdata.msclient.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PassiveProduct {
    private String id;
    private String name;
    private double balance;
    private boolean commissionFree;
    private int monthlyMovementLimit;
}
