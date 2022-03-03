package bnorbert.onlineshop.transfer.product;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BinderRequest {
    @NotNull
    String name;
    @NotNull
    double percentage;
}
