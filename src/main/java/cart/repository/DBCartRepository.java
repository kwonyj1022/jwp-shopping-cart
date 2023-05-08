package cart.repository;

import cart.entity.CartItemEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Transactional
public class DBCartRepository implements CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public DBCartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CartItemEntity save(CartItemEntity cartItemEntity) {
        String sql = "INSERT INTO cart (member_id, product_id) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            Long memberId = cartItemEntity.getMemberId();
            Long productId = cartItemEntity.getProductId();
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setLong(1, memberId);
            preparedStatement.setLong(2, productId);
            return preparedStatement;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return new CartItemEntity(id, cartItemEntity.getMemberId(), cartItemEntity.getProductId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemEntity> findByUserId(Long userId) {
        String sql = "SELECT id, member_id, product_id " +
                "FROM cart WHERE member_id = ?";
        return jdbcTemplate.query(sql, cartEntityMaker(), userId);
    }

    private static RowMapper<CartItemEntity> cartEntityMaker() {
        return (rs, rowNum) -> {
            Long id = rs.getLong("id");
            Long memberId = rs.getLong("member_id");
            Long productId = rs.getLong("product_id");

            return new CartItemEntity(id, memberId, productId);
        };
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cart WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
