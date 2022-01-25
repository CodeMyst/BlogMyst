package rs.myst.backend.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "blog_follow")
public class BlogFollow {
    @EmbeddedId
    private BlogFollowId id;

    public BlogFollowId getId() {
        return id;
    }

    public void setId(BlogFollowId id) {
        this.id = id;
    }
}