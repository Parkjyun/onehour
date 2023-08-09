package onehour.demo.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import onehour.demo.entity.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @Size(min = 5, max = 50)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//password는 직렬화(serialization)되지 않는다
    @NotNull//entity에서 바로 jsonignore하지 않고 jsonproperty로 dto에서 막은 이유 : jsonignore시 deserialize도 못해 valid를 통한 검증못해서 jsonproperty로 직렬화만 막음
    @Size(min = 3, max = 100)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)//password는 직렬화(serialization)되지 않는다
    @NotNull//entity에서 바로 jsonignore하지 않고 jsonproperty로 dto에서 막은 이유 : jsonignore시 deserialize도 못해 valid를 통한 검증못해서 jsonproperty로 직렬화만 막음
    @Size(min = 3, max = 100)
    private String confirm;

    @NotNull
    @Size(min = 3, max = 50)
    private String nickname;

    private Set<AuthorityDto> authorityDtoSet;

    public static UserDto from(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .email(user.getUsername())
                .nickname(user.getNickname())
                .authorityDtoSet(user.getAuthorities().stream()
                        .map(authority -> AuthorityDto.builder().authorityName(authority.getAuthorityName()).build())
                        .collect(Collectors.toSet()))
                .build();
    }
}