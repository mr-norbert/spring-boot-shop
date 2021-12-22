package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CommentMapper;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CommentService {

    private final ReviewRepository reviewRepository;
    private final UserService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(ReviewRepository reviewRepository, UserService authService,
                          CommentMapper commentMapper, CommentRepository commentRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.authService = authService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public void saveComment(CommentsDto request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review " + request.getReviewId() + "id not found"));
        Comment comment = commentMapper.map(request, review, authService.getCurrentUser());
        commentRepository.save(comment);

    }

    @Transactional
    public Page<CommentResponse> getCommentsByReview(Long review_id, Pageable pageable){
        log.info("Retrieving comments");
        Page<Comment> comments = commentRepository.findByReview_Id(review_id, pageable);
        List<CommentResponse> commentResponses = commentMapper.entitiesToEntityDTOs(comments.getContent());
        return new PageImpl<>(commentResponses, pageable, comments.getTotalElements());
    }


    @Transactional
    public Page<CommentResponse> getCommentsForReview(Long review_id, Integer page) {
        log.info("Retrieving comments #2");
        Review review = reviewRepository.findById(review_id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id: " + review_id + "not found"));
        return commentRepository.findByReview(review, PageRequest.of(page, 8))
                .map(commentMapper::mapToCommentResponse);
    }

    @Transactional
    public Page<CommentResponse> getCommentsByUserEmail(String email, Integer page) {
        log.info("Retrieving comments #3");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User: " + email + "not found"));
        return commentRepository.findByUser(user, PageRequest.of(page, 8)).map(commentMapper::mapToCommentResponse);
    }

}
