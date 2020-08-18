package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CommentService;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

class CommentsControllerTest {

    @Mock
    private CommentService mockCommentService;

    private CommentsController commentsControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        commentsControllerUnderTest = new CommentsController(mockCommentService);
    }

    @Test
    void testCreateComment() {
        final CommentsDto request = new CommentsDto();

        final ResponseEntity<Void> result = commentsControllerUnderTest.createComment(request);

        verify(mockCommentService).saveComment(any(CommentsDto.class));
    }
}
