package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Blog;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    boolean existsByUrlAndAuthorUsername(String url, String author);
}