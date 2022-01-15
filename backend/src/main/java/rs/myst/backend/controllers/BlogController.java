package rs.myst.backend.controllers;

import com.github.slugify.Slugify;
import org.springframework.beans.BeanUtils;
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

import javax.transaction.Transactional;
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
    public ResponseEntity<?> getBlog(@PathVariable String author, @PathVariable String url) {
        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(url, author);

        if (blog.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(blog.get(), HttpStatus.OK);
    }

    @PatchMapping("/{author}/{url}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> editBlog(@PathVariable String author, @PathVariable String url, @Valid @RequestBody BlogCreateInfo createInfo) {
        Optional<User> user = userRepository.findByUsername(author);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getUsername().equals(author)) {
            return new ResponseEntity<>(new MessageResponse("Can't modify a blog for a different user."), HttpStatus.UNAUTHORIZED);
        }

        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(url, author);

        if (blog.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Blog newBlog = new Blog();
        BeanUtils.copyProperties(blog.get(), newBlog, Blog.class);

        newBlog.setName(createInfo.getName());
        newBlog.setDescription(createInfo.getDescription());

        return ResponseEntity.ok(blogRepository.save(newBlog));
    }

    @PostMapping("/{author}/{url}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> createPost(@PathVariable String author, @PathVariable String url, @Valid @RequestBody PostCreateInfo createInfo) {
        Slugify slugify = new Slugify();
        String postUrl = slugify.slugify(createInfo.getTitle());

        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(url, author);

        if (blog.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if (postRepository.existsByUrlAndBlog(postUrl, blog.get())) {
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

        Post post = new Post();
        post.setBlog(blog.get());
        post.setContent(createInfo.getContent());
        post.setUrl(postUrl);
        post.setCreatedAt(new Timestamp(new Date().getTime()));
        post.setTitle(createInfo.getTitle());

        return ResponseEntity.ok(postRepository.save(post));
    }

    @PatchMapping("/{author}/{blog}/{post}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    public ResponseEntity<?> editPost(@PathVariable String author, @PathVariable("blog") String blogUrl, @PathVariable("post") String postUrl, @Valid @RequestBody PostCreateInfo createInfo) {
        var res = verifyPostUpdate(author, blogUrl, postUrl);
        if (res.isPresent()) return res.get();

        Blog blog = blogRepository.findByUrlAndAuthorUsername(blogUrl, author).orElseThrow();
        Post post = postRepository.findByUrlAndBlog(postUrl, blog).orElseThrow();

        Post newPost = new Post();
        BeanUtils.copyProperties(post, newPost, Post.class);

        newPost.setContent(createInfo.getContent());
        newPost.setTitle(createInfo.getTitle());
        newPost.setLastEdit(new Timestamp(new Date().getTime()));

        return ResponseEntity.ok(postRepository.save(newPost));
    }

    @DeleteMapping("/{author}/{blog}/{post}")
    @PreAuthorize(AuthConstants.USER_AUTH)
    @Transactional
    public ResponseEntity<?> deletePost(@PathVariable String author, @PathVariable("blog") String blogUrl, @PathVariable("post") String postUrl) {
        var res = verifyPostUpdate(author, blogUrl, postUrl);
        if (res.isPresent()) return res.get();

        Blog blog = blogRepository.findByUrlAndAuthorUsername(blogUrl, author).orElseThrow();
        postRepository.deleteByUrlAndBlog(postUrl, blog);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{author}")
    public ResponseEntity<?> getBlogs(@PathVariable String author) {
        return ResponseEntity.ok(blogRepository.findByAuthorUsername(author));
    }

    @GetMapping("/{author}/{blogUrl}/{post}")
    public ResponseEntity<?> getPost(@PathVariable String author, @PathVariable String blogUrl, @PathVariable String post) {
        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(blogUrl, author);

        if (blog.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Post> res = postRepository.findByUrlAndBlog(post, blog.get());

        if (res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/{author}/{blogUrl}/posts")
    public ResponseEntity<?> getBlogPosts(@PathVariable String author, @PathVariable String blogUrl) {
        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(blogUrl, author);

        if (blog.isEmpty()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(postRepository.findByBlog(blog.get()));
    }

    private Optional<ResponseEntity<?>> verifyPostUpdate(String author, String blogUrl, String postUrl) {
        Optional<Blog> blog = blogRepository.findByUrlAndAuthorUsername(blogUrl, author);

        if (blog.isEmpty()) return Optional.of(ResponseEntity.notFound().build());

        if (!postRepository.existsByUrlAndBlog(postUrl, blog.get())) {
            return Optional.of(ResponseEntity.notFound().build());
        }

        Optional<User> user = userRepository.findByUsername(author);

        if (user.isEmpty()) {
            return Optional.of(ResponseEntity.notFound().build());
        }

        UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!currentUser.getUsername().equals(author)) {
            return Optional.of(new ResponseEntity<>(new MessageResponse("Can't modify a blog post of a different user."), HttpStatus.UNAUTHORIZED));
        }

        return Optional.empty();
    }
}
