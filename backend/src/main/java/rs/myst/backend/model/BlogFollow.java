package rs.myst.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "blog_follow")
public class BlogFollow {
    @EmbeddedId
    private BlogFollowId id;

    @ManyToOne
    @JoinColumn(name = "user_username", updatable = false, insertable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blog_url", updatable = false, insertable = false)
    private Blog blog;

    @JsonBackReference
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonBackReference
    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public BlogFollowId getId() {
        return id;
    }

    public void setId(BlogFollowId id) {
        this.id = id;
    }
}