package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Blog;
import rs.myst.backend.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, String> {
    boolean existsByUrlAndBlog(String url, Blog blog);
    Optional<Post> findByUrlAndBlog(String url, Blog blog);
    void deleteByUrlAndBlog(String url, Blog blog);
    List<Post> findByBlog(Blog blog);
}