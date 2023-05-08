package cart.repository;

import cart.entity.CartItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Sql({"/testSchema.sql"})
class DBCartRepositoryTest {

    private final long memberId = 1L;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private CartRepository cartRepository;
    private CartItemEntity entity1;
    private CartItemEntity entity2;

    @BeforeEach
    void setUp() {
        cartRepository = new DBCartRepository(jdbcTemplate);

        CartItemEntity entity1 = new CartItemEntity(null, memberId, 1L);
        CartItemEntity entity2 = new CartItemEntity(null, memberId, 2L);

        this.entity1 = cartRepository.save(entity1);
        this.entity2 = cartRepository.save(entity2);
    }

    @Test
    @DisplayName("회원 id와 상품 id를 cart DB에 저장한다.")
    void saveTest() {
        CartItemEntity entity3 = new CartItemEntity(null, memberId, 3L);
        cartRepository.save(entity3);

        List<CartItemEntity> entities = cartRepository.findByMemberId(memberId);
        assertThat(entities).hasSize(3);
    }

    @Test
    @DisplayName("사용자 id로 cart 정보를 조회한다.")
    void findByMemberIdTest() {
        List<CartItemEntity> entities = cartRepository.findByMemberId(memberId);
        assertThat(entities).isEqualTo(List.of(entity1, entity2));
    }

    @Test
    @DisplayName("cartId에 해당하는 카트 정보를 삭제한다.")
    void deleteByIdTest() {
        cartRepository.deleteById(memberId);
        List<CartItemEntity> entities = cartRepository.findByMemberId(memberId);
        assertThat(entities).hasSize(1);
    }
}