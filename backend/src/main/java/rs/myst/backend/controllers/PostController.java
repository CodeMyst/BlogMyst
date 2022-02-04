package rs.myst.backend.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.RoleConstants;
import rs.myst.backend.model.*;
import rs.myst.backend.payload.MessageResponse;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.CommentRepository;
import rs.myst.backend.repositories.PostRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository, BlogRepository blogRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<?> getAll(@PathVariable int page) {
        return ResponseEntity.ok(postRepository.findAll(PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/followed/{page}")
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
    public ResponseEntity<?> getFollowed(@PathVariable int page) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.username()).orElseThrow();

        Set<Blog> blogs = currentUser.getFollows().stream().map(BlogFollow::getBlog).collect(Collectors.toSet());

        Page<Post> posts = postRepository.findByBlogIn(blogs, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt")));

        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{author}/{blog}/{post}/upvote")
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
    public ResponseEntity<?> postUpvote(@PathVariable String author, @PathVariable String blog, @PathVariable String post) {
        return upvote(author, blog, post, true);
    }

    @PostMapping("/{author}/{blog}/{post}/downvote")
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
    public ResponseEntity<?> postDownvote(@PathVariable String author, @PathVariable String blog, @PathVariable String post) {
        return upvote(author, blog, post, false);
    }

    @PostMapping("/{author}/{blog}/{post}/comment")
    @PreAuthorize(RoleConstants.USER_NOT_BANNED)
    public ResponseEntity<?> postComment(@PathVariable String author, @PathVariable String blog, @PathVariable String post, @RequestBody String comment) {
        Optional<Blog> blogRes = blogRepository.findByUrlAndAuthorUsername(blog, author);

        if (blogRes.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Post> res = postRepository.findByUrlAndBlog(post, blogRes.get());

        if (res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.username()).orElseThrow();

        Comment commentObj = new Comment();
        commentObj.setContent(comment);
        commentObj.setCreatedAt(new Timestamp(new Date().getTime()));
        commentObj.setPost(res.get());
        commentObj.setAuthor(currentUser);

        return ResponseEntity.ok(commentRepository.save(commentObj));
    }

    @GetMapping("/{author}/{blog}/{post}/comments/{page}")
    public ResponseEntity<?> getComments(@PathVariable String author, @PathVariable String blog, @PathVariable String post, @PathVariable int page) {
        Optional<Blog> blogRes = blogRepository.findByUrlAndAuthorUsername(blog, author);

        if (blogRes.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Post> res = postRepository.findByUrlAndBlog(post, blogRes.get());

        if (res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(commentRepository.findAllByPost(res.get(), PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @DeleteMapping("/comment/{id}")
    @PreAuthorize(RoleConstants.USER_NOT_BANNED)
    public ResponseEntity<?> deleteComment(@PathVariable int id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) return ResponseEntity.notFound().build();

        User currentUser = userRepository.findByUsername(userDetails.username()).orElseThrow();

        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.MOD) {
            if (!comment.get().getAuthor().getUsername().equals(userDetails.getUsername())) {
                return new ResponseEntity<>(new MessageResponse("Can't modify someone else's comment."), HttpStatus.UNAUTHORIZED);
            }
        }

        commentRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/comment/{id}")
    @PreAuthorize(RoleConstants.USER_NOT_BANNED)
    public ResponseEntity<?> editComment(@PathVariable int id, @RequestBody String content) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Comment> comment = commentRepository.findById(id);

        if (comment.isEmpty()) return ResponseEntity.notFound().build();

        if (!comment.get().getAuthor().getUsername().equals(userDetails.getUsername())) {
            return new ResponseEntity<>(new MessageResponse("Can't modify someone else's comment."), HttpStatus.UNAUTHORIZED);
        }

        Comment newComment = new Comment();
        BeanUtils.copyProperties(comment.get(), newComment, Comment.class);

        newComment.setContent(content);
        newComment.setLastEdit(new Timestamp(new Date().getTime()));

        commentRepository.save(newComment);

        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> upvote(String author, String blog, String post, boolean upvote) {
        Optional<Blog> blogRes = blogRepository.findByUrlAndAuthorUsername(blog, author);

        if (blogRes.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Post> res = postRepository.findByUrlAndBlog(post, blogRes.get());

        if (res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post newPost = new Post();
        BeanUtils.copyProperties(res.get(), newPost, Post.class);

        newPost.setUpvotes(newPost.getUpvotes() + (upvote ? 1 : -1));

        postRepository.save(newPost);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{search}")
    public ResponseEntity<?> searchPosts(@PathVariable String search) {
        return ResponseEntity.ok(postRepository.findByTitleContainingIgnoreCase(search));
    }
}
