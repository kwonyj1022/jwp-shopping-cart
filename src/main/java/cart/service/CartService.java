package cart.service;

import cart.dto.cart.CartItemDto;
import cart.dto.member.MemberDto;
import cart.entity.CartItemEntity;
import cart.entity.ProductEntity;
import cart.repository.CartRepository;
import cart.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartItemDto addItem(MemberDto memberDto, Long productId) {
        CartItemEntity entity = new CartItemEntity(null, memberDto.getId(), productId);
        Optional<ProductEntity> nullableProductEntity = productRepository.findById(entity.getProductId());
        if (nullableProductEntity.isEmpty()) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }
        CartItemEntity savedEntity = cartRepository.save(entity);
        ProductEntity productEntity = nullableProductEntity.get();

        return CartItemDto.fromCartIdAndProductEntity(savedEntity.getId(), productEntity);
    }

    @Transactional(readOnly = true)
    public List<CartItemDto> findAllUserItems(MemberDto memberDto) {
        List<CartItemEntity> entities = cartRepository.findByUserId(memberDto.getId());
        return entities.stream()
                .map((cartEntity -> CartItemDto.fromCartIdAndProductEntity(cartEntity.getId(),
                        productRepository.findById(cartEntity.getProductId()).get())))
                .collect(toList());
    }

    @Transactional
    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }
}
