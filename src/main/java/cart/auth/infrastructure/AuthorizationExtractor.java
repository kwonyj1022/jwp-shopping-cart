package cart.auth.infrastructure;

import cart.domain.member.Member;

public interface AuthorizationExtractor {

    Member extract(String authorization);
}
