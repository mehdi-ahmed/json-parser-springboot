package com.mytutorials.spring.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This will use a separate container for test.
 * The 100 Posts saved in Dev will not be part of this Test container.
 */

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    void setUp() {
        List<Post> posts = List.of(
                new Post(1, 1, "Hey Folks", "This is my first Test With TestContainers", null));
        postRepository.saveAll(posts);
    }

    @Test
    void testConnectionEstablished() {
        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnPostByTitle() {
        Optional<Post> post = postRepository.findByTitle("Hey Folks");
        assertThat(post.isPresent()).isTrue();
        assertThat(post).isNotEmpty();
    }

    @Test
    void shouldNotReturnPostWhenTitleIsNotFound() {
        Optional<Post> byTitle = postRepository.findByTitle("Non existing Title duh !!");
        assertFalse(byTitle.isPresent(), "Post should not be present !!!");
    }
}