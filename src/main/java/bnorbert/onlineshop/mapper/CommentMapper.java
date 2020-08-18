package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Comment;
import bnorbert.onlineshop.domain.Review;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.comment.CommentsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "review", source = "review")
    @Mapping(target = "user", source = "user")
    Comment map(CommentsDto commentsDto, Review review, User user);

}