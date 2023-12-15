package com.mytutorials.spring.posts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class PostControllerIntTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0");
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void connectionEstablished() {
        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
    }

    @Test
    void shouldFindAllPosts() {
        // /api/posts
        Post[] posts = restTemplate.getForObject("/api/posts", Post[].class);
        assertThat(posts.length).isGreaterThan(99);
    }

    @Test
    void shouldThrowNotFoundWhenInvalidPostId() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/666", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void shouldFindPostWhenGivenValidId() {
        // api/posts/55
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/55", HttpMethod.GET, null, Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void shouldNotCreateNewPostsWhenValidationFails() {
        // "The title and Body are marked with @NotEmpty => Should not be empty"
        Post post = new Post(101, 1, "", "", null);
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Rollback
    void shouldCreateNewPostWhenPostIsValid() {
        Post post = new Post(101, 1, "101 AI", "Everything related to AI is here", null);

        ResponseEntity<Post> response = restTemplate.exchange("/api/posts", HttpMethod.POST, new HttpEntity<>(post), Post.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).id()).isEqualTo(101);
        assertThat(response.getBody().userId()).isEqualTo(1);
        assertThat(response.getBody().title()).isEqualTo("101 AI");
        assertThat(response.getBody().body()).isEqualTo("Everything related to AI is here");
    }

    @Test
    @Rollback
    void shouldUpdatePostWhenPostIsValid() {
        ResponseEntity<Post> response = restTemplate.exchange("/api/posts/99", HttpMethod.GET, null, Post.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Post existing = response.getBody();
        assertThat(existing).isNotNull();

        Post toBeUpdated = new Post(
                existing.id(),
                existing.userId(),
                "New Post Title UPDATED",
                "New Post BODY UPDATED",
                existing.version());

        ResponseEntity<Post> updatedResponse = restTemplate.exchange("/api/posts/99",
                HttpMethod.PUT, new HttpEntity<>(toBeUpdated), Post.class);

        Post updated = updatedResponse.getBody();
        System.out.println(updated);
        assertThat(updated).isNotNull();

        assertThat(toBeUpdated.id()).isEqualTo(99);
        assertThat(toBeUpdated.userId()).isEqualTo(10);
        assertThat(toBeUpdated.title()).isEqualTo(updated.title());
        assertThat(toBeUpdated.body()).isEqualTo(updated.body());
    }

    @Test
    @Rollback
    void shouldDeleteWithValidID() {
        ResponseEntity<Void> response = restTemplate.exchange("/api/posts/88", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
