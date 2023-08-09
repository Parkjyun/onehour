package onehour.demo.controller;

import onehour.demo.dto.LoginDto;
import onehour.demo.dto.TokenDto;
import onehour.demo.jwt.JwtFilter;
import onehour.demo.jwt.TokenProvider;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController//controller + response body
@RequestMapping("/api")//기본 path는 /api
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //생성자가 하나 자동으로 autowired가 붙고 자동 의존관계 주입이 일어남
    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")//로그인임ㅋㅋ
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =//유저네임비번 인증 토큰 생성
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        //authenticatetoken통해 authenticate메서드가 실행이 될 떄
        //내가 만든 customuserdetailsservice class의 loadUserbyUsernamemethod가 실행된다., 이값을 가지고 authentication 객체 생성
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);//authentication을 securitycontext에 저장함

        String jwt = tokenProvider.createToken(authentication);//토큰생성

        HttpHeaders httpHeaders = new HttpHeaders();//해더만들고
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);//인증해더에 jwt token넣고

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);//바디, 헤더, status순 .
    }
}
//@valid : 내가 엔티티에서 @notnull, @size같은 제약 조건을 걸었자나 그리고 변수 앞에 @valid를 붙이면 유효성 체크 후 전송된 데이터가 도메인 클래스에서 지정한 검증 규칙에 어긋날 경우 돌려보냄
//@requestbody : 이 어노테이션이 붙은 파라미터에는 Http요청의 본문이 그대로 전달된다 즉 json의 형태의 데이터를 자바 객체에 자동으로 값을 넣어줌
//reponseentity : httpentity(요청 또는 응답에 해당하는 httpheader와 http바디를 포함하는 클래스)를 상속받아 생성된다.
//고로 httpstatus, httpheaders, httpbody를 포함하는 클래스이다