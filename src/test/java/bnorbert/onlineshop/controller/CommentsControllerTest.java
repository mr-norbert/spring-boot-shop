package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CommentService;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Test
    void testGetCommentsByReview() {

        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));
        when(mockCommentService.getCommentsByReview(eq(1L), any(Pageable.class))).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentsControllerUnderTest.getCommentsByReview(1L, PageRequest.of(0, 1));

    }

    @Test
    void testGetCommentsForReview() {

        final Optional<Integer> page = Optional.of(0);


        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));
        when(mockCommentService.getCommentsForReview(1L, 0)).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentsControllerUnderTest.getCommentsForReview(1L, page);

    }


    @Test
    void testGetCommentsByUserEmail() {
        final Optional<Integer> page = Optional.of(0);

        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));

        when(mockCommentService.getCommentsByUserEmail("email", 0)).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentsControllerUnderTest.getCommentsByUserEmail("email", page);

    }

}
