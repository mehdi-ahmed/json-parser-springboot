package com.mytutorials.spring.posts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.StringTemplate.STR;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1, 1, "Hey Folks", "This is my first Test With TestContainers", null),
                new Post(2, 1, "Hey Folks TWO", "This is my second Test With TestContainers", null)
        );
    }

    @Test
    void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                 [
                                    {
                                        "id":1,
                                        "userId":1,
                                        "title":"Hey Folks",
                                        "body":"This is my first Test With TestContainers",
                                        "version": null
                                    },
                                    {
                                        "id":2,
                                        "userId":1,
                                        "title":"Hey Folks TWO",
                                        "body":"This is my second Test With TestContainers",
                                        "version": null
                                    }
                                ]
                """;

        when(postRepository.findAll()).thenReturn(posts);

        ResultActions resultActions = mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        JSONAssert.assertEquals(jsonResponse, resultActions.andReturn().getResponse().getContentAsString(), false);
    }

    @Test
    void shouldFindPostWhenGivenValidId() throws Exception {
        Post post = new Post(1, 1, "Test Title", "Test Body", null);
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String json = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldCreateNewPostWhenGivenValidId() throws Exception {
        Post post = new Post(3, 1, "Test is a brand new Post", "Test BODY Yeah !!", null);
        when(postRepository.save(post)).thenReturn(post);

        String postCreatedJson = STR."""
                {
                    "id":\{post.id()},
                    "userId":\{post.userId()},
                    "title":"\{post.title()}",
                    "body":"\{post.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(postCreatedJson))
                .andExpect(status().isCreated())
                .andExpect(content().json(postCreatedJson));
    }


    @Test
    void shouldUpdateNewPostWhenGivenValidId() throws Exception {
        Post updated = new Post(1, 1, "Test is a brand new Post", "Test UPDATE BODY Yeah !!", null);

        when(postRepository.findById(updated.id())).thenReturn(Optional.of(posts.get(0)));
        when(postRepository.save(updated)).thenReturn(updated);

        String requestBody = STR."""
                {
                    "id":\{updated.id()},
                    "userId":\{updated.userId()},
                    "title":"\{updated.title()}",
                    "body":"\{updated.body()}",
                    "version": null
                }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotUpdateAndThrowNotFoundWhenGivenAnInvalidPostID() throws Exception {
        Post updated = new Post(50, 1, "Test is a brand new Post", "Test BODY UPDATE Yeah !!", null);
        when(postRepository.save(updated)).thenReturn(updated);

        String json = STR."""
                {
                    "id":\{updated.id()},
                    "userId":\{updated.userId()},
                    "title":"\{updated.title()}",
                    "body":"\{updated.body()}",
                    "version":\{updated.version()}
                }
                """;

        mockMvc.perform(put("/api/posts/999")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePostWhenGivenValidID() throws Exception {
        doNothing().when(postRepository).deleteById(1);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postRepository, times(1)).deleteById(1);
    }
}
