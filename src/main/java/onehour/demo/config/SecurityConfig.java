package onehour.demo.config;


import onehour.demo.jwt.JwtAccessDeniedHandler;
import onehour.demo.jwt.JwtAuthenticationEntryPoint;
import onehour.demo.jwt.JwtSecurityConfig;
import onehour.demo.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableMethodSecurity
@EnableWebSecurity//기본적인 web보안을 활성화,componentscan의 대상 빈으로 관리됨
@EnableGlobalMethodSecurity(prePostEnabled = true)//@preauthorize라는 어노테이션을 메소드 단위로 추가하기 위햐서 적용
public class SecurityConfig {


    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(//유일한 생성자 autowired붙은거임 의존관계 주입
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;//내가 만든 tokenprovider, jwtauthenticationentrypoint,jwtaccessdeniedhandler를 주입
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()//restapi설정시 csrf필요하지 않음

                .exceptionHandling()//exception handling시 우리가 만들어 놓은 클래스를 추가
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()//세션을 사용하지 않기에 세션을 무상태로 지정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()//httpservletrequest를 사용하는 요청들에 대한 접근제한을 설정하겠다
                .antMatchers("/api/hello").permitAll()///api/hello에 대한 요청은 인증없이 접근을 허용하겠다는 의미
                .antMatchers("/api/authenticate").permitAll()//로그인 api
                .antMatchers("/api/signup").permitAll()//회원가입 api는 토큰이 없는 상태에서 요청이 들어오기에 항상 permitall
                .anyRequest().authenticated()//나머지 요청들에 대해서는 인증을 받아야한다

                .and()
                .apply(new JwtSecurityConfig(tokenProvider));//jwtsecurityconfig도 설정






        return http.build();
    }

}

