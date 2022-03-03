package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CommentService;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin
@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto request) {
        commentService.saveComment(request);
        return new ResponseEntity<>(CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentsByReview(
            Long reviewId, Pageable pageable) {
        Page<CommentResponse> comments = commentService.getCommentsByReview(reviewId, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsForReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam (name = "page", required = false, defaultValue = "0")Integer page) {
        return new ResponseEntity<>(commentService.getCommentsForReview(reviewId, page), HttpStatus.OK);
    }

    @GetMapping("/emails/{email}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByUserEmail(
            @PathVariable("email") String email,
            @RequestParam (name = "page", required = false, defaultValue = "0")Integer page) {
        return new ResponseEntity<>(commentService.getCommentsByUserEmail(email, page), HttpStatus.OK);
    }

}
