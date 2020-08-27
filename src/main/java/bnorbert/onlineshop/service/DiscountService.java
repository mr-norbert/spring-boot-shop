package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Discount;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.DiscountMapper;
import bnorbert.onlineshop.repository.DiscountRepository;
import bnorbert.onlineshop.transfer.cart.DiscountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    public DiscountService(DiscountRepository discountRepository, DiscountMapper discountMapper) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
    }

    public Discount save(DiscountDto request) {
        log.info("Creating discount: {}", request);
        return discountRepository.save(discountMapper.map(request));
    }

    public Discount getDiscount(String id){
        log.info("Retrieving discount {}", id);
        return discountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Discount" + id + "not found"));
    }

    public void deleteDiscount(String id){
        log.info("Deleting discount {}", id);
        discountRepository.deleteById(id);
    }

}
