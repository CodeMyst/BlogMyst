package rs.myst.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.RoleConstants;
import rs.myst.backend.model.*;
import rs.myst.backend.payload.MessageResponse;
import rs.myst.backend.repositories.BlogFollowRepository;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
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
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
    public ResponseEntity<?> getFollowedBlogs() {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow();

        List<BlogFollow> followList = blogFollowRepository.findByIdUserUsername(currentUser.getUsername());

        List<Blog> blogs = followList.stream().map(BlogFollow::getBlog).collect(Collectors.toList());

        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/follow/{username}/{blog}")
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
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
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
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
    @PreAuthorize(RoleConstants.ADMIN)
    public ResponseEntity<?> patchUserRole(@PathVariable String username, @RequestBody UserRole role) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) return ResponseEntity.notFound().build();

        user.get().setRole(role);

        userRepository.save(user.get());

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{username}")
    @PreAuthorize(RoleConstants.USER_MIGHT_BANNED)
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(currentUserDetails.getUsername()).orElseThrow();

        if (currentUser.getRole() != UserRole.ADMIN) {
            if (!currentUserDetails.getUsername().equals(username)) {
                return new ResponseEntity<>(new MessageResponse("Can't delete someone else's account."), HttpStatus.UNAUTHORIZED);
            }
        }

        userRepository.deleteByUsername(username);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/ban")
    @PreAuthorize(RoleConstants.MOD)
    public ResponseEntity<?> banUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setRole(UserRole.BANNED);
        user.setBannedAt(new Timestamp(new Date().getTime()));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/unban")
    @PreAuthorize(RoleConstants.MOD)
    public ResponseEntity<?> unbanUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setRole(UserRole.USER);
        user.setBannedAt(null);

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    public void scheduledUnban() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date weekAgo = cal.getTime();

        List<User> usersToUban = userRepository.findAllByBannedAtBefore(weekAgo);

        for (User user : usersToUban) {
            user.setRole(UserRole.USER);
            user.setBannedAt(null);
            userRepository.save(user);
        }
    }
}
