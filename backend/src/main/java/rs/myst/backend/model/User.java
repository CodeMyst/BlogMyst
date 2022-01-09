package rs.myst.backend.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
public class User {
    @Id
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Basic
    @Column(name = "password_hash")
    private String passwordHash;

    @OneToMany(mappedBy = "author")
    private Collection<Blog> blogs;

    @OneToMany(mappedBy = "user")
    private Collection<BlogFollow> follows;

    @OneToMany(mappedBy = "author")
    private Collection<Comment> comments;

    @OneToMany(mappedBy = "user")
    private Collection<Report> reports;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(role, user.role) && Objects.equals(passwordHash, user.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, role, passwordHash);
    }

    public Collection<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(Collection<Blog> blogs) {
        this.blogs = blogs;
    }

    public Collection<BlogFollow> getFollows() {
        return follows;
    }

    public void setFollows(Collection<BlogFollow> follows) {
        this.follows = follows;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    public Collection<Report> getReports() {
        return reports;
    }

    public void setReports(Collection<Report> reports) {
        this.reports = reports;
    }
}
