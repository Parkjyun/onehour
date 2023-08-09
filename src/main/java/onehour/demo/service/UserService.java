package onehour.demo.service;

import onehour.demo.dto.UserDto;
import onehour.demo.entity.Authority;
import onehour.demo.entity.User;
import onehour.demo.repository.UserRepository;
import onehour.demo.util.SecurityUtil;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional//특정 실행단위에서 오류 발생시 전체 실행 내용을 롤백해주는 기능
    public UserDto signup(UserDto userDto) {//회원가입로직 수행
        //1.dto로 넘어온 유저네임이 이미 db에 있다면 예외
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getEmail()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
        //2.만약 유저정보가 없다면 권한 정보를 만듦
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")//일반계정은 user권한, 관리자 계정은 role로 admin, user모두 가짐
                .build();
        //3.유저정보도 만들어서 넣음
        User user = User.builder()
                .username(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return UserDto.from(userRepository.save(user));//save의 반환은 user -> 디비에 저장하고 userdto반환ㄴ
    }

    @Transactional(readOnly = true)
    public UserDto getUserWithAuthorities(String email) {//유저네임을 기준으로 유저정보를 갖고옴
        return UserDto.from(userRepository.findOneWithAuthoritiesByUsername(email).orElse(null));
    }

    @Transactional(readOnly = true)
    public UserDto getMyUserWithAuthorities() {//현재 securitycontext에 저장된 유저의 유저정보만 갖고옴
        return UserDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findOneWithAuthoritiesByUsername)
                        .orElseThrow(() -> new RuntimeException("Member not found"))
        );
    }
}