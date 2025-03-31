package com.gomin_jungdok.gdgoc.vote_option;

import com.gomin_jungdok.gdgoc.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

    // post, order로 VoteOption 찾기
    Optional<VoteOption> findByPostAndOrder(Post post, int order);

    List<VoteOption> findByPostId(Long postId);
}
