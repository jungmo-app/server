package jungmo.server.domain.repository;

import jungmo.server.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    boolean existsByUserCode(String userCode);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.userCode LIKE CONCAT(:userCode, '%') AND u.isDeleted = false")
    List<User> findByUserCodeStartingWith(@Param("userCode") String userCode);

    Optional<User> findByResetToken(String resetToken);

}
