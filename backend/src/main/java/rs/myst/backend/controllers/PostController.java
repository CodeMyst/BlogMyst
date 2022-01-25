package rs.myst.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.Blog;
import rs.myst.backend.model.BlogFollow;
import rs.myst.backend.model.Post;
import rs.myst.backend.model.User;
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

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
}
