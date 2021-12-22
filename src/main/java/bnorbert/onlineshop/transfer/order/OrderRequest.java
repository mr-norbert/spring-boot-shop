package bnorbert.onlineshop.transfer.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequest {

    private Integer year;
    private Integer month;
    private Integer day;
    private Integer _year;
    private Integer _month;
    private Integer _day;


}
