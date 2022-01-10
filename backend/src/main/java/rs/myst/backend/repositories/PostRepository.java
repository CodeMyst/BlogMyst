package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Post;

public interface PostRepository extends JpaRepository<Post, String> {
    boolean existsByUrlAndBlogUrl(String url, String blogUrl);
}