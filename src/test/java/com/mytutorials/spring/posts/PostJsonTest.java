package com.mytutorials.spring.posts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PostJsonTest {

    @Autowired
    private JacksonTester<Post> jacksonTester;

    @Test
    void shouldSerializePost() throws IOException {
        Post post = new Post(1, 1, "Hello, World!", "This is my first post.", null);
        String expected = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        assertThat(jacksonTester.write(post)).isEqualToJson(expected);
    }

    @Test
    void shouldDeSerializePost() throws IOException {
        Post post = new Post(1, 1, "Hello, World!", "This is my first post.", null);
        String content = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        assertThat(jacksonTester.parseObject(content)).isEqualTo(post);
        assertThat(jacksonTester.parseObject(content).id()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).userId()).isEqualTo(1);
        assertThat(jacksonTester.parseObject(content).title()).isEqualTo("Hello, World!");
        assertThat(jacksonTester.parseObject(content).body()).isEqualTo("This is my first post.");
    }
}
