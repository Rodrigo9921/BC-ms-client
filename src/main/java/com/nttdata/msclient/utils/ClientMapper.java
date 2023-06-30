package com.nttdata.msclient.utils;

import com.nttdata.msclient.dto.ClientDto;
import com.nttdata.msclient.model.Client;

import java.util.stream.Collectors;

public class ClientMapper {
    public static ClientDto convertToDto(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setClientId(client.getClientId());
        clientDto.setName(client.getName());
        clientDto.setLastname(client.getLastname());
        clientDto.setDni(client.getDni());
        clientDto.setPhone(client.getPhone());
        clientDto.setEmail(client.getEmail());
        clientDto.setAddress(client.getAddress());
        clientDto.setType(client.getType());
        clientDto.setPassiveProducts(client.getPassiveProduct().stream().map(PassiveProductMapper::convertToDto).collect(Collectors.toList()));
        clientDto.setActiveProducts(client.getActiveProduct().stream().map(ActiveProductMapper::convertToDto).collect(Collectors.toList()));
        // ...other fields...
        return clientDto;
    }

    public static Client convertToEntity(ClientDto clientDto) {
        Client client = new Client();
        client.setClientId(clientDto.getClientId());
        client.setName(clientDto.getName());
        client.setLastname(clientDto.getLastname());
        client.setDni(clientDto.getDni());
        client.setPhone(clientDto.getPhone());
        client.setEmail(clientDto.getEmail());
        client.setAddress(clientDto.getAddress());
        client.setType(clientDto.getType());
        client.setPassiveProduct(clientDto.getPassiveProducts().stream().map(PassiveProductMapper::convertToEntity).collect(Collectors.toList()));
        client.setActiveProduct(clientDto.getActiveProducts().stream().map(ActiveProductMapper::convertToEntity).collect(Collectors.toList()));
        // ...other fields...
        return client;
    }
}
