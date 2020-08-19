package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.comment.CommentResponse;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "review", source = "review")
    @Mapping(target = "user", source = "user")
    public abstract Comment map(CommentsDto commentsDto, Review review, User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "reviewId", source = "review.id")
    public abstract CommentResponse mapToDto(Comment comment);

    public abstract List<CommentResponse> entitiesToEntityDTOs(List<Comment> comments);

}