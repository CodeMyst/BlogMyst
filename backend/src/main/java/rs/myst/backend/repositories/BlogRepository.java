package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Blog;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer> {
    boolean existsByUrlAndAuthorUsername(String url, String author);
    Optional<Blog> findByUrlAndAuthorUsername(String url, String author);
    List<Blog> findByAuthorUsername(String author);
    void deleteByUrlAndAuthorUsername(String url, String author);
}