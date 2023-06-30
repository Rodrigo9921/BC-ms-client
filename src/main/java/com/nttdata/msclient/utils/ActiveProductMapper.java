package com.nttdata.msclient.utils;

import com.nttdata.msclient.dto.ActiveProductDto;
import com.nttdata.msclient.model.ActiveProduct;

public class ActiveProductMapper {
    public static ActiveProductDto convertToDto(ActiveProduct activeProduct) {
        ActiveProductDto activeProductDto = new ActiveProductDto();
        activeProductDto.setId(activeProduct.getId());
        activeProductDto.setName(activeProduct.getName());
        activeProductDto.setBalance(activeProduct.getBalance());
        activeProductDto.setInterestRate(activeProduct.getInterestRate());
        return activeProductDto;
    }

    public static ActiveProduct convertToEntity(ActiveProductDto activeProductDto) {
        ActiveProduct activeProduct = new ActiveProduct();
        activeProduct.setId(activeProductDto.getId());
        activeProduct.setName(activeProductDto.getName());
        activeProduct.setBalance(activeProductDto.getBalance());
        activeProduct.setInterestRate(activeProductDto.getInterestRate());
        return activeProduct;
    }
}
