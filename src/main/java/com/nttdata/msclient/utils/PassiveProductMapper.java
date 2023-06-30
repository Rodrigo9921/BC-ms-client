package com.nttdata.msclient.utils;

import com.nttdata.msclient.dto.PassiveProductDto;
import com.nttdata.msclient.model.PassiveProduct;

public class PassiveProductMapper {
    public static PassiveProductDto convertToDto(PassiveProduct passiveProduct) {
        PassiveProductDto passiveProductDto = new PassiveProductDto();
        passiveProductDto.setId(passiveProduct.getId());
        passiveProductDto.setName(passiveProduct.getName());
        passiveProductDto.setBalance(passiveProduct.getBalance());
        passiveProductDto.setCommissionFree(passiveProduct.isCommissionFree());
        passiveProductDto.setMonthlyMovementLimit(passiveProduct.getMonthlyMovementLimit());
        return passiveProductDto;
    }

    public static PassiveProduct convertToEntity(PassiveProductDto passiveProductDto) {
        PassiveProduct passiveProduct = new PassiveProduct();
        passiveProduct.setId(passiveProductDto.getId());
        passiveProduct.setName(passiveProductDto.getName());
        passiveProduct.setBalance(passiveProductDto.getBalance());
        passiveProduct.setCommissionFree(passiveProductDto.isCommissionFree());
        passiveProduct.setMonthlyMovementLimit(passiveProductDto.getMonthlyMovementLimit());
        return passiveProduct;
    }
}
