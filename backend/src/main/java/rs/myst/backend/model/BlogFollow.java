package rs.myst.backend.model;

import javax.persistence.*;

@Entity
@Table(name = "blog_follow", schema = "blogmyst")
@IdClass(BlogFollowPK.class)
public class BlogFollow {
    @ManyToOne
    @Id
    @JoinColumn(name = "user_username", referencedColumnName = "username", nullable = false)
    private User user;

    @ManyToOne
    @Id
    @JoinColumn(name = "blog_url", referencedColumnName = "url", nullable = false)
    private Blog blog;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }
}
