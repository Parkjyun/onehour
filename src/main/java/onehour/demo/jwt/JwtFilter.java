package onehour.demo.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class JwtFilter extends GenericFilterBean {//jwt를 위한 커스텀 필터 만들기 위해

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;
    public JwtFilter(TokenProvider tokenProvider) {//내가 만든 tokenproviderFMF WNDLQGKA
        this.tokenProvider = tokenProvider;
    }

    @Override//jwt토큰의 인증정보를 현재 실행중인 securitycontext에 저장하는 역할을 수행한다.
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String jwt = resolveToken(httpServletRequest);//1.request가 들어올 때 요청에서 토큰을 받아서
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {//2.내가 만든 토큰 유효성 검사에서 토큰을 검사
            Authentication authentication = tokenProvider.getAuthentication(jwt);//3.토큰이 정상이라면 토큰에서 authentication객체를 받아와서
            SecurityContextHolder.getContext().setAuthentication(authentication);//4.securitycontext에 set해준다
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {//필터링을 하기 위해 필요한 토큰 정보를 갖고 오기 위한 함수
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);//Requestheader에서 토큰 정보를 꺼내옴

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
