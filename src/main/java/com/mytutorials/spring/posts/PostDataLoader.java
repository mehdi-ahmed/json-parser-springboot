package com.mytutorials.spring.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PostDataLoader implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDataLoader.class);
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;
    public PostDataLoader(ObjectMapper objectMapper, PostRepository postRepository) {
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
    }
    @Override
    public void run(String... args) {
        if(postRepository.count() == 0){
            String POSTS_JSON = "/data/posts.json";
            LOGGER.info("Loading POSTS into DB from JSON : {}", POSTS_JSON);
            try(InputStream inputStream = TypeReference.class.getResourceAsStream(POSTS_JSON)){
                Posts response = objectMapper.readValue(inputStream, Posts.class);
                postRepository.saveAll(response.posts());
            } catch (IOException e){
                throw new RuntimeException("Failed to read JSON data", e);
            }
        }
    }
}