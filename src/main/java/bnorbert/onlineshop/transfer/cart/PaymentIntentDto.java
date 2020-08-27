package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentIntentDto {

    public enum Currency{
        USD, EUR;
    }

    private Currency currency;


}
