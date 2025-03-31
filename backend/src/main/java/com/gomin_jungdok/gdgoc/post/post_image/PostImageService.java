package com.gomin_jungdok.gdgoc.post.post_image;

import com.gomin_jungdok.gdgoc.post.Post;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PostImageService {
    private final Storage storage;
    private final PostImageRepository postImageRepository;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public PostImageService(PostImageRepository postImageRepository) {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.postImageRepository = postImageRepository;
    }

    public List<PostImage> uploadPostImages(List<MultipartFile> files, Post post) throws IOException {
        List<PostImage> postImages = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            int orderSequence = 1;
            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                BlobId blobId = BlobId.of(bucketName, fileName);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
                storage.create(blobInfo, file.getBytes());

                String imageUrl = "https://storage.googleapis.com/" + bucketName + "/" + fileName;

                PostImage postImage = new PostImage(null, post, imageUrl, orderSequence++);
                postImages.add(postImage);
            }
            postImageRepository.saveAll(postImages);
        }
        return postImages;
    }
}