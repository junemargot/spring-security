package hello.sprintsecurityoauth2clientsession.repository;

import hello.sprintsecurityoauth2clientsession.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String usernaem);

}
