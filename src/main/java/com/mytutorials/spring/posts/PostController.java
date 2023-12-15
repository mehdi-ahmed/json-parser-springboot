package com.mytutorials.spring.posts;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    List<Post> findAll() {
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new));
    }

    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @RequestBody Post post) {
        Optional<Post> existing = postRepository.findById(id);
        if (existing.isPresent()) {
            Post updatedPost = new Post(existing.get().id(),
                    existing.get().userId(),
                    post.title(),
                    post.body(),
                    existing.get().version());
            return postRepository.save(updatedPost);
        } else {
            throw new PostNotFoundException();
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Post save(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Integer id) {
        postRepository.deleteById(id);
    }
}
