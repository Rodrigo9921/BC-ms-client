package com.nttdata.msclient.dto;

import lombok.Data;

import java.util.List;
@Data
public class ClientDto {
    private String clientId;
    private String name;
    private String lastname;
    private String dni;
    private String phone;
    private String email;
    private String address;
    private String type; // Individual or Business
    private List<PassiveProductDto> passiveProducts;
    private List<ActiveProductDto> activeProducts;
}
