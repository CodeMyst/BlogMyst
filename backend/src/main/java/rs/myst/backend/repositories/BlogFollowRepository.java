package rs.myst.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.myst.backend.model.BlogFollow;
import rs.myst.backend.model.BlogFollowId;

public interface BlogFollowRepository extends JpaRepository<BlogFollow, BlogFollowId> {
}