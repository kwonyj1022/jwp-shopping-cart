package cart;

import cart.dto.product.ProductRequestDto;
import cart.dto.product.ProductResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/testSchemaForIntegrationTest.sql"})
public class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * "testSchemaForIntegrationTest.sql" product 테이블
     * <p>
     * | id |   name   |      imgUrl      | price
     * --------------------------------------------------
     * | 1  | product1 | https://url1.com | 1000
     * --------------------------------------------------
     * | 2  | product2 | https://url2.com | 2000
     * --------------------------------------------------
     * | 3  | product3 | https://url3.com | 3000
     */

    @Test
    @DisplayName("상품 정보를 등록한다.")
    void addProductIntegrationTest_success() throws JsonProcessingException {
        ProductRequestDto request = new ProductRequestDto("product4", "https://url4.com", 4000);
        ProductResponseDto expectResponse = ProductResponseDto.of(4L, "product4", "https://url4.com", 4000);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/products")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @ParameterizedTest
    @MethodSource("invalidProductMaker")
    @DisplayName("잘못된 형식의 상품 정보를 등록하면 BAD_REQUEST가 된다.")
    void addProductIntegrationTest_fail(ProductRequestDto badRequest) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badRequest)
                .when().post("/products")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    static Stream<Arguments> invalidProductMaker() {
        return Stream.of(
                Arguments.arguments(new ProductRequestDto("a".repeat(51), "https://naver.com", 1000)),
                Arguments.arguments(new ProductRequestDto("aaa", "https://" + "a".repeat(8001) + ".com", 1000)),
                Arguments.arguments(new ProductRequestDto("aaa", "https://naver.com", -1000)));
    }

    @Test
    @DisplayName("상품 정보를 수정한다.")
    void updateProductIntegrationTest_success() throws JsonProcessingException {
        ProductRequestDto request = new ProductRequestDto("product3", "https://url4.com", 100000);
        ProductResponseDto expectResponse = ProductResponseDto.of(3L, "product3", "https://url4.com", 100000);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/products/3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @ParameterizedTest
    @MethodSource("invalidProductMaker")
    @DisplayName("잘못된 형식의 상품 정보로 수정하면 BAD_REQUEST가 된다.")
    void updateProductIntegrationTest_fail(ProductRequestDto badRequest) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badRequest)
                .when().put("/products/*")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @DisplayName("상품 정보를 삭제한다.")
    void deleteProductIntegrationTest() {
        RestAssured.given().log().all()
                .when().delete("/products/3")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
