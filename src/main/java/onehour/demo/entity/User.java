package onehour.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity//데이터베이스의 테이블고 1대1로 매핑되는 객체
@Table(name = "`user`")//테이블명지정위해
@Getter
@Setter
@Builder//빌더패턴을 위해
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @JsonIgnore//json응답값에 포함하지 않는다는 asnnoataion
    @Id//pk
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)//자동 증가되는 pk를 가짐
    private Long userId;

    @Column(name = "username", length = 50, unique = true)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "activated")
    private boolean activated;//활성화여부

    @ManyToMany//다대다
    @JoinTable(//다대다의 관계를 일대다 다대일로 정의했다는 뜻
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}
