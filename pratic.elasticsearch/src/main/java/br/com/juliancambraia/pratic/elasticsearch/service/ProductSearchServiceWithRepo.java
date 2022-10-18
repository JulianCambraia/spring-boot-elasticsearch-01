package br.com.juliancambraia.pratic.elasticsearch.service;

import br.com.juliancambraia.pratic.elasticsearch.models.Product;
import br.com.juliancambraia.pratic.elasticsearch.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSearchServiceWithRepo {

    private final ProductRepository productRepository;

    @Autowired
    public ProductSearchServiceWithRepo(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProductIndexBulk(final List<Product> products) {
        productRepository.saveAll(products);
    }

    public void createProductIndex(final Product product) {
        productRepository.save(product);
    }

    public List<Product> findByProductName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findProductsByManufacturerAndCategory(String manufacturer, String category) {
        return productRepository.findByManufacturerAndCategory(manufacturer, category);
    }

    public List<Product> findByProductMatchingNames(final String productName) {
        return productRepository.findByNameContaining(productName);
    }
}
