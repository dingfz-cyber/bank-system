package com.bank.module.product.dto;

import lombok.Data;

/**
 * 产品查询参数 DTO
 */
@Data
public class ProductQueryDto {

    private Integer productType;
    private String keyword;
    private long page = 1;
    private long pageSize = 10;
}
