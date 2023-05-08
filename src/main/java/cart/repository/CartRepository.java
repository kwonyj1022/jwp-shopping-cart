package cart.repository;

import cart.entity.CartItemEntity;

import java.util.List;

public interface CartRepository {

    CartItemEntity save(CartItemEntity cartItemEntity);

    List<CartItemEntity> findByUserId(Long userId);

    void deleteById(Long id);
}
