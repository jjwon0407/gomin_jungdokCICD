package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.post.post_image.PostImage;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageService;
import com.gomin_jungdok.gdgoc.post.post_image.PostImageRepository;
import com.gomin_jungdok.gdgoc.vote_option.VoteOption;
import com.gomin_jungdok.gdgoc.vote_option.VoteOptionRepository;
import com.gomin_jungdok.gdgoc.vote.VoteResultDTO;
import com.gomin_jungdok.gdgoc.user.User;
import com.gomin_jungdok.gdgoc.user.UserRepository;
import com.gomin_jungdok.gdgoc.vote.VoteUtils;
import com.gomin_jungdok.gdgoc.vote.VoteRepository;
import com.gomin_jungdok.gdgoc.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final PostImageService postImageService;
    private final PostImageRepository postImageRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createPost(PostWriteRequestDto requestDto) throws IOException {
        Post post = new Post();
        //TODO 로그인 구현 후 token에서 userId 추출해서 setUserId에 사용하도록 수정해야함
        post.setUserId(1L);
        post.setTitle(requestDto.getTitle());
        post.setDescription(requestDto.getDescription());
        post.setPostCategory(PostCategory.fromValue(requestDto.getCategory()));

        post = postRepository.save(post);

        VoteOption option1 = new VoteOption(null, post, 1, requestDto.getOption1());
        VoteOption option2 = new VoteOption(null, post, 2, requestDto.getOption2());
        voteOptionRepository.saveAll(List.of(option1, option2));

        postImageService.uploadPostImages(requestDto.getImages(), post);
    }

    // 고민 글 상세보기
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 게시글"));

        User writer = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 작성자"));

        //TODO 로그인 구현 후 token에서 userId 추출해서 currentUserId에 사용하도록 수정해야함
        Long currentUserId = 1L;
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 접속 유저"));

        boolean isMine = (post.getUserId().equals(currentUserId));
        boolean isVoted = (voteRepository.existsByVoteUserAndPostId(currentUser, postId));

        List<VoteOption> voteOptions = voteOptionRepository.findByPostId(postId);
        Map<String, Object> voteResult = VoteUtils.calculateVoteResults(voteOptions, voteRepository, isMine || isVoted);

        List<PostImage> images = postImageRepository.findByPostId(post.getId());
        Map<String, String> imageUrls = new HashMap<>();
        for (int i = 0; i < images.size(); i++) {
            imageUrls.put("image" + (i + 1), images.get(i).getImageUrl());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String formattedDate = dateFormat.format(post.getCreatedAt());

        return new PostDetailResponseDto(
                isVoted,
                isMine,
                post.isAI(),
                post.getPostCategory().getValue(),
                writer.getProfileImage(),
                writer.getNickname(),
                formattedDate,
                post.getTitle(),
                post.getDescription(),
                imageUrls,
                (String) voteResult.get("option1Content"),
                (String) voteResult.get("option2Content"),
                (Long) voteResult.get("option1Votes"),
                (Long) voteResult.get("option2Votes"),
                (String) voteResult.get("option1Percentage"),
                (String) voteResult.get("option2Percentage")
        );
    }

    // 고민 글 조회
    public PostListResponseDto getPosts(int size, Long lastId, List<String> category) {
        Pageable pageable = PageRequest.of(0, size);

        // 카테고리가 있는 경우 해당 카테고리로 필터링한 게시글 조회
        List<Post> posts;
        if (category != null && !category.isEmpty()) {
            List<PostCategory> formattingCategory = category.stream()
                    .map(PostCategory::fromValue)
                    .toList();

            posts = postRepository.findPostsByCategoryAfterId(formattingCategory, lastId, pageable);
        } else {
            posts = postRepository.findPostsAfterId(lastId, pageable);
        }

        //TODO 로그인 구현 후 token에서 userId 추출해서 currentUserId에 사용하도록 수정해야함
        Long currentUserId = 1L;
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 접속 유저"));

        List<PostListDetailResponseDto> postListDetails = posts.stream().map(post -> {
            boolean isVoted = voteRepository.existsByVoteUserAndPostId(currentUser, post.getId());
            boolean isMine = post.getUserId().equals(currentUserId);
            boolean isAi = post.isAI();

            List<VoteOption> voteOptions = voteOptionRepository.findByPostId(post.getId());
            Map<String, Object> voteResult = VoteUtils.calculateVoteResults(voteOptions, voteRepository, isMine || isVoted);

            return new PostListDetailResponseDto(
                    post.getId(),
                    isVoted,
                    isMine,
                    isAi,
                    post.getPostCategory().getValue(),
                    post.getTitle(),
                    (String) voteResult.get("option1Content"),
                    (String) voteResult.get("option2Content"),
                    (Long) voteResult.get("option1Votes"),
                    (Long) voteResult.get("option2Votes"),
                    (String) voteResult.get("option1Percentage"),
                    (String) voteResult.get("option2Percentage")
            );
        }).collect(Collectors.toList());

        return new PostListResponseDto(size, postListDetails);
    }

    //오늘의 고민 게시글 3개 조회
    public List<TodayPostsDTO> getTodayPost() {
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        ZoneId utcZone = ZoneId.of("UTC");

        //오늘 날짜 (KST 기준)
        LocalDate todayInKorea = Instant.now().atZone(koreaZone).toLocalDate();
        //어제 날짜 (KST 기준)
        LocalDate yesterdayInKorea = todayInKorea.minusDays(1);

        //어제 23:59:00 (KST) → UTC 변환
        LocalDateTime startTime = yesterdayInKorea.atTime(23, 59).atZone(koreaZone).withZoneSameInstant(utcZone).toLocalDateTime();

        //오늘 19:00:00 (KST) → UTC 변환
        LocalDateTime endTime = todayInKorea.atTime(19, 0).atZone(koreaZone).withZoneSameInstant(utcZone).toLocalDateTime();

        System.out.println("UTC 기준 StartTime: " + startTime);
        System.out.println("UTC 기준 EndTime: " + endTime);

        System.out.println("StartTime (KST 기준): " + startTime.atZone(utcZone).withZoneSameInstant(koreaZone));
        System.out.println("EndTime (KST 기준): " + endTime.atZone(utcZone).withZoneSameInstant(koreaZone));

        List<Object[]> topVotedPosts = voteRepository.findTodayPosts(startTime, endTime);

        //선정된 게시글 ID 리스트
        List<Long> todayPosts = topVotedPosts.stream()
                .map(obj -> (long) obj[0])
                .collect(Collectors.toList());

        //today_post 값을 true로 변경
        if (!todayPosts.isEmpty()) {
            postRepository.updateTodayPostStatus(todayPosts, true);
        } else {
            System.out.println("업데이트할 게시글이 없습니다.");
        }

        //댓글 개수 조회
        List<Object[]> commentCounts = commentRepository.countCommentsByPostIds(todayPosts);
        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(obj -> (Long) obj[0], obj -> (Long) obj[1]));


        return topVotedPosts.stream()
                .map(obj -> {
                    long postId = (long) obj[0];
                    long totalVoteCount = (long) obj[1];
                    long commentCount = commentCountMap.getOrDefault(postId, 0L); //댓글 개수 적용


                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

                    //해당 게시글의 모든 옵션 가져오기
                    List<VoteOption> allOptions = voteOptionRepository.findByPostId(postId);

                    //해당 게시글의 투표된 옵션 가져오기
                    List<Object[]> voteData = voteRepository.findVoteResults(postId);

                    //옵션별 투표 수 매칭 (없는 건 0으로 설정)
                    Map<String, Long> voteCountMap = voteData.stream()
                            .collect(Collectors.toMap(
                                    v -> ((VoteOption) v[0]).getText(),
                                    v -> v[1] instanceof Number ? ((Number) v[1]).longValue() : 0
                            ));

                    long totalVotes = voteCountMap.values().stream().mapToLong(Long::longValue).sum();

                    //모든 옵션에 대해 투표 정보 생성
                    List<VoteResultDTO> voteResults = allOptions.stream()
                            .map(option -> {
                                long votes = voteCountMap.getOrDefault(option.getText(), 0L);
                                long percentage = totalVotes == 0 ? 0 : Math.round(((double) votes / totalVotes) * 100);
                                return new VoteResultDTO(option.getText(), votes, percentage);
                            })
                            .collect(Collectors.toList());

                    return new TodayPostsDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getDescription(),
                            post.getPostCategory().getValue(),
                            voteResults,
                            totalVoteCount,
                            commentCount
                    );
                })
                .collect(Collectors.toList());
    }

    //오늘의 고민 상세 조회
    public PostDetailDTO getPostDetail(long post_id) {
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + post_id));

        // 이미지 리스트 조회
        List<String> imageUrls = postImageRepository.findByPost(post)
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        // 댓글 개수 조회
        Long commentCount = commentRepository.countCommentsByPostId(post_id);

        //해당 게시글의 모든 옵션 가져오기
        List<VoteOption> allOptions = voteOptionRepository.findByPostId(post_id);

        //해당 게시글의 투표된 옵션 가져오기
        List<Object[]> voteData = voteRepository.findVoteResults(post_id);

        //옵션별 투표 수 매칭 (없는 건 0으로 설정)
        Map<String, Long> voteCountMap = voteData.stream()
                .collect(Collectors.toMap(
                        v -> ((VoteOption) v[0]).getText(),
                        v -> v[1] instanceof Number ? ((Number) v[1]).longValue() : 0
                ));

        long totalVotes = voteCountMap.values().stream().mapToLong(Long::longValue).sum();

        //모든 옵션에 대해 투표 정보 생성
        List<VoteResultDTO> voteResults = allOptions.stream()
                .map(option -> {
                    long votes = voteCountMap.getOrDefault(option.getText(), 0L);
                    long percentage = totalVotes == 0 ? 0 : Math.round(((double) votes / totalVotes) * 100);
                    return new VoteResultDTO(option.getText(), votes, percentage);
                })
                .collect(Collectors.toList());


/*
        // 댓글 조회
            List<CommentDTO> comments = commentRepository.findByPostId(post_id)
                .stream()
                .map(c -> new CommentDTO(
                        c.getUser().getUsername(),
                        c.getContent(),
                        c.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
*/
        return new PostDetailDTO(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getPostCategory().getValue(),
                imageUrls,
                voteResults,
                totalVotes,
                commentCount
                //comments
        );
    }
}
