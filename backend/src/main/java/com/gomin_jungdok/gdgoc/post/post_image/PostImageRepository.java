package com.gomin_jungdok.gdgoc.post.post_image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gomin_jungdok.gdgoc.post.Post;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    List<PostImage> findByPostId(Long postId);
}
