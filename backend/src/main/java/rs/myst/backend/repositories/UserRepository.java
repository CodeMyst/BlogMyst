package rs.myst.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.myst.backend.model.User;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
    List<User> findAllByBannedAtBefore(Date before);
    List<User> findAllByCreatedAtBetween(Timestamp from, Timestamp to);
}