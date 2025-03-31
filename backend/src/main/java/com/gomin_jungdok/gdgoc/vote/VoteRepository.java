package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.post.Post;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.vote.Vote;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v.post.id, COUNT(v.id) AS voteCount " +
            "FROM Vote v " +
            "WHERE v.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "GROUP BY v.post.id " +
            "ORDER BY voteCount DESC " +
            "LIMIT 3")
    List<Object[]> findTodayPosts(LocalDateTime startOfDay, LocalDateTime endOfDay);


    @Query("SELECT v.voteOption, COUNT(v) FROM Vote v WHERE v.post.id = :post_id GROUP BY v.voteOption")
    List<Object[]> findVoteResults(@Param("post_id") long post_id);

    long countByVoteOption(VoteOption option);

    Long countByVoteOptionId(Long id);

    int countByPostId(Long postId);

    int countByPostIdAndVoteOptionOrder(Long postId, int i);

    boolean existsByVoteUserAndPostId(User voteUser, Long postId);

}