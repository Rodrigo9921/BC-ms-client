package com.nttdata.msclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clients")
public class Client {
    @Id
    private String clientId;
    private String name;
    private String lastname;
    private String dni;
    private String phone;
    private String email;
    private String address;
    private String type; // Personal o Empresarial
    private List<PassiveProduct> passiveProduct = new ArrayList<>();
    private List<ActiveProduct> activeProduct = new ArrayList<>();
}
