package rs.myst.backend.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.Blog;
import rs.myst.backend.model.BlogFollow;
import rs.myst.backend.model.Post;
import rs.myst.backend.model.User;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.PostRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository, BlogRepository blogRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
    }

    @GetMapping("/all/{page}")
    public ResponseEntity<?> getAll(@PathVariable int page) {
        return ResponseEntity.ok(postRepository.findAll(PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }

    @GetMapping("/followed/{page}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> getFollowed(@PathVariable int page) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.username()).orElseThrow();

        Set<Blog> blogs = currentUser.getFollows().stream().map(BlogFollow::getBlog).collect(Collectors.toSet());

        Page<Post> posts = postRepository.findByBlogIn(blogs, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "createdAt")));

        return ResponseEntity.ok(posts);
    }

    @PostMapping("/{author}/{blog}/{post}/upvote")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> postUpvote(@PathVariable String author, @PathVariable String blog, @PathVariable String post) {
        return upvote(author, blog, post, true);
    }

    @PostMapping("/{author}/{blog}/{post}/downvote")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> postDownvote(@PathVariable String author, @PathVariable String blog, @PathVariable String post) {
        return upvote(author, blog, post, false);
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
}
