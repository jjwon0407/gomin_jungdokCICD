package com.gomin_jungdok.gdgoc.vote_option;

import com.gomin_jungdok.gdgoc.post.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "vote_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "option_order", nullable = false)
    private int order;

    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String text;
}
