package bnorbert.onlineshop.controller;

import bnorbert.onlineshop.domain.Discount;
import bnorbert.onlineshop.service.DiscountService;
import bnorbert.onlineshop.transfer.cart.DiscountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class DiscountControllerTest {

    @Mock
    private DiscountService mockDiscountService;

    private DiscountController discountControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        discountControllerUnderTest = new DiscountController(mockDiscountService);
    }

    @Test
    void testCreateDiscount() {
        final DiscountDto request = new DiscountDto();

        when(mockDiscountService.save(any(DiscountDto.class))).thenReturn(new Discount());

        final ResponseEntity<Discount> result = discountControllerUnderTest.createDiscount(request);
    }

    @Test
    void testDeleteDiscount() {
        final ResponseEntity<Void> result = discountControllerUnderTest.deleteDiscount("summersale20");

        verify(mockDiscountService).deleteDiscount("summersale20");
    }
}
