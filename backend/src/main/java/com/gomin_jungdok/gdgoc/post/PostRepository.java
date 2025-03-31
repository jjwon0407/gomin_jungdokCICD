package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE (:lastId IS NULL OR p.id < :lastId) AND DATE(p.createdAt) = CURRENT_DATE ORDER BY p.id DESC")
    List<Post> findPostsAfterId(@Param("lastId") Long lastId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (:lastId IS NULL OR p.id < :lastId) " +
            "AND p.postCategory IN :category " +
            "AND DATE(p.createdAt) = CURRENT_DATE " +
            "ORDER BY p.id DESC")
    List<Post> findPostsByCategoryAfterId(@Param("category") List<PostCategory> category,
                                          @Param("lastId") Long lastId,
                                          Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.todayPost = :status WHERE p.id IN :todayPosts")
    void updateTodayPostStatus(@Param("todayPosts") List<Long> todayPosts, @Param("status") boolean status);
}