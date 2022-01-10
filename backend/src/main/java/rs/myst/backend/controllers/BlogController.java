package rs.myst.backend.controllers;

import com.github.slugify.Slugify;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rs.myst.backend.constants.AuthConstants;
import rs.myst.backend.model.Blog;
import rs.myst.backend.model.Post;
import rs.myst.backend.model.User;
import rs.myst.backend.payload.BlogCreateInfo;
import rs.myst.backend.payload.MessageResponse;
import rs.myst.backend.payload.PostCreateInfo;
import rs.myst.backend.repositories.BlogRepository;
import rs.myst.backend.repositories.PostRepository;
import rs.myst.backend.repositories.UserRepository;
import rs.myst.backend.services.UserDetailsImpl;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog")
public class BlogController {
    private final BlogRepository blogRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public BlogController(BlogRepository blogRepository, PostRepository postRepository, UserRepository userRepository) {
        this.blogRepository = blogRepository;
        this.postRepository = postRepository;
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

    @PostMapping("/{author}/{url}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> createPost(@PathVariable("author") String author, @PathVariable("url") String url, @Valid @RequestBody PostCreateInfo createInfo) {
        Slugify slugify = new Slugify();
        String postUrl = slugify.slugify(createInfo.getTitle());

        if (postRepository.existsByUrlAndBlogUrl(postUrl, url)) {
            return ResponseEntity.badRequest().body(new MessageResponse("A post with the same title already exists on this blog."));
        }

        Optional<User> user = userRepository.findByUsername(author);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getUsername().equals(author)) {
            return new ResponseEntity<>(new MessageResponse("Can't create a blog post for a different user."), HttpStatus.UNAUTHORIZED);
        }

        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(url, author);

        if (blog.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Post post = new Post();
        post.setBlog(blog.get());
        post.setContent(createInfo.getContent());
        post.setUrl(postUrl);
        post.setCreatedAt(new Timestamp(new Date().getTime()));
        post.setTitle(createInfo.getTitle());

        return ResponseEntity.ok(postRepository.save(post));
    }

    @GetMapping("/{author}")
    public ResponseEntity<?> getBlogs(@PathVariable("author") String author) {
        return ResponseEntity.ok(blogRepository.findByAuthorUsername(author));
    }

    @GetMapping("/{author}/{blog}/{post}")
    public ResponseEntity<?> getPost(@PathVariable("author") String ignoredAuthor, @PathVariable("blog") String blog, @PathVariable("post") String post) {
        Optional<Post> res = postRepository.findByUrlAndBlogUrl(post, blog);

        if (res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(res);
    }
}
