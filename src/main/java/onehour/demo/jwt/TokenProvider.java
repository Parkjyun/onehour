package onehour.demo.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    @Autowired
    public TokenProvider(//유일한 생성자에는 autowired고로 자동으로 의존성 주입
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }


    @Override//initializingbean을 implement해서 이것을 override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);//주입 받은 secret값을 base64 decode해서 key변수에 할당 b64인코딩=이진데이터를텍스트로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {//authentication객체의 권한정보를 이용해서 토큰을 생성하는 메서드
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);//tokenvaliditymillisecond = yml에서 설정한 시간

        return Jwts.builder()//토큰생성
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)//시크릿값을 다시 이진 데이터로
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {//token에 담겨있는 정보를 이용해서 authentication 객체를 리턴하느 메소드 생성
        Claims claims = Jwts//토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication객체를 리턴
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =//클레임에서 권한정보를 빼내고 이를 사용하여 유저 객체를 만듦
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);//유저객체 토큰 권한 정보를 사용하여 최종적으로 authentication객체를 return
    }

    public boolean validateToken(String token) {//토큰을 parameter로 받아서 토큰의 유효성 검사를 진행
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);//토큰을 받아서 parsing, parsing시 발생하는 exception들을 catc, 정상시 true, 문제시 false
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}