package com.mytutorials.spring.posts;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

public record Post(
        @Id
        Integer id,
        Integer userId,
        @NotEmpty
        String title,
        @NotEmpty
        String body,

        // version => For Spring Data JDBC when it tries to figure out is it creation a new one or updating existing
        @Version Integer version
) {
}
