package rs.myst.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.*;
import rs.myst.backend.repositories.BlogFollowRepository;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final BlogFollowRepository blogFollowRepository;

    public UserController(UserRepository userRepository, BlogRepository blogRepository, BlogFollowRepository blogFollowRepository) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.blogFollowRepository = blogFollowRepository;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(user);
    }

    @GetMapping("/following")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> getFollowedBlogs() {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow();

        List<BlogFollow> followList = blogFollowRepository.findByIdUserUsername(currentUser.getUsername());

        List<Blog> blogs = followList.stream().map(BlogFollow::getBlog).collect(Collectors.toList());

        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/follow/{username}/{blog}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> isFollowingBlog(@PathVariable String username, @PathVariable String blog) {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow();

        Optional<Blog> blogObj = blogRepository.findByUrlAndAuthorUsername(blog, username);

        if (blogObj.isEmpty()) return ResponseEntity.notFound().build();

        BlogFollowId id = new BlogFollowId();
        id.setUserUsername(currentUser.getUsername());
        id.setBlogUrl(blogObj.get().getUrl());

        if (blogFollowRepository.existsById(id)) return ResponseEntity.ok().build();

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/follow/{username}/{blog}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> toggleFollowBlog(@PathVariable String username, @PathVariable String blog) {
        if (!userRepository.existsByUsername(username)) return ResponseEntity.notFound().build();

        Optional<Blog> blogObj = blogRepository.findByUrlAndAuthorUsername(blog, username);

        if (blogObj.isEmpty()) return ResponseEntity.notFound().build();

        UserDetailsImpl currentUserDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow();

        BlogFollowId id = new BlogFollowId();
        id.setBlogUrl(blogObj.get().getUrl());
        id.setUserUsername(currentUser.getUsername());

        if (blogFollowRepository.existsById(id)) {
            blogFollowRepository.deleteById(id);
        } else {
            BlogFollow follow = new BlogFollow();
            follow.setId(id);

            blogFollowRepository.save(follow);
        }

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{username}")
    @PreAuthorize(AuthConstants.ADMIN_AUTH)
    public ResponseEntity<?> patchUserRole(@PathVariable String username, @RequestBody UserRole role) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) return ResponseEntity.notFound().build();

        user.get().setRole(role);

        userRepository.save(user.get());

        return ResponseEntity.ok().build();
    }
}
