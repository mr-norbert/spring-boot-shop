package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.service.CommentService;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/getCommentsByReview")
    public ResponseEntity<Page<CommentResponse>> getCommentsByReview(
            Long review_id, Pageable pageable) {
        Page<CommentResponse> comments = commentService.getCommentsByReview(review_id, pageable);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/getCommentsForReview/{review_id}")
    public ResponseEntity<Page<CommentResponse>> getCommentsForReview(
            @PathVariable("review_id") Long review_id,
            @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(commentService.getCommentsForReview(review_id, page.orElse(0)), HttpStatus.OK);
    }

    @GetMapping("/getCommentsByUserEmail/{email}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByUserEmail(
            @PathVariable("email") String email,
            @RequestParam Optional<Integer> page) {
        return new ResponseEntity<>(commentService.getCommentsByUserEmail(email, page.orElse(0)), HttpStatus.OK);
    }

}
