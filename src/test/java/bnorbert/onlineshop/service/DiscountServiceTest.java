package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Discount;
import bnorbert.onlineshop.mapper.DiscountMapper;
import bnorbert.onlineshop.repository.DiscountRepository;
import bnorbert.onlineshop.transfer.cart.DiscountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class DiscountServiceTest {

    @Mock
    private DiscountRepository mockDiscountRepository;
    @Mock
    private DiscountMapper mockDiscountMapper;

    private DiscountService discountServiceUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
        discountServiceUnderTest = new DiscountService(mockDiscountRepository, mockDiscountMapper);
    }

    @Test
    void testSave() {

        final DiscountDto request = new DiscountDto();
        request.setId("summersale20");
        request.setCreatedDate(Instant.now());
        request.setExpirationDate(Instant.now().plus(10, ChronoUnit.DAYS));
        request.setPercentOff(0.05);

        when(mockDiscountRepository.save(any(Discount.class))).thenReturn(new Discount());
        when(mockDiscountMapper.map(any(DiscountDto.class))).thenReturn(new Discount());

        final Discount result = discountServiceUnderTest.save(request);
    }

    @Test
    void testGetDiscount() {

        when(mockDiscountRepository.findById("summersale20")).thenReturn(Optional.of(new Discount()));

        final Discount result = discountServiceUnderTest.getDiscount("summersale20");
    }

    @Test
    void testDeleteDiscount() {

        discountServiceUnderTest.deleteDiscount("s20");

        verify(mockDiscountRepository).deleteById("s20");
    }
}
