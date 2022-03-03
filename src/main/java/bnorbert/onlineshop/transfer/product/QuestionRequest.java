package bnorbert.onlineshop.transfer.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class QuestionRequest {
    @NotBlank
    String question;
}
