package cart.service;

import cart.domain.product.Product;
import cart.dto.product.ProductDto;
import cart.dto.product.ProductRequestDto;
import cart.entity.ProductEntity;
import cart.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductDto> findAll() {
        List<ProductEntity> entities = repository.findAll();
        return entities.stream()
                .map(ProductDto::fromEntity)
                .collect(toList());
    }

    public ProductDto add(ProductRequestDto requestDto) {
        Product product = new Product(requestDto.getName(), requestDto.getImgUrl(), requestDto.getPrice());
        ProductEntity entity = new ProductEntity(null, product.getName(), product.getImgUrl(), product.getPrice());
        ProductEntity savedEntity = repository.save(entity);
        return ProductDto.fromEntity(savedEntity);
    }

    public ProductDto updateById(ProductRequestDto requestDto, Long id) {
        Product product = new Product(requestDto.getName(), requestDto.getImgUrl(), requestDto.getPrice());
        ProductEntity entity = new ProductEntity(id, product.getName(), product.getImgUrl(), product.getPrice());
        repository.update(entity);
        return ProductDto.fromEntity(entity);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
