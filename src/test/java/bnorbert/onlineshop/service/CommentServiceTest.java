package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Product;
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
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        initMocks(this);
        commentServiceUnderTest = new CommentService(mockReviewRepository, mockAuthService, mockCommentMapper, mockCommentRepository, mockUserRepository);
    }

    @Test
    void testSaveComment() {
        final CommentsDto request = new CommentsDto();
        request.setReviewId(1L);
        request.setText("text");

        final Review review1 = new Review();
        review1.setId(1L);
        review1.setRating(5);
        review1.setIntent("intent");
        review1.setContent("content");
        review1.setUser(new User());
        review1.setProduct(new Product());
        review1.setCreatedDate(Instant.ofEpochMilli(0L));

        final Optional<Review> review = Optional.of(review1);
        when(mockReviewRepository.findById(1L)).thenReturn(review);

        //when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));
        when(mockCommentMapper.map(any(CommentsDto.class), eq(new Review()), eq(new User()))).thenReturn(new Comment());
        when(mockAuthService.getCurrentUser()).thenReturn(new User());
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(new Comment());


        commentServiceUnderTest.saveComment(request);
    }

    @Test
    void testGetCommentsByReview() {

        final Page<Comment> comments = new PageImpl<>(Collections.singletonList(new Comment()));
        when(mockCommentRepository.findByReview_Id(eq(1L), any(Pageable.class))).thenReturn(comments);

        when(mockCommentMapper.entitiesToEntityDTOs(Collections.singletonList(new Comment()))).thenReturn(Collections.singletonList(new CommentResponse()));

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsByReview(1L, PageRequest.of(0, 1));

    }

    @Test
    void testGetCommentsForReview() {

        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));

        final Page<Comment> comments = new PageImpl<>(Collections.singletonList(new Comment()));
        when(mockCommentRepository.findByReview(new Review(), PageRequest.of(0, 8))).thenReturn(comments);

        when(mockCommentMapper.mapToDto(any(Comment.class))).thenReturn(new CommentResponse());

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsForReview(1L, 0);
    }


    @Test
    void testGetCommentsByUserEmail() {
        when(mockUserRepository.findByEmail("email")).thenReturn(Optional.of(new User()));

        final Page<Comment> comments = new PageImpl<>(Collections.singletonList(new Comment()));
        when(mockCommentRepository.findByUser(new User(), PageRequest.of(0, 8))).thenReturn(comments);

        when(mockCommentMapper.mapToDto(any(Comment.class))).thenReturn(new CommentResponse());

        final Page<CommentResponse> result = commentServiceUnderTest.getCommentsByUserEmail("email", 0);

    }
}
