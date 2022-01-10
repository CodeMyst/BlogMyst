package rs.myst.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
public class Post {
    @Id
    @Column(name = "url")
    private String url;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    @Basic
    @Column(name = "last_edit")
    private Timestamp lastEdit;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "content")
    private String content;

    @Basic
    @Column(name = "upvotes")
    private int upvotes;

    @Basic
    @Column(name = "downvotes")
    private int downvotes;

    @OneToMany(mappedBy = "post")
    private Collection<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "blog_url", referencedColumnName = "url", nullable = false)
    private Blog blog;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Timestamp lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return upvotes == post.upvotes && downvotes == post.downvotes && Objects.equals(createdAt, post.createdAt) && Objects.equals(lastEdit, post.lastEdit) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && Objects.equals(url, post.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, createdAt, lastEdit, title, content, upvotes, downvotes);
    }

    @JsonManagedReference
    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    @JsonBackReference
    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
