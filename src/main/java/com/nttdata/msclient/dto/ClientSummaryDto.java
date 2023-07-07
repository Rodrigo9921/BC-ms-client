package com.nttdata.msclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryDto {
    private ClientDto client;
    private List<ActiveProductDto> activeProducts;
    private List<PassiveProductDto> passiveProducts;
}
