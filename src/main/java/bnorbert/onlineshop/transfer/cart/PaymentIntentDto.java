package bnorbert.onlineshop.transfer.cart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PaymentIntentDto {

    public enum Currency{
        USD, EUR;
    }

    private Currency currency;

    @Override
    public String toString() {
        return "PaymentIntentDto{" +
                "currency=" + currency +
                '}';
    }
}
