package com.example.applefarm_.product.service;

import com.example.applefarm_.exception.CustomException;
import com.example.applefarm_.exception.ExceptionStatus;
import com.example.applefarm_.product.dto.ProductRequest;
import com.example.applefarm_.product.dto.ProductResponse;
import com.example.applefarm_.product.entitiy.Product;
import com.example.applefarm_.product.repository.ProductRepository;
import com.example.applefarm_.user.entitiy.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse saveProduct(ProductRequest request, Long sellerId) {
        Product product = productRepository.save(new Product(request,sellerId));
        return new ProductResponse(product);
    }

    @Transactional
    public List<ProductResponse> getProducts(int page, int size, Long sellerId) {
        Page<Product> products= productRepository.findAllBySellerId(sellerId,PageRequest.of(page-1,size,Sort.Direction.DESC,"id"));
        if(products.isEmpty()){
            throw new CustomException(ExceptionStatus.PAGINATION_IS_NOT_EXIST);
        }
        List<ProductResponse> productResponseList = products.stream().map(ProductResponse::new).collect(Collectors.toList());
        return productResponseList;
    }



    @Transactional
    public void updateProduct(Long id, Long sellerId,ProductRequest productRequest) {
        Product foundProduct = productRepository.findByIdAndSellerId(id,sellerId).orElseThrow(
                ()-> new CustomException(ExceptionStatus.Product_IS_NOT_EXIST)
        );
        foundProduct.updateProduct(productRequest);
        productRepository.save(foundProduct); //    코드가 많아질때 대비하여 @Transactional 이 없으면 save 되지 않으므로 . 가시적으로 save()호출 하는게 좋음
    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product foundProduct = productRepository.findByIdAndSellerId(id,sellerId).orElseThrow(
                ()-> new CustomException(ExceptionStatus.Product_IS_NOT_EXIST)
        );
        productRepository.deleteById(id);
    }


}
