package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Vote;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.VoteMapper;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.VoteRepository;
import bnorbert.onlineshop.transfer.vote.CreateVoteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static bnorbert.onlineshop.domain.CommentsVoteType.HELPFUL;


@Service
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final VoteMapper voteMapper;

    public VoteService(VoteRepository voteRepository, CommentRepository commentRepository, UserService userService, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.voteMapper = voteMapper;
    }


    @Transactional
    public void voteComments(CreateVoteRequest request) {
        log.info("Creating vote for comments: {}", request);
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Comment" + request.getCommentId() + "not found"));

        if (HELPFUL.equals(request.getCommentsVoteType())) {
            comment.setVoteCount(comment.getVoteCount() + 1);
        }else {
           throw new ResourceNotFoundException("CommentsVoteType issue");
        }

        Optional<Vote> commentAndUser = voteRepository.findTopByCommentAndUserOrderByIdDesc
                (comment, userService.getCurrentUser());

        if (commentAndUser.isPresent() &&
                commentAndUser
                        .get()
                        .getCommentsVoteType()
                        .equals(request.getCommentsVoteType())) {
            throw new ResourceNotFoundException
                    ("You have already voted for this comment: " + request.getCommentId());
        }

        Vote vote = voteMapper.map(request, comment, userService.getCurrentUser());
        voteRepository.save(vote);
        commentRepository.save(comment);
    }


}
