package rs.myst.backend.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

public class BlogFollowPK implements Serializable {
    @ManyToOne
    @JoinColumn(name = "user_username", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blog_url", referencedColumnName = "url", nullable = false)
    private Blog blog;
}
