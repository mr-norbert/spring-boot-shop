package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Answer;
import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.domain.Vote;
import bnorbert.onlineshop.transfer.vote.VoteAnswersRequest;
import bnorbert.onlineshop.transfer.vote.CreateVoteRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentsVoteType", source = "createVoteRequest.commentsVoteType")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "user", source = "user")
    Vote map(CreateVoteRequest createVoteRequest, Comment comment, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "answersVoteType", source = "voteAnswersRequest.answersVoteType")
    @Mapping(target = "answer", source = "answer")
    @Mapping(target = "user", source = "user")
    Vote map2(VoteAnswersRequest voteAnswersRequest, Answer answer, User user);
}
