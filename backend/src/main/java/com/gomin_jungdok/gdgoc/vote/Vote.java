package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "vote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private VoteOption voteOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //TODO 추후 voteUser가 아니라 user_id로 바꿔도 괜찮을듯
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_user", nullable = false)
    private User voteUser;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
