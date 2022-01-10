package rs.myst.backend.controllers;

import com.github.slugify.Slugify;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.Blog;
import rs.myst.backend.model.User;
import rs.myst.backend.payload.BlogCreateInfo;
import rs.myst.backend.payload.MessageResponse;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    public BlogController(BlogRepository blogRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> createBlog(@Valid @RequestBody BlogCreateInfo createInfo) {
        Slugify slugify = new Slugify();
        String url = slugify.slugify(createInfo.getName());

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        if (blogRepository.existsByUrlAndAuthorUsername(url, user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("A blog with the same name already exists on this account."));
        }

        Blog blog = new Blog();
        blog.setAuthor(user);
        blog.setDescription(createInfo.getDescription());
        blog.setName(createInfo.getName());
        blog.setUrl(url);

        return new ResponseEntity<>(blogRepository.save(blog), HttpStatus.OK);
    }

    @GetMapping("/{author}/{url}")
    public ResponseEntity<?> getBlog(@PathVariable("author") String author, @PathVariable("url") String url) {
        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(url, author);

        if (blog.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(blog.get(), HttpStatus.OK);
    }

    @GetMapping("/{author}")
    public ResponseEntity<?> getBlogs(@PathVariable("author") String author) {
        return ResponseEntity.ok(blogRepository.findByAuthorUsername(author));
    }
}
