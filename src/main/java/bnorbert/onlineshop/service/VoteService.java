package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Vote;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.VoteMapper;
import bnorbert.onlineshop.repository.AnswerRepository;
import bnorbert.onlineshop.repository.CommentRepository;
import bnorbert.onlineshop.repository.VoteRepository;
import bnorbert.onlineshop.transfer.vote.VoteAnswersDto;
import bnorbert.onlineshop.transfer.vote.VoteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static bnorbert.onlineshop.domain.AnswersVoteType.UPVOTE;
import static bnorbert.onlineshop.domain.CommentsVoteType.HELPFUL;


@Service
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final UserService authService;
    private final VoteMapper voteMapper;

    public VoteService(VoteRepository voteRepository, AnswerRepository answerRepository,
                       CommentRepository commentRepository, UserService authService, VoteMapper voteMapper) {
        this.voteRepository = voteRepository;
        this.answerRepository = answerRepository;
        this.commentRepository = commentRepository;
        this.authService = authService;
        this.voteMapper = voteMapper;
    }


    @Transactional
    public void voteComments(VoteDto request) {
        log.info("Creating vote for comments: {}", request);
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment" + request.getCommentId() + "not found"));

        if (HELPFUL.equals(request.getCommentsVoteType())) {
            comment.setVoteCount(comment.getVoteCount() + 1);
        }else {
           throw new ResourceNotFoundException("CommentsVoteType issue");
        }

        Optional<Vote> commentAndUser = voteRepository.findTopByCommentAndUserOrderByIdDesc(comment, authService.getCurrentUser());
        if (commentAndUser.isPresent() &&
                commentAndUser.get().getCommentsVoteType().equals(request.getCommentsVoteType())) {
            throw new ResourceNotFoundException("You have already voted for this comment: " + request.getCommentId());
        }

        Vote vote = voteMapper.map(request, comment, authService.getCurrentUser());
        voteRepository.save(vote);
        commentRepository.save(comment);
    }

    @Transactional
    public void voteAnswers(VoteAnswersDto request) {
        log.info("Creating vote for answers: {}", request);
        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer Not Found with ID - " + request.getAnswerId()));

        if (UPVOTE.equals(request.getAnswersVoteType())) {
            answer.setVoteCount(answer.getVoteCount() + 1);
        }else {
            answer.setVoteCount(answer.getVoteCount() - 1);
        }

        Optional<Vote> answerAndUser = voteRepository.findTopByAnswerAndUserOrderByIdDesc(answer, authService.getCurrentUser());
        if (answerAndUser.isPresent() &&
                answerAndUser.get().getAnswersVoteType().equals(request.getAnswersVoteType())) {
            throw new ResourceNotFoundException("You have already voted for this answer: " + request.getAnswerId());
        }

        Vote vote = voteMapper.map2(request, answer, authService.getCurrentUser());
        voteRepository.save(vote);
        answerRepository.save(answer);
    }


}
