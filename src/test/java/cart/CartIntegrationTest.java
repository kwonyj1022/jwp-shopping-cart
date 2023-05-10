package cart;

import cart.dto.cart.CartItemResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/testSchemaForIntegrationTest.sql"})
public class CartIntegrationTest {

    @LocalServerPort
    int port;

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
    /**
     * "testSchemaForIntegrationTest.sql" member 테이블
     * <p>
     * | id |  email  |  password
     * ----------------------------------
     * | 1  | a@a.com | password1
     * ----------------------------------
     * | 2  | b@b.com | password2
     */
    /**
     * "testSchemaForIntegrationTest.sql" cart 테이블
     * <p>
     * | id | member_id | product_id
     * ------------------------------
     * | 1  |     1     |     1
     * ------------------------------
     * | 2  |     1     |     3
     * ------------------------------
     * | 3  |     2     |     2
     * ------------------------------
     * | 4  |     2     |     3
     */

    @Test
    @DisplayName("헤더에 담긴 인증정보에 해당하는 회원의 장바구니에 담긴 상품 목록을 조회한다.")
    void itemsIntegrationTest_success() throws JsonProcessingException {
        String member1Email = "a@a.com";
        String member1Password = "password1";
        String infoText = member1Email + ":" + member1Password;
        byte[] encodedInfo = Base64.encodeBase64(infoText.getBytes());
        String header = "Basic" + new String(encodedInfo);

        CartItemResponseDto cartItem1 = CartItemResponseDto.of(1L, 1L, "product1", "https://url1.com", 1000);
        CartItemResponseDto cartItem2 = CartItemResponseDto.of(2L, 3L, "product3", "https://url3.com", 3000);
        List<CartItemResponseDto> expectResponse = List.of(cartItem1, cartItem2);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, header)
                .when().get("/cart/items")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @Test
    @DisplayName("헤더에 인증정보가 없는 조회 요청은 BAD_REQUEST가 된다.")
    void itemsIntegrationTest_fail_noAuthorization() {
        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "")
                .when().get("/cart/items")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("헤더에 인증정보가 있지만, 회원이 아니라면 해당 조회 요청은 BAD_REQUEST가 된다.")
    void itemsIntegrationTest_fail_notMember() {
        String nonMemberEmail = "non@non.com";
        String nonMemberPassword = "nonPassword";
        String infoText = nonMemberEmail + ":" + nonMemberPassword;
        byte[] encodedInfo = Base64.encodeBase64(infoText.getBytes());
        String header = "Basic" + new String(encodedInfo);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, header)
                .when().get("/cart/items")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("헤더에 담긴 인증정보에 해당하는 회원의 장바구니에 상품을 추가한다.")
    void addItemIntegrationTest_success() throws JsonProcessingException {
        String member1Email = "a@a.com";
        String member1Password = "password1";
        String infoText = member1Email + ":" + member1Password;
        byte[] encodedInfo = Base64.encodeBase64(infoText.getBytes());
        String header = "Basic" + new String(encodedInfo);

        CartItemResponseDto expectResponse = CartItemResponseDto.of(5L, 2L, "product2", "https://url2.com", 2000);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, header)
                .when().post("cart/items/2")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @Test
    @DisplayName("헤더에 인증정보가 없는 장바구니 추가 요청은 BAD_REQUEST가 된다.")
    void addItemIntegrationTest_fail_noAuthorization() {
        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "")
                .when().post("/cart/items/2")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("헤더에 인증정보가 있지만, 회원이 아니라면 해당 장바구니 추가 요청은 BAD_REQUEST가 된다.")
    void addItemIntegrationTest_fail_notMember() {
        String nonMemberEmail = "non@non.com";
        String nonMemberPassword = "nonPassword";
        String infoText = nonMemberEmail + ":" + nonMemberPassword;
        byte[] encodedInfo = Base64.encodeBase64(infoText.getBytes());
        String header = "Basic" + new String(encodedInfo);

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, header)
                .when().post("/cart/items/2")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("장바구니 정보를 삭제한다.")
    void deleteItemTest() throws JsonProcessingException {
        String member1Email = "a@a.com";
        String member1Password = "password1";
        String infoText = member1Email + ":" + member1Password;
        byte[] encodedInfo = Base64.encodeBase64(infoText.getBytes());
        String header = "Basic" + new String(encodedInfo);

        CartItemResponseDto cartItem1 = CartItemResponseDto.of(1L, 1L, "product1", "https://url1.com", 1000);
        List<CartItemResponseDto> expectCartItemsAfterDelete = List.of(cartItem1);


        RestAssured.given().log().all()
                .when().delete("/cart/items/2")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, header)
                .when().get("/cart/items")
                .then().log().all()
                .body(is(objectMapper.writeValueAsString(expectCartItemsAfterDelete)));
    }
}
