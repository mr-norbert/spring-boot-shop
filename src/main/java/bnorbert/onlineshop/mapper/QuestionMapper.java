package bnorbert.onlineshop.mapper;

import bnorbert.onlineshop.domain.Product;
import bnorbert.onlineshop.domain.Question;
import bnorbert.onlineshop.domain.User;
import bnorbert.onlineshop.transfer.questionsAndAnswers.CreateQuestionRequest;
import bnorbert.onlineshop.transfer.questionsAndAnswers.QuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "text", source = "createQuestionRequest.text")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "user", source = "user")
    public abstract Question map(CreateQuestionRequest createQuestionRequest, Product product, User user);


    @Mapping(target = "id", source = "id")
    @Mapping(target = "text", source = "question.text")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "createdDate", source = "question.createdDate")
    public abstract QuestionResponse mapToQuestionResponse(Question question);
}
