package com.gomin_jungdok.gdgoc.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_title", nullable = false, length = 255)
    private String title;

    @NotBlank(message = "상세설명은 필수입니다.")
    @Column(name = "post_desc", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "today_post", nullable = false)
    private boolean todayPost = false;

    @Column(name = "best_post", nullable = false)
    private boolean bestPost = false;

    @Column(name = "is_AI", nullable = false)
    private boolean isAI = false;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostCategory postCategory = PostCategory.ETC;
}
