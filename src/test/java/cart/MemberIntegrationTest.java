package cart;

import cart.dto.member.MemberRequestDto;
import cart.dto.member.MemberResponseDto;
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
public class MemberIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    /**
     * "testSchemaForIntegrationTest.sql" member 테이블
     * <p>
     * | id |  email  |  password
     * ----------------------------------
     * | 1  | a@a.com | password1
     * ----------------------------------
     * | 2  | b@b.com | password2
     */

    @Test
    @DisplayName("회원 정보를 등록한다.")
    void joinMemberIntegrationTest_success() throws JsonProcessingException {
        MemberRequestDto request = new MemberRequestDto("c@c.com", "password3");
        MemberResponseDto expectResponse = MemberResponseDto.of(3L, "c@c.com", "password3");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @Test
    @DisplayName("동일한 email을 가진 회원을 등록하면 BAD_REQUEST가 된다.")
    void addMemberIntegrationTest_fail_duplicateEmail() {
        MemberRequestDto request = new MemberRequestDto("b@b.com", "password3");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @MethodSource("invalidMemberMaker")
    @DisplayName("잘못된 형식의 회원 정보를 등록하면 BAD_REQUEST가 된다.")
    void addPMemberIntegrationTest_fail_invalidMember(MemberRequestDto badRequest) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badRequest)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    static Stream<Arguments> invalidMemberMaker() {
        return Stream.of(
                Arguments.arguments(new MemberRequestDto("invalidEmail", "1234")),
                Arguments.arguments(new MemberRequestDto("c@c.com", "a".repeat(51)))
        );
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    void updateMemberIntegrationTest_success() throws JsonProcessingException {
        MemberRequestDto request = new MemberRequestDto("b@b.com", "newPassword");
        MemberResponseDto expectResponse = MemberResponseDto.of(2L, "b@b.com", "newPassword");

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().put("/members/2")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(objectMapper.writeValueAsString(expectResponse)));
    }

    @ParameterizedTest
    @MethodSource("invalidMemberMaker")
    @DisplayName("잘못된 형식의 회원 정보로 수정하면 BAD_REQUEST가 된다.")
    void updateMemberIntegrationTest_fail(MemberRequestDto badRequest) {
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badRequest)
                .when().put("/members/*")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @DisplayName("회원 정보를 삭제한다.")
    void deleteMemberIntegrationTest() {
        RestAssured.given().log().all()
                .when().delete("/members/2")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());

        // TODO: delete 되었는지 어떻게 확인?
    }
}
