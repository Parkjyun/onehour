package onehour.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {//토큰정보 응답 내줄 떄 사용

    private String token;
}