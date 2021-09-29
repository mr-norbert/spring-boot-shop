package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CommentService;
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
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService mockCommentService;

    private CommentController commentControllerUnderTest;

    @BeforeEach
    void setUp() {

        commentControllerUnderTest = new CommentController(mockCommentService);
    }

    @Test
    void testCreateComment() {
        final CommentsDto request = new CommentsDto();

        final ResponseEntity<Void> result = commentControllerUnderTest.createComment(request);

        verify(mockCommentService).saveComment(any(CommentsDto.class));
    }

    @Test
    void testGetCommentsByReview() {

        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));
        when(mockCommentService.getCommentsByReview(eq(1L), any(Pageable.class))).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentControllerUnderTest.getCommentsByReview(1L, PageRequest.of(0, 1));

    }

    @Test
    void testGetCommentsForReview() {

        final Optional<Integer> page = Optional.of(0);


        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));
        when(mockCommentService.getCommentsForReview(1L, 0)).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentControllerUnderTest.getCommentsForReview(1L, page);

    }


    @Test
    void testGetCommentsByUserEmail() {
        final Optional<Integer> page = Optional.of(0);

        final Page<CommentResponse> commentResponses = new PageImpl<>(Collections.singletonList(new CommentResponse()));

        when(mockCommentService.getCommentsByUserEmail("email@gmail.com", 0)).thenReturn(commentResponses);

        final ResponseEntity<Page<CommentResponse>> result = commentControllerUnderTest.getCommentsByUserEmail( "email@gmail.com",page);

    }

}
