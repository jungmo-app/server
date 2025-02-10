package jungmo.server.domain.repository;

import jungmo.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    boolean existsByUserCode(String userCode);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserCode(String userCode);
    Optional<User> findByResetToken(String resetToken);

}
