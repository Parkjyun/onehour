package onehour.demo.repository;

import onehour.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {//jparepository를 extend함으로서 findall 또는 save같은 메서드를 기본적으로 사용할 수 있게 된다
    //entitygraph -> 쿼리가 수행될 때 lazy조회가 아니고 eager조회로 authorities정보를 같이 가져오게 된다.
    @EntityGraph(attributePaths = "authorities")//유저네임을 기준으로 유저정보를 갖고오는데 그때 권한정보도 같이 갖고옴
    Optional<User> findOneWithAuthoritiesByUsername(String userName);//null잀수도 있는 객체를 감싸는 일종의 wrapper class
}//객체 접근시 optional<user> result로 했을 떄 result.get으로 값에 접근 result.orElse(null)은 만약 값이 있으면 반환 없으면 null울 반환
