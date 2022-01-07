package rs.myst.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.myst.backend.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}