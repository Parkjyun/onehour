package onehour.demo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotNull//null이면 안된다 -> validation관련 annotation
    @Size(min = 5, max = 50)//validation관련 annotation
    private String email;

    @NotNull
    @Size(min = 3, max = 100)
    private String password;
}