package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.mapper.CommentMapper;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.ReviewRepository;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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

    private CommentService commentServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        commentServiceUnderTest = new CommentService(mockReviewRepository, mockAuthService, mockCommentMapper, mockCommentRepository);
    }

    @Test
    void testSaveComment() {
        final CommentsDto request = new CommentsDto();
        request.setReviewId(1L);
        request.setText("text");

        when(mockReviewRepository.findById(1L)).thenReturn(Optional.of(new Review()));
        when(mockCommentMapper.map(any(CommentsDto.class), eq(new Review()), eq(new User()))).thenReturn(new Comment());
        when(mockAuthService.getCurrentUser()).thenReturn(new User());
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(new Comment());


        commentServiceUnderTest.saveComment(request);
    }
}
