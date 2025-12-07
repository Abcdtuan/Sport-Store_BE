package com.E_Commerce.Ecom.controller.admin;
import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.entity.Product;
import com.E_Commerce.Ecom.services.admin.adminproduct.AdminProduct;
import com.E_Commerce.Ecom.services.admin.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminProductController {

    private final AdminProduct adminProduct;

    @PostMapping("/product")
    public ResponseEntity<ProductDto> addProduct(@ModelAttribute ProductDto productDto) throws IOException {
        ProductDto productDto1 = adminProduct.addProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productDto1);
    }
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size
    ) {
        Page<ProductDto> productDtos = adminProduct.getAllProducts(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(productDtos);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProductDto>> getAllProductsByName(@PathVariable String name) {
        List<ProductDto> productDtos = adminProduct.getAllProductsByName(name);
        return ResponseEntity.status(HttpStatus.OK).body(productDtos);
    }

    @DeleteMapping("/product/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable Long id)  {
        boolean deleted = adminProduct.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id)  {
        ProductDto productDto = adminProduct.getProductById(id);
        if(productDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(productDto);
        }
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,@ModelAttribute ProductDto productDto)  throws IOException {
        ProductDto productDto1 = adminProduct.updateProduct(id, productDto);
        if(productDto1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(productDto1);
        }

    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockProducts() {
        List<ProductDto> productDtos = adminProduct.getLowStockProducts();
        return ResponseEntity.status(HttpStatus.OK).body(productDtos);
    }
}
