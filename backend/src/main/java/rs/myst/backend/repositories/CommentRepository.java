package rs.myst.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.Comment;
import rs.myst.backend.model.Post;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllByPost(Post post, Pageable pageable);
}