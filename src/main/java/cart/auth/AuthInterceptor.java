package cart.auth;

import cart.auth.infrastructure.AuthorizationExtractor;
import cart.domain.member.Member;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    private final AuthorizationExtractor authorizationExtractor;

    public AuthInterceptor(AuthorizationExtractor authorizationExtractor) {
        this.authorizationExtractor = authorizationExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Member member = authorizationExtractor.extract(authHeader);
        request.setAttribute("member", member);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
