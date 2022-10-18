package br.com.juliancambraia.pratic.elasticsearch;

import br.com.juliancambraia.pratic.elasticsearch.models.Product;
import br.com.juliancambraia.pratic.elasticsearch.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class Application {

    private static final String COMMA_DELIMITER = ",";

    private final ElasticsearchOperations esOps;

    private final ProductRepository productRepo;

    @Autowired
    public Application(ElasticsearchOperations esOps, ProductRepository productRepo) {
        this.esOps = esOps;
        this.productRepo = productRepo;
    }

    @PreDestroy
    public void deleteIndex() {
        esOps.indexOps(Product.class).delete();
    }

    @PostConstruct
    public void buildIndex() {

        esOps.indexOps(Product.class).refresh();
        productRepo.deleteAll();
        productRepo.saveAll(prepareDataset());
    }

    private Collection<Product> prepareDataset() {
        Resource resource = new ClassPathResource("fashion-products.csv");
        List<Product> productList = new ArrayList<>();

        try (
                InputStream input = resource.getInputStream();
                Scanner scanner = new Scanner(resource.getInputStream());) {
            int lineNo = 0;
            while (scanner.hasNextLine()) {
                ++lineNo;
                String line = scanner.nextLine();
                if (lineNo == 1) continue;
                Optional<Product> product =
                        csvRowToProductMapper(line);
                product.ifPresent(productList::add);
            }
        } catch (Exception e) {
            log.error("File read error: ", e);
        }
        return productList;
    }

    private Optional<Product> csvRowToProductMapper(final String line) {
        try (
                Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                String name = rowScanner.next();
                String description = rowScanner.next();
                String manufacturer = rowScanner.next();
                return Optional.of(
                        Product.builder()
                                .name(name)
                                .description(description)
                                .manufacturer(manufacturer)
                                .build());

            }
        }
        return Optional.of(null);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
