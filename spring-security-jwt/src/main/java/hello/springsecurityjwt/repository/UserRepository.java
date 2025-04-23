package hello.springsecurityjwt.repository;

import hello.springsecurityjwt.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByUsername(String username);

  // username을 받아 DB 테이블에서 회원을 조회
  User findByUsername(String username);
}
