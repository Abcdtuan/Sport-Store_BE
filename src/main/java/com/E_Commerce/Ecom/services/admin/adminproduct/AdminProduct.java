package com.E_Commerce.Ecom.services.admin.adminproduct;

import com.E_Commerce.Ecom.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface AdminProduct {

    ProductDto addProduct(ProductDto productDto) throws IOException;

    Page<ProductDto> getAllProducts(int page, int size);

    List<ProductDto> getAllProductsByName(String name);

    boolean deleteProduct(Long id);

    ProductDto getProductById(Long id);

    ProductDto updateProduct(Long id, ProductDto productDto)  throws IOException;

    List<ProductDto> getLowStockProducts();
}
