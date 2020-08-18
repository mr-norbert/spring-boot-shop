package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.CommentMapper;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CommentService {

    private final ReviewRepository reviewRepository;
    private final UserService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentService(ReviewRepository reviewRepository, UserService authService,
                          CommentMapper commentMapper, CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.authService = authService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }


    @Transactional
    public void saveComment(CommentsDto request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review " + request.getReviewId() + "id not found"));
        Comment comment = commentMapper.map(request, review, authService.getCurrentUser());
        commentRepository.save(comment);

    }
}
