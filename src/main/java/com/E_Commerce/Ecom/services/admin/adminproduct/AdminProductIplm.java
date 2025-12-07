package com.E_Commerce.Ecom.services.admin.adminproduct;

import com.E_Commerce.Ecom.dto.ProductDto;
import com.E_Commerce.Ecom.entity.Brand;
import com.E_Commerce.Ecom.entity.Category;
import com.E_Commerce.Ecom.entity.Product;
import com.E_Commerce.Ecom.entity.ProductImages;
import com.E_Commerce.Ecom.repository.BrandRepository;
import com.E_Commerce.Ecom.repository.CategoryRepository;
import com.E_Commerce.Ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductIplm implements AdminProduct {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    public ProductDto addProduct(ProductDto productDto) throws IOException {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setOrigin(productDto.getOrigin());
        product.setStockQuantity(productDto.getStockQuantity());
        Brand brand = brandRepository.findById(productDto.getBrandId()).get();
        product.setBrand(brand);
        Category category = categoryRepository.findById(productDto.getCategoryId()).orElseThrow();
        product.setCategory(category);
        addImagesToProduct(product, productDto.getImages());
        Product savedProduct = productRepository.save(product);
        if (!savedProduct.getImages().isEmpty() && savedProduct.getImages().get(0).getId() != null) {
            Long thumbnailId = savedProduct.getImages().get(0).getId();
            savedProduct.setThumbnailImageId(thumbnailId);
            savedProduct = productRepository.save(savedProduct);
        }


        return savedProduct.getDto();
    }



    public Page<ProductDto> getAllProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(Product::getDto);
    }

    public List<ProductDto> getAllProductsByName(String name){
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }
    public boolean deleteProduct(Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ProductDto getProductById(Long id) {
       Optional<Product> product = productRepository.findById(id);
       if(product.isPresent()){
           return product.get().getDto();
       }else{
           return null;
       }
    }
    public ProductDto updateProduct(Long id, ProductDto productDto) throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());
        Optional<Brand> optionalBrand = productDto.getBrandId() != null
                ? brandRepository.findById(productDto.getBrandId())
                : Optional.empty();
        if(optionalProduct.isPresent() && optionalCategory.isPresent()){
            Product product = optionalProduct.get();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setCategory(optionalCategory.get());
            product.setOrigin(productDto.getOrigin());
            product.setStockQuantity(productDto.getStockQuantity());
            product.setBrand(optionalBrand.orElse(null));
            addImagesToProduct(product, productDto.getImages());

            Product savedProduct = productRepository.save(product);
            if (!savedProduct.getImages().isEmpty() && savedProduct.getImages().get(0).getId() != null) {
                Long thumbnailId = savedProduct.getImages().get(0).getId();
                savedProduct.setThumbnailImageId(thumbnailId);
                savedProduct = productRepository.save(savedProduct);
            }



            return savedProduct.getDto();

        }
        return null;
    }
    private void addImagesToProduct(Product product, List<MultipartFile> files) throws IOException {
        if (files != null && !files.isEmpty()) {
            product.getImages().clear();
            for (MultipartFile file : files) {
                ProductImages productImages = new ProductImages();
                productImages.setImg(file.getBytes());
                productImages.setProduct(product);
                product.getImages().add(productImages);
            }
            product.setThumbnailImageId(product.getImages().get(0).getId());
        }
    }

    public List<ProductDto> getLowStockProducts() {
        int threshold = 5;
        List<Product> products = productRepository.findByStockQuantityLessThanEqual((long) threshold);
        return products.stream()
                .map(Product::getDto)
                .collect(Collectors.toList());
    }


}
