package com.nttdata.msclient.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActiveProduct {
    private String id;
    private String name;
    private double balance;
    private double interestRate;
}
