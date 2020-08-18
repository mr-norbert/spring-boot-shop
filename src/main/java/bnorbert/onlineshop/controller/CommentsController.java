package bnorbert.onlineshop.controller;


import bnorbert.onlineshop.service.CommentService;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;

@CrossOrigin
@RestController
@RequestMapping("/comments")
public class CommentsController {
    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto request) {
        commentService.saveComment(request);
        return new ResponseEntity<>(CREATED);
    }


}
