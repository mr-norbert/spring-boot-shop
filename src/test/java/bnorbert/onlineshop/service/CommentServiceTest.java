package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.CommentMapper;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.repository.UserRepository;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private ReviewRepository mockReviewRepository;
    @Mock
    private UserService mockAuthService;
    @Mock
    private CommentMapper mockCommentMapper;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserRepository mockUserRepository;

    private CommentService commentServiceUnderTest;

    @BeforeEach
    void setUp() {
        commentServiceUnderTest = new CommentService(mockReviewRepository, mockAuthService, mockCommentMapper, mockCommentRepository, mockUserRepository);
    }

    @Test
    void testCreateComment() {

        CommentsDto request = new CommentsDto();
        request.setReviewId(1L);
        request.setText("text");

        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        Review review = new Review();
        review.setId(1L);
        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        when(mockCommentMapper.map(any(CommentsDto.class), eq(review), eq(user))).thenReturn(new Comment());

        when(mockAuthService.getCurrentUser()).thenReturn(user);

        when(mockCommentRepository.save(any(Comment.class))).thenReturn(new Comment());

        commentServiceUnderTest.saveComment(request);
    }



    @Test
    void testGetCommentsByReview() {

        Review review = new Review();
        review.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        Page<Comment> comments = new PageImpl<>(Collections.singletonList(comment));
        when(mockCommentRepository.findByReview_Id(eq(1L), any(Pageable.class))).thenReturn(comments);

        when(mockCommentMapper.entitiesToEntityDTOs(Collections.singletonList(comment)))
                .thenReturn(Collections.singletonList(new CommentResponse()));

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsByReview(1L, PageRequest.of(0, 8));

    }


    @Test
    void testGetCommentsForReview() {
        Review review = new Review();
        review.setId(1L);

        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Comment comment = new Comment();
        comment.setId(1L);
        Page<Comment> comments = new PageImpl<>(Collections.singletonList(comment));
        when(mockCommentRepository.findByReview(review, PageRequest.of(0, 8))).thenReturn(comments);

        when(mockCommentMapper.mapToCommentResponse(any(Comment.class))).thenReturn(new CommentResponse());

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsForReview(1L, 0);
    }


    @Test
    void testGetCommentsByUserEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        when(mockUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Comment comment = new Comment();
        comment.setId(1L);
        Page<Comment> comments = new PageImpl<>(Collections.singletonList(comment));
        when(mockCommentRepository.findByUser(any(User.class), eq(PageRequest.of(0, 8)))).thenReturn(comments);

        when(mockCommentMapper.mapToCommentResponse(any(Comment.class))).thenReturn(new CommentResponse());

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsByUserEmail(user.getEmail(), 0);
    }


}
