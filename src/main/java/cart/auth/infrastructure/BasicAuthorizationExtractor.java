package cart.auth.infrastructure;

import cart.domain.member.Member;
import org.apache.tomcat.util.codec.binary.Base64;

public class BasicAuthorizationExtractor implements AuthorizationExtractor {
    private static final String BASIC_TYPE = "Basic";
    private static final String DELIMITER = ":";

    @Override
    public Member extract(String authHeader) {
        if (authHeader == null) {
            return null;
        }

        if ((authHeader.toLowerCase().startsWith(BASIC_TYPE.toLowerCase()))) {
            String authHeaderValue = authHeader.substring(BASIC_TYPE.length()).trim();
            byte[] decodedBytes = Base64.decodeBase64(authHeaderValue);
            String decodedString = new String(decodedBytes);

            String[] credentials = decodedString.split(DELIMITER);
            String email = credentials[0];
            String password = credentials[1];

            return new Member(email, password);
        }

        return null;
    }
}
