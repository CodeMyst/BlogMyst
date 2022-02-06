package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.BlogFollow;
import rs.myst.backend.model.BlogFollowId;

import java.util.List;

public interface BlogFollowRepository extends JpaRepository<BlogFollow, BlogFollowId> {
    List<BlogFollow> findByIdUserUsername(String username);
}