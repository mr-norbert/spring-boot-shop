package bnorbert.onlineshop.service;

import bnorbert.onlineshop.domain.Brand;
import bnorbert.onlineshop.exception.ResourceNotFoundException;
import bnorbert.onlineshop.mapper.BrandMapper;
import bnorbert.onlineshop.repository.BrandRepository;
import bnorbert.onlineshop.transfer.brand.BrandDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    public void save(BrandDto request) {
        brandRepository.save(brandMapper.map(request));
    }

    public Brand getBrand(long id){
        log.info("Retrieving brand {}", id);
        return brandRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Brand: " + id + "not found"));
    }
}
