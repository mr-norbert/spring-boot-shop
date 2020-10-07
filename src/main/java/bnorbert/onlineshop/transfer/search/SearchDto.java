package bnorbert.onlineshop.transfer.search;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Data

public class SearchDto {

    @NotBlank
    private String searchWords;
}
