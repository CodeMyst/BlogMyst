package rs.myst.backend.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BlogFollowId implements Serializable {
    @Serial
    private static final long serialVersionUID = -4127826993006652740L;

    @Column(name = "user_username", nullable = false, length = 20)
    private String userUsername;

    @Column(name = "blog_url", nullable = false, length = 20)
    private String blogUrl;

    public String getBlogUrl() {
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl) {
        this.blogUrl = blogUrl;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userUsername, blogUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BlogFollowId entity = (BlogFollowId) o;
        return Objects.equals(this.userUsername, entity.userUsername) &&
                Objects.equals(this.blogUrl, entity.blogUrl);
    }
}